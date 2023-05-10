package com.fuse.listener;

import cn.hutool.core.date.DateUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.fuse.config.RabbitmqConfig;
import com.fuse.config.configure.WeatherConfigure;
import com.fuse.domain.pojo.ChinaCity;
import com.fuse.domain.pojo.CityWeatherEachHour;
import com.fuse.exception.ObjectException;
import com.fuse.exception.WeatherFetchException;
import com.fuse.mapper.ChinaCityMapper;
import com.fuse.mapper.CityWeatherEachHourMapper;
import com.fuse.util.MybatisBatchUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Cra2iTeT
 * @since 2023/5/10 10:01
 */
public class AutoWeatherService {

    private final ChinaCityMapper chinaCityMapper;

    private final ThreadPoolTaskExecutor autoExecutors;

    private final WeatherConfigure weatherConfigure;

    private final RabbitTemplate rabbitTemplate;

    private final MybatisBatchUtils mybatisBatchUtils;

    public AutoWeatherService(ChinaCityMapper chinaCityMapper,
                              @Qualifier("autoExecutors") ThreadPoolTaskExecutor weatherExecutors,
                              WeatherConfigure weatherConfigure,
                              RabbitTemplate rabbitTemplate,
                              MybatisBatchUtils mybatisBatchUtils) {
        this.chinaCityMapper = chinaCityMapper;
        this.autoExecutors = weatherExecutors;
        this.weatherConfigure = weatherConfigure;
        this.rabbitTemplate = rabbitTemplate;
        this.mybatisBatchUtils = mybatisBatchUtils;
    }

    @Scheduled(fixedRate = 1000 * 60 * 60 * 22)
    public void autoWeather() {
        List<ChinaCity> chinaCities = chinaCityMapper.getFanCities();

        fetchAllWeatherFromNet(chinaCities);
    }

    private void fetchAllWeatherFromNet(List<ChinaCity> chinaCities) {
        // 分组
        int citySize = chinaCities.size();
        int size = Math.min(citySize, 6);

        for (int i = 0; i < size; i++) {
            int startIdx = citySize / size * i;
            int endIdx = i != size - 1 ? citySize / size * (i + 1) : citySize;
            List<ChinaCity> cities = chinaCities.subList(startIdx, endIdx);

            autoExecutors.execute(() -> {
                List<CityWeatherEachHour> cityWeatherEachHours = transferAndGetWeather(cities);
                try {
                    saveOrUpdateToDB(cityWeatherEachHours);
                } catch (ObjectException e) {
                    WeatherFetchException exception = new WeatherFetchException("从网络中获取天气，更新到数据库时异常", e.getMessage());
                    rabbitTemplate.convertAndSend(RabbitmqConfig.ROUTINGKEY_WEATHER_FETCH_EXCEPTION,
                            JSONUtil.toJsonStr(exception));
                }
            });
        }
    }

    private void saveOrUpdateToDB(List<CityWeatherEachHour> cityWeatherEachHours) throws ObjectException {
        mybatisBatchUtils.batch(cityWeatherEachHours, CityWeatherEachHourMapper.class,
                (cityWeatherEachHour, cityWeatherEachHourMapper) -> cityWeatherEachHourMapper
                        .saveOrUpdate(cityWeatherEachHour));
    }

    private List<CityWeatherEachHour> transferAndGetWeather(List<ChinaCity> chinaCities) {
        List<CityWeatherEachHour> cityWeatherEachHours = new ArrayList<>();
        chinaCities.forEach(chinaCity -> {
            List<CityWeatherEachHour> eachHours = fetchWeather(chinaCity);
            cityWeatherEachHours.addAll(eachHours);
        });
        return cityWeatherEachHours;
    }

    private List<CityWeatherEachHour> fetchWeather(ChinaCity chinaCity) {
        String httpsLink = weatherConfigure.getPrefix() + chinaCity.getLocationId() + "&key=" + weatherConfigure.getKey();

        String result = HttpUtil.createGet(httpsLink).execute().body();

        return weatherResolver(result, chinaCity);
    }

    private List<CityWeatherEachHour> weatherResolver(String result, ChinaCity chinaCity) {
        JSONObject jsonObject = JSONUtil.parseObj(result);
        JSONArray jsonArray = jsonObject.getJSONArray("hourly");

        List<CityWeatherEachHour> cityWeatherEachHours = new ArrayList<>();

        for (Object o : jsonArray) {
            // TODO 气象数据异常生产一条异常消息

            String str = String.valueOf(o);
            JSONObject entries = JSONUtil.parseObj(str);
            CityWeatherEachHour cityWeatherEachHour = new CityWeatherEachHour();

            cityWeatherEachHour.setLocationName(chinaCity.getLocationName());
            cityWeatherEachHour.setTime(DateUtil.parse(String.valueOf(entries.get("fxTime")), "yyyy-MM-dd'T'HH:mmXXX").getTime());
            cityWeatherEachHour.setHumidity(String.valueOf(entries.get("humidity")));
            cityWeatherEachHour.setTemperature(String.valueOf(entries.get("temp")));
            cityWeatherEachHour.setDate(DateUtil.date(cityWeatherEachHour.getTime()));
            cityWeatherEachHour.setPressure(Integer.parseInt(String.valueOf(entries.get("pressure"))));
            cityWeatherEachHour.setWindDirection(Integer.parseInt(String.valueOf(entries.get("wind360"))));
            cityWeatherEachHour.setWindSpeed(String.valueOf(entries.get("windSpeed")));
            cityWeatherEachHour.setLocationId(chinaCity.getLocationId());

            cityWeatherEachHours.add(cityWeatherEachHour);
        }

        return cityWeatherEachHours;
    }
}

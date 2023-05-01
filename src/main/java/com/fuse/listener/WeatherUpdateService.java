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
import com.fuse.exception.WeatherFetchException;
import com.fuse.mapper.ChinaCityMapper;
import com.fuse.mapper.CityWeatherEachHourMapper;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * @author Cra2iTeT
 * @since 2023/4/30 10:38
 */
@Component
public class WeatherUpdateService {

    private final ChinaCityMapper chinaCityMapper;

    private final CityWeatherEachHourMapper cityWeatherEachHourMapper;

    private final ThreadPoolTaskExecutor weatherExecutors;

    private final WeatherConfigure weatherConfigure;

    private final RabbitTemplate rabbitTemplate;

    public WeatherUpdateService(ChinaCityMapper chinaCityMapper, CityWeatherEachHourMapper cityWeatherEachHourMapper, @Qualifier("weatherExecutors") ThreadPoolTaskExecutor weatherExecutors, WeatherConfigure weatherConfigure, RabbitTemplate rabbitTemplate) {
        this.chinaCityMapper = chinaCityMapper;
        this.cityWeatherEachHourMapper = cityWeatherEachHourMapper;
        this.weatherExecutors = weatherExecutors;
        this.weatherConfigure = weatherConfigure;
        this.rabbitTemplate = rabbitTemplate;
    }

    @Scheduled(fixedRate = 1000 * 60 * 60 * 22)
    public void updateWeather() {
        List<ChinaCity> chinaCities = fetchChinaCity();

        try {
            fetchAllWeather(chinaCities);
        } catch (WeatherFetchException e) {
            rabbitTemplate.convertAndSend(RabbitmqConfig.ROUTINGKEY_WEATHER_FETCH_EXCEPTION,
                    JSONUtil.toJsonStr(e));
        }
    }

    private List<ChinaCity> fetchChinaCity() {
        return chinaCityMapper.getAllChinaCities();
    }

    private void fetchAllWeather(List<ChinaCity> chinaCities) throws WeatherFetchException {
        // 分组
        int citySize = chinaCities.size();
        int size = Math.min(citySize, 6);

        List<Future<Boolean>> futures = new ArrayList<>();

        for (int i = 0; i < size; i++) {
            int startIdx = citySize / size * i;
            int endIdx = i != size - 1 ? citySize / size * (i + 1) : citySize;
            List<ChinaCity> cities = chinaCities.subList(startIdx, endIdx);

            Future<Boolean> submit = weatherExecutors.submit(() -> {
                List<CityWeatherEachHour> cityWeatherEachHours = transferAndGetWeather(cities);
                return saveOrUpdateToDB(cityWeatherEachHours);
            });

            futures.add(submit);
        }

        for (Future<Boolean> future : futures) {
            try {
                if (!future.isDone() || !future.get()) {
                    throw new WeatherFetchException("天气数据获取异常，请与管理员联系");
                }
            } catch (InterruptedException | ExecutionException e) {
                throw new WeatherFetchException("天气数据获取异常，请与管理员联系");
            }
        }
    }

    private List<CityWeatherEachHour> transferAndGetWeather(List<ChinaCity> chinaCities) {
        List<CityWeatherEachHour> cityWeatherEachHours = new ArrayList<>();
        chinaCities.forEach(chinaCity -> {
            List<CityWeatherEachHour> eachHours = fetchWeather(chinaCity.getLocationId());
            cityWeatherEachHours.addAll(eachHours);
        });
        return cityWeatherEachHours;
    }

    private boolean saveOrUpdateToDB(List<CityWeatherEachHour> cityWeatherEachHours) {
        return cityWeatherEachHourMapper.saveOrUpdate(cityWeatherEachHours);
    }

    private List<CityWeatherEachHour> fetchWeather(String locationId) {
        String httpsLink = weatherConfigure.getPrefix() + locationId + "&key=" + weatherConfigure.getKey();

        String result = HttpUtil.createGet(httpsLink).execute().body();

        return weatherResolver(result, locationId);
    }

    private List<CityWeatherEachHour> weatherResolver(String result, String locationId) {
        JSONObject jsonObject = JSONUtil.parseObj(result);
        JSONArray jsonArray = jsonObject.getJSONArray("hourly");

        List<CityWeatherEachHour> cityWeatherEachHours = new ArrayList<>();

        for (Object o : jsonArray) {
            String str = String.valueOf(o);
            JSONObject entries = JSONUtil.parseObj(str);
            CityWeatherEachHour cityWeatherEachHour = new CityWeatherEachHour();

            cityWeatherEachHour.setTime(DateUtil.parse(String.valueOf(entries.get("fxTime")), "yyyy-MM-dd'T'HH:mmXXX").getTime());
            cityWeatherEachHour.setHumidity(Byte.parseByte(String.valueOf(entries.get("humidity"))));
            cityWeatherEachHour.setTemperature(Byte.parseByte(String.valueOf(entries.get("temp"))));
            cityWeatherEachHour.setPressure(Integer.parseInt(String.valueOf(entries.get("pressure"))));
            cityWeatherEachHour.setWindDirection(Integer.parseInt(String.valueOf(entries.get("wind360"))));
            cityWeatherEachHour.setWindSpeed(Byte.parseByte(String.valueOf(entries.get("windSpeed"))));
            cityWeatherEachHour.setLocationId(locationId);

            cityWeatherEachHours.add(cityWeatherEachHour);
        }

        return cityWeatherEachHours;
    }
}

package com.fuse.listener;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.text.csv.CsvUtil;
import cn.hutool.core.text.csv.CsvWriter;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.fuse.config.RabbitmqConfig;
import com.fuse.config.SystemConfig;
import com.fuse.config.configure.WeatherConfigure;
import com.fuse.domain.pojo.ChinaCity;
import com.fuse.domain.pojo.CityWeatherEachHour;
import com.fuse.domain.pojo.PredictResult;
import com.fuse.domain.to.PredictTo;
import com.fuse.exception.ObjectException;
import com.fuse.exception.PredictException;
import com.fuse.exception.WeatherFetchException;
import com.fuse.mapper.ChinaCityMapper;
import com.fuse.mapper.CityWeatherEachHourMapper;
import com.fuse.mapper.PredictResultMapper;
import com.fuse.service.PredictService;
import com.fuse.util.MybatisBatchUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Cra2iTeT
 * @since 2023/4/30 10:38
 */
@Component
public class AutoPredictService {

    private final ChinaCityMapper chinaCityMapper;

    private final CityWeatherEachHourMapper cityWeatherEachHourMapper;

    private final ThreadPoolTaskExecutor weatherExecutors;

    private final WeatherConfigure weatherConfigure;

    private final RabbitTemplate rabbitTemplate;

    private final MybatisBatchUtils mybatisBatchUtils;

    private final PredictService predictService;

    private final PredictResultMapper predictResultMapper;

    public AutoPredictService(ChinaCityMapper chinaCityMapper, CityWeatherEachHourMapper cityWeatherEachHourMapper, @Qualifier("weatherExecutors") ThreadPoolTaskExecutor weatherExecutors, WeatherConfigure weatherConfigure, RabbitTemplate rabbitTemplate, PredictService predictService, PredictResultMapper predictResultMapper, MybatisBatchUtils mybatisBatchUtils) {
        this.chinaCityMapper = chinaCityMapper;
        this.cityWeatherEachHourMapper = cityWeatherEachHourMapper;
        this.weatherExecutors = weatherExecutors;
        this.weatherConfigure = weatherConfigure;
        this.rabbitTemplate = rabbitTemplate;
        this.predictService = predictService;
        this.predictResultMapper = predictResultMapper;
        this.mybatisBatchUtils = mybatisBatchUtils;
    }

//    @Scheduled(fixedRate = 1000 * 60 * 60 * 22)
    public void updateWeather() {
        List<ChinaCity> chinaCities = fetchChinaCity();

        fetchAllWeatherFromNet(chinaCities);
    }

    //    @Scheduled(fixedRate = 1000 * 60 * 60 * 12)
    public void autoPredict() {
        List<ChinaCity> chinaCities = fetchChinaCity();
        getWeatherFromDBAndPredict(chinaCities);
    }

    private List<ChinaCity> fetchChinaCity() {

        return chinaCityMapper.getAllChinaCities();
    }

    private void fetchAllWeatherFromNet(List<ChinaCity> chinaCities) {
        // 分组
        int citySize = chinaCities.size();
        int size = Math.min(citySize, 6);

        for (int i = 0; i < size; i++) {
            int startIdx = citySize / size * i;
            int endIdx = i != size - 1 ? citySize / size * (i + 1) : citySize;
            List<ChinaCity> cities = chinaCities.subList(startIdx, endIdx);

            weatherExecutors.execute(() -> {
                List<CityWeatherEachHour> cityWeatherEachHours = transferAndGetWeather(cities);
                try {
                    saveOrUpdateToDB(cityWeatherEachHours);
                } catch (ObjectException e) {
                    WeatherFetchException exception = new WeatherFetchException("从网络中获取天气，更新到数据库时异常", e.getMessage());
                    rabbitTemplate.convertAndSend(RabbitmqConfig.ROUTINGKEY_WEATHER_FETCH_EXCEPTION, JSONUtil.toJsonStr(exception));
                }
            });
        }
    }

    private List<CityWeatherEachHour> transferAndGetWeather(List<ChinaCity> chinaCities) {
        List<CityWeatherEachHour> cityWeatherEachHours = new ArrayList<>();
        chinaCities.forEach(chinaCity -> {
            List<CityWeatherEachHour> eachHours = fetchWeather(chinaCity);
            cityWeatherEachHours.addAll(eachHours);
        });
        return cityWeatherEachHours;
    }

    private void getWeatherFromDBAndPredict(List<ChinaCity> chinaCities) {
        // 分组
        int citySize = chinaCities.size();
        int size = Math.min(citySize, 6);

        for (int i = 0; i < size; i++) {
            int startIdx = citySize / size * i;
            int endIdx = i != size - 1 ? citySize / size * (i + 1) : citySize;
            List<ChinaCity> cities = chinaCities.subList(startIdx, endIdx);

            List<String> locationIds = new ArrayList<>();
            cities.forEach(chinaCity -> {
                locationIds.add(chinaCity.getLocationId());
            });
            long from = System.currentTimeMillis() + 1000 * 60 * 60 * 12;
            long to = from + 1000 * 60 * 60 * 24 * 3;
            weatherExecutors.execute(() -> {
                List<CityWeatherEachHour> weather3d = cityWeatherEachHourMapper.get3dWeather(from, to, locationIds);
                // 根据城市分组
//                Map<String, List<CityWeatherEachHour>> weather3dGroup = weather3d.stream()
//                        .collect(Collectors.groupingBy(CityWeatherEachHour::getLocationId));

//                for (Map.Entry<String, List<CityWeatherEachHour>> stringListEntry : weather3dGroup.entrySet()) {
//                    String path = saveWeatherToCsv(stringListEntry.getValue());
//
//                    List<PredictResult> predictResults = predictByPythonScript(path);
//                    try {
//                        mybatisBatchUtils.batch(predictResults, predictResultMapper.getClass(),
//                                (predictResult, predictResultMapper) -> predictResultMapper.saveOrUpdate(predictResult));
//                    } catch (ObjectException e) {
//                        PredictException exception = new PredictException("预测自动更新失败", e.getMessage());
//                        rabbitTemplate.convertAndSend(RabbitmqConfig.ROUTINGKEY_PREDICT_EXCEPTION, JSONUtil.toJsonStr(exception));
//                    }
//                }
            });
        }
    }

    private List<PredictResult> predictByPythonScript(String path) {
        PredictTo predictTo = new PredictTo();
        predictTo.setToken(path);
        predictTo.setPredictStartTime(System.currentTimeMillis());
        predictTo.setPredictEndTime(System.currentTimeMillis() + 1000 * 60 * 60 * 24 * 3);

        return predictService.predict(predictTo);
    }

    private String saveWeatherToCsv(List<CityWeatherEachHour> cityWeatherEachHours) {
        String filename = System.currentTimeMillis() + "_" + RandomUtil.randomNumbers(3);
        String path = SystemConfig.CSV_TEMPORARY_SAVE_PATH + "\\" + filename + ".csv";
        File file = new File(path);
        if (file.exists()) {
            filename = System.currentTimeMillis() + "_" + RandomUtil.randomNumbers(3)
                    + RandomUtil.randomNumbers(2);
            path = SystemConfig.CSV_TEMPORARY_SAVE_PATH + "\\" + filename + ".csv";
        }
        CsvWriter writer = CsvUtil.getWriter(path, CharsetUtil.CHARSET_UTF_8);
        writer.write(cityWeatherEachHours);
        return path;
    }

    private void saveOrUpdateToDB(List<CityWeatherEachHour> cityWeatherEachHours) throws ObjectException {
        mybatisBatchUtils.batch(cityWeatherEachHours, CityWeatherEachHourMapper.class,
                (cityWeatherEachHour, cityWeatherEachHourMapper) -> cityWeatherEachHourMapper
                        .saveOrUpdate(cityWeatherEachHour));
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

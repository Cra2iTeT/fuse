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
import com.fuse.exception.WeatherFetchException;
import com.fuse.mapper.ChinaCityMapper;
import com.fuse.mapper.CityWeatherEachHourMapper;
import com.fuse.mapper.PredictResultMapper;
import com.fuse.service.PredictService;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
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

    private final PredictService predictService;

    private final PredictResultMapper predictResultMapper;

    public AutoPredictService(ChinaCityMapper chinaCityMapper, CityWeatherEachHourMapper cityWeatherEachHourMapper, @Qualifier("weatherExecutors") ThreadPoolTaskExecutor weatherExecutors, WeatherConfigure weatherConfigure, RabbitTemplate rabbitTemplate, PredictService predictService, PredictResultMapper predictResultMapper) {
        this.chinaCityMapper = chinaCityMapper;
        this.cityWeatherEachHourMapper = cityWeatherEachHourMapper;
        this.weatherExecutors = weatherExecutors;
        this.weatherConfigure = weatherConfigure;
        this.rabbitTemplate = rabbitTemplate;
        this.predictService = predictService;
        this.predictResultMapper = predictResultMapper;
    }

    @Scheduled(fixedRate = 1000 * 60 * 60 * 22)
    public void updateWeather() {
        List<ChinaCity> chinaCities = fetchChinaCity();

        try {
            fetchAllWeatherFromNet(chinaCities);
        } catch (WeatherFetchException e) {
            rabbitTemplate.convertAndSend(RabbitmqConfig.ROUTINGKEY_WEATHER_FETCH_EXCEPTION,
                    JSONUtil.toJsonStr(e));
        }
    }

    @Scheduled(fixedRate = 1000 * 60 * 60 * 22)
    public void autoPredict() {
        List<ChinaCity> chinaCities = fetchChinaCity();
        getWeatherFromDBAndPredict(chinaCities);
    }

    private List<ChinaCity> fetchChinaCity() {
        return chinaCityMapper.getAllChinaCities();
    }

    private void fetchAllWeatherFromNet(List<ChinaCity> chinaCities) throws WeatherFetchException {
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
            weatherExecutors.execute(() -> {
                List<CityWeatherEachHour> weather3d = get3dWeatherFromDB(locationIds);
                // 根据城市分组
                Map<String, List<CityWeatherEachHour>> weather3dGroup = weather3d.stream()
                        .collect(Collectors.groupingBy(CityWeatherEachHour::getLocationId));

                for (Map.Entry<String, List<CityWeatherEachHour>> stringListEntry : weather3dGroup.entrySet()) {
                    String path = saveWeatherToCsv(stringListEntry.getValue());

                    List<PredictResult> predictResults = predictByPythonScript(path);
                    predictResultMapper.saveOrUpdate(predictResults);
                }
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

    private List<CityWeatherEachHour> get24hWeatherFromDB(List<String> locationIds) {
        return cityWeatherEachHourMapper.get24hWeather(locationIds);
    }

    private List<CityWeatherEachHour> get48hWeatherFromDB(List<String> locationIds) {
        return cityWeatherEachHourMapper.get48hWeather(locationIds);
    }

    private List<CityWeatherEachHour> get3dWeatherFromDB(List<String> locationIds) {
        return cityWeatherEachHourMapper.get3dWeather(locationIds);
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

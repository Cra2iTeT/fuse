package com.fuse.listener;

import cn.hutool.core.text.csv.CsvUtil;
import cn.hutool.core.text.csv.CsvWriter;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.json.JSONUtil;
import com.fuse.domain.pojo.CityWeatherEachHour;
import com.fuse.domain.to.PredictTo;
import com.fuse.exception.PredictException;
import com.fuse.exception.PythonScriptRunException;
import com.fuse.mapper.ChinaCityMapper;
import com.fuse.mapper.CityWeatherEachHourMapper;
import com.fuse.mapper.FanCityMapper;
import com.fuse.service.PredictService;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.fuse.config.RabbitmqConfig.ROUTINGKEY_PREDICT_EXCEPTION;
import static com.fuse.config.SystemConfig.CSV_TEMPORARY_SAVE_PATH;

/**
 * @author Cra2iTeT
 * @since 2023/4/30 10:38
 */
@Component
public class AutoPredictService {

    private final FanCityMapper fanCityMapper;

    private final CityWeatherEachHourMapper cityWeatherEachHourMapper;

    private final PredictService predictService;

    private final ChinaCityMapper chinaCityMapper;

    private final RabbitTemplate rabbitTemplate;


    public AutoPredictService(CityWeatherEachHourMapper cityWeatherEachHourMapper,
                              PredictService predictService,
                              ChinaCityMapper chinaCityMapper,
                              RabbitTemplate rabbitTemplate, FanCityMapper fanCityMapper) {
        this.cityWeatherEachHourMapper = cityWeatherEachHourMapper;
        this.predictService = predictService;
        this.chinaCityMapper = chinaCityMapper;
        this.rabbitTemplate = rabbitTemplate;
        this.fanCityMapper = fanCityMapper;
    }

    // TODO 时间需要优化
    //    @Scheduled(fixedRate = 1000 * 60 * 60 * 12)
    public void autoPredict() {
        long from = System.currentTimeMillis() + 1000 * 60 * 60 * 12;
        long to = from + 1000 * 60 * 60 * 24 * 3;


        List<String> fanCityIds = chinaCityMapper.getFanCityIds();
        List<CityWeatherEachHour> allCityWeather = cityWeatherEachHourMapper.getWeatherFromTo(from, to, fanCityIds);
        Map<String, List<CityWeatherEachHour>> weatherMap = allCityWeather.stream()
                .collect(Collectors.groupingBy(CityWeatherEachHour::getLocationId));

        fanCityIds.forEach(id -> {
            List<Integer> fanIds = fanCityMapper.getFanId(id);
            List<CityWeatherEachHour> cityWeather = weatherMap.get(id);
            String path = transferWeatherToCsv(cityWeather);
            try {
                predictByPythonScript(path, from, to, fanIds, id);
            } catch (PythonScriptRunException | IOException | InterruptedException e) {
                PredictException exception = new PredictException("预测自动更新失败", e.getMessage());
                rabbitTemplate.convertAndSend(ROUTINGKEY_PREDICT_EXCEPTION, JSONUtil.toJsonStr(exception));
            }
        });
    }

    private String transferWeatherToCsv(List<CityWeatherEachHour> cityWeatherEachHours) {
        String filename = System.currentTimeMillis() + "_" + RandomUtil.randomNumbers(3);
        String path = CSV_TEMPORARY_SAVE_PATH + "\\" + filename + ".csv";
        File file = new File(path);
        if (file.exists()) {
            filename = System.currentTimeMillis() + "_" + RandomUtil.randomNumbers(3)
                    + RandomUtil.randomNumbers(2);
            path = CSV_TEMPORARY_SAVE_PATH + "\\" + filename + ".csv";
        }
        CsvWriter writer = CsvUtil.getWriter(path, CharsetUtil.CHARSET_UTF_8);
        writer.write(cityWeatherEachHours);
        return path;
    }

    private void predictByPythonScript(String path, long from, long to, List<Integer> fanIds, String locationId) throws PythonScriptRunException,
            IOException, InterruptedException {
        PredictTo predictTo = new PredictTo();
        predictTo.setToken(path);
        predictTo.setStartTime(from);
        predictTo.setEndTime(to);
        predictTo.setRegion(locationId);
        predictTo.setFanIds(fanIds);

        predictService.predict(predictTo);
    }
}

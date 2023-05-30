package com.fuse.listener;

import cn.hutool.core.text.csv.CsvUtil;
import cn.hutool.core.text.csv.CsvWriter;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.json.JSONUtil;
import com.fuse.config.RabbitmqConfig;
import com.fuse.config.SystemConfig;
import com.fuse.domain.pojo.CityWeatherEachHour;
import com.fuse.domain.to.PredictTo;
import com.fuse.exception.ObjectException;
import com.fuse.exception.PredictException;
import com.fuse.exception.PythonScriptRunException;
import com.fuse.mapper.ChinaCityMapper;
import com.fuse.mapper.CityWeatherEachHourMapper;
import com.fuse.mapper.FanCityMapper;
import com.fuse.service.PredictService;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * @author Cra2iTeT
 * @since 2023/4/30 10:38
 */
//@Component
public class AutoPredictService {

    private FanCityMapper fanCityMapper;

    private final CityWeatherEachHourMapper cityWeatherEachHourMapper;

    private final PredictService predictService;

    private final ChinaCityMapper chinaCityMapper;

    private final RabbitTemplate rabbitTemplate;

    public AutoPredictService(FanCityMapper fanCityMapper,
                              CityWeatherEachHourMapper cityWeatherEachHourMapper,
                              PredictService predictService,
                              ChinaCityMapper chinaCityMapper,
                              RabbitTemplate rabbitTemplate) {
        this.fanCityMapper = fanCityMapper;
        this.cityWeatherEachHourMapper = cityWeatherEachHourMapper;
        this.predictService = predictService;
        this.chinaCityMapper = chinaCityMapper;
        this.rabbitTemplate = rabbitTemplate;
    }

    //    @Scheduled(fixedRate = 1000 * 60 * 60 * 12)
    public void autoPredict() {
        long from = System.currentTimeMillis() + 1000 * 60 * 60 * 12;
        long to = from + 1000 * 60 * 60 * 24 * 3;

        List<String> fanCityIds = chinaCityMapper.getFanCityIds();
        List<CityWeatherEachHour> cityWeatherEachHours = getWeatherFromDB(fanCityIds, from, to);
        String path = transferWeatherToCsv(cityWeatherEachHours);

        try {
            predictByPythonScript(path);
        } catch (ObjectException | IOException | InterruptedException e) {
            PredictException exception = new PredictException("预测自动更新失败", e.getMessage());
            rabbitTemplate.convertAndSend(RabbitmqConfig.ROUTINGKEY_PREDICT_EXCEPTION,
                    JSONUtil.toJsonStr(exception));
        }
    }

    private List<CityWeatherEachHour> getWeatherFromDB(List<String> fanCityIds, long from, long to) {
        return cityWeatherEachHourMapper.getWeatherFromTo(from, to, fanCityIds);
    }

    private void predictByPythonScript(String path) throws ObjectException, IOException, InterruptedException {
        // TODO predict 内容补全
        PredictTo predictTo = new PredictTo();
        predictTo.setToken(path);

        predictService.predict(predictTo);
    }

    private String transferWeatherToCsv(List<CityWeatherEachHour> cityWeatherEachHours) {
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
}

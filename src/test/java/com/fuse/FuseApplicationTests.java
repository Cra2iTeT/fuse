package com.fuse;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSON;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.fuse.config.RabbitmqConfig;
import com.fuse.config.SystemConfig;
import com.fuse.config.configure.SystemConfigure;
import com.fuse.domain.pojo.CityWeatherEachHour;
import com.fuse.domain.vo.CsvTimeDivideVo;
import com.fuse.exception.ObjectException;
import com.fuse.exception.PythonScriptRunException;
import com.fuse.mapper.CityWeatherEachHourMapper;
import com.fuse.util.MybatisBatchUtils;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class FuseApplicationTests {

    @Autowired
    SystemConfigure systemConfigure;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Test
    void test() throws PythonScriptRunException {
        String[] arguments = new String[]{systemConfigure.getPythonExePath(),
                SystemConfig.PYTHON_SCRIPT_Parent_PATH + "\\" + SystemConfig.PYTHON_SCRIPT_TIME_DIVIDE_PATH};
        try {
            Process process = Runtime.getRuntime().exec(arguments);
            BufferedReader bufferedReader = new
                    BufferedReader(new InputStreamReader(process.getInputStream()));

            CsvTimeDivideVo csvTimeDivideVo = new CsvTimeDivideVo();
            //waitFor是用来显示脚本是否运行成功，1表示失败，0表示成功，还有其他的表示其他错误
            if (process.waitFor() != 0) {
                throw new PythonScriptRunException("python脚本执行错误");
            }
            String line;
            System.out.println("java");
            while ((line = bufferedReader.readLine()) != null) {
                System.out.println(line);
            }
            bufferedReader.close();
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void test2() {
    }

    @Test
    void test3() throws ParseException {
        DateTime parse = DateUtil.parse("2023-05-01T01:00+08:00", "yyyy-MM-dd'T'HH:mmXXX");
        DateTime parse2 = DateUtil.parse("2023-05-01T02:00+08:00", "yyyy-MM-dd'T'HH:mmXXX");
        System.out.println(parse.getTime());
        System.out.println(parse2.getTime());
    }

    @Test
    void test4() {
        System.out.println(RandomUtil.randomNumbers(3));
        System.out.println(RandomUtil.randomNumbers(3));
        System.out.println(RandomUtil.randomNumbers(3));
    }

    @Test
    void test5() throws IOException {
        MultipartFile file = (MultipartFile) new File("F:\\Java\\fuse\\dateset" + "\\" + 1 + ".csv");
        File file2 = new File("F:\\Java\\fuse\\dateset" + "\\" + 2 + ".csv");

        file.transferTo(file2);
    }

    @Test
    void test6() {
        rabbitTemplate.setMandatory(true);
        //发送消息
        rabbitTemplate.convertAndSend(RabbitmqConfig.EXCHANGE_DIRECT_EXCEPTION_LISTENER, "", "2222");
    }

    @Autowired
    private CityWeatherEachHourMapper cityWeatherEachHourMapper;

    @Autowired
    private MybatisBatchUtils mybatisBatchUtils;

    @Test
    void test7() {
        CityWeatherEachHour eachHour = new CityWeatherEachHour();
        eachHour.setLocationId(System.currentTimeMillis() + "1");
        eachHour.setTime(System.currentTimeMillis());
        eachHour.setLocationName("55");
        eachHour.setTemperature("37");
        eachHour.setWindDirection(350);
        eachHour.setPressure(1000);
        eachHour.setWindSpeed("10");
        eachHour.setHumidity("20");
        eachHour.setDate(DateUtil.date(eachHour.getTime()));

        cityWeatherEachHourMapper.saveOrUpdate(eachHour);
    }

    @Test
    void test8() {
        System.out.println(IOException.class.getName());
    }

    @Test
    void test9() {
        File file = new File("F:\\Java\\fuse\\sql\\fuse.sql");
        System.out.println(file.getName());
        System.out.println(file.getAbsolutePath());
        System.out.println(file.getPath());
    }
}

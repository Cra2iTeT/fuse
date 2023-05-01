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
        String result = HttpUtil.createGet("https://devapi.qweather.com/v7/weather/24h?location=101090101&key=553f1bc8f5f44e079c9ecc0fe82d7bf9").execute().body();

        JSONObject jsonObject = JSONUtil.parseObj(result);
        JSONArray jsonArray = jsonObject.getJSONArray("hourly");
        for (Object o : jsonArray) {
            String str = String.valueOf(o);
            JSONObject entries = JSONUtil.parseObj(str);
            CityWeatherEachHour cityWeatherEachHour = new CityWeatherEachHour();
            System.out.println("------------------------------");
            cityWeatherEachHour.setTime(DateUtil.parse(String.valueOf(entries.get("fxTime")), "yyyy-MM-dd'T'HH:mmXXX").getTime());
            cityWeatherEachHour.setHumidity(Byte.parseByte(String.valueOf(entries.get("humidity"))));
            cityWeatherEachHour.setTemperature(Byte.parseByte(String.valueOf(entries.get("temp"))));
            cityWeatherEachHour.setPressure(Integer.parseInt(String.valueOf(entries.get("pressure"))));
            cityWeatherEachHour.setWindDirection(Integer.parseInt(String.valueOf(entries.get("wind360"))));
            cityWeatherEachHour.setWindSpeed(Byte.parseByte(String.valueOf(entries.get("windSpeed"))));
            cityWeatherEachHour.setLocationId("101090101");
            System.out.println(cityWeatherEachHour);
            System.out.println("------------------------------");
        }


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
        rabbitTemplate.convertAndSend(RabbitmqConfig.EXCHANGE_FANOUT_EXCEPTION_LISTENER, "", "2222");
    }

    @Autowired
    private CityWeatherEachHourMapper cityWeatherEachHourMapper;

    @Autowired
    private MybatisBatchUtils mybatisBatchUtils;

    @Test
    void test7() throws ObjectException, InterruptedException {
        CityWeatherEachHour eachHour = new CityWeatherEachHour();
        eachHour.setLocationId(System.currentTimeMillis() + "1");
        eachHour.setTime(System.currentTimeMillis());
        eachHour.setTemperature((byte) 37);
        eachHour.setWindDirection(350);
        eachHour.setPressure(1000);
        eachHour.setWindSpeed((byte) 10);
        eachHour.setHumidity((byte) 20);

        cityWeatherEachHourMapper.save(eachHour);
        Thread.sleep(3000);
        eachHour.setHumidity((byte) 25);

        CityWeatherEachHour eachHour2 = new CityWeatherEachHour();
        eachHour2.setLocationId(System.currentTimeMillis() + "2");
        eachHour2.setTime(System.currentTimeMillis());
        eachHour2.setTemperature((byte) 38);
        eachHour2.setWindDirection(350);
        eachHour2.setPressure(1000);
        eachHour2.setWindSpeed((byte) 10);
        eachHour2.setHumidity((byte) 20);


        CityWeatherEachHour eachHour3 = new CityWeatherEachHour();
        eachHour3.setLocationId(System.currentTimeMillis() + "3");
        eachHour3.setTime(System.currentTimeMillis());
        eachHour3.setTemperature((byte) 37);
        eachHour3.setWindDirection(350);
        eachHour3.setPressure(1000);
        eachHour3.setWindSpeed((byte) 10);
        eachHour3.setHumidity((byte) 20);

//        cityWeatherEachHourMapper.save(eachHour);

        List<CityWeatherEachHour> list = new ArrayList<>();
        list.add(eachHour);
        list.add(eachHour2);
        list.add(eachHour3);
    }
}

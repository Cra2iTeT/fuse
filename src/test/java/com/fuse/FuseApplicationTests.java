package com.fuse;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSON;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.fuse.config.SystemConfig;
import com.fuse.config.configure.SystemConfigure;
import com.fuse.domain.pojo.CityWeatherEachHour;
import com.fuse.domain.vo.CsvTimeDivideVo;
import com.fuse.exception.PythonScriptRunException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

@SpringBootTest
class FuseApplicationTests {

    @Autowired
    SystemConfigure systemConfigure;

    @Test
    void test() {
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
}

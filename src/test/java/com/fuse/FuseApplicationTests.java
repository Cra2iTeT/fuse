package com.fuse;

import com.fuse.config.SystemConfig;
import com.fuse.config.SystemConfigure;
import com.fuse.domain.vo.CsvTimeDivideVo;
import com.fuse.exception.PythonScriptRunException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

@SpringBootTest
class FuseApplicationTests {

    @Autowired
    SystemConfigure systemConfigure;

    @Test
    void contextLoads() {
        System.out.println(systemConfigure.getCsvTemporarySavePath());
    }

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
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}

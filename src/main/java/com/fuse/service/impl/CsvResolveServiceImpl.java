package com.fuse.service.impl;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.json.JSONUtil;
import com.fuse.common.SystemCode;
import com.fuse.config.RabbitmqConfig;
import com.fuse.config.SystemConfig;
import com.fuse.config.configure.SystemConfigure;
import com.fuse.domain.vo.CsvTimeDivideVo;
import com.fuse.domain.vo.R;
import com.fuse.exception.ObjectException;
import com.fuse.exception.PythonScriptRunException;
import com.fuse.service.CsvResolveService;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * @author Cra2iTeT
 * @since 2023/4/29 14:40
 */
@Service
public class CsvResolveServiceImpl implements CsvResolveService {

    private final SystemConfigure systemConfigure;

    private final RabbitTemplate rabbitTemplate;

    public CsvResolveServiceImpl(SystemConfigure systemConfigure, RabbitTemplate rabbitTemplate) {
        this.systemConfigure = systemConfigure;
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public R csvResolve(MultipartFile csv) {
        String temporarySavePath = SystemConfig.CSV_TEMPORARY_SAVE_PATH;
        String filename = System.currentTimeMillis() + "_" + RandomUtil.randomNumbers(3);
        String path = temporarySavePath + "\\" + filename + ".csv";
        File file = new File(path);

        if (file.exists()) {
            filename = System.currentTimeMillis() + RandomUtil.randomNumbers(3) + RandomUtil.randomNumbers(2);
            path = temporarySavePath + "\\" + filename + ".csv";
            file = new File(path);
        }

        try {
            csv.transferTo(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try {
            CsvTimeDivideVo csvTimeDivideVo = csvResolveByPython();
            csvTimeDivideVo.setToken(path);
            return new R<>(SystemCode.CSV_RESOLVE_SUCCESS.getCode(),
                    SystemCode.SUCCESS.getMsg(), csvTimeDivideVo);
        } catch (PythonScriptRunException e) {
            String errorMsg = "python脚本执行错误,请与系统管理员联系";
            ObjectException exception = new ObjectException(errorMsg,
                    "PythonScriptRunException.class", e.getMessage());
            rabbitTemplate.convertAndSend(RabbitmqConfig.ROUTINGKEY_PYTHON_SCRIPT_EXCEPTION,
                    JSONUtil.toJsonStr(exception));
            return new R<>(SystemCode.CSV_RESOLVE_ERROR.getCode(),
                    SystemCode.CSV_RESOLVE_ERROR.getMsg(), null);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private CsvTimeDivideVo csvResolveByPython() throws PythonScriptRunException, IOException,
            InterruptedException {
        String[] arguments = new String[]{systemConfigure.getPythonExePath(),
                SystemConfig.PYTHON_SCRIPT_Parent_PATH + "\\" + SystemConfig.PYTHON_SCRIPT_TIME_DIVIDE_PATH};
        try {
            Process process = Runtime.getRuntime().exec(arguments);
            BufferedReader bufferedReader = new
                    BufferedReader(new InputStreamReader(process.getInputStream()));

            //waitFor是用来显示脚本是否运行成功，1表示失败，0表示成功，还有其他的表示其他错误
            if (process.waitFor() != 0) {
                throw new PythonScriptRunException("python脚本执行错误");
            }

            CsvTimeDivideVo csvTimeDivideVo = new CsvTimeDivideVo();
            csvTimeDivideVo.setStartTime(Long.parseLong(bufferedReader.readLine()));
            csvTimeDivideVo.setEndTime(Long.parseLong(bufferedReader.readLine()));

            bufferedReader.close();
            return csvTimeDivideVo;
        } catch (IOException e) {
            throw new IOException(e);
        } catch (InterruptedException e) {
            throw new InterruptedException(e.getMessage());
        }
    }
}

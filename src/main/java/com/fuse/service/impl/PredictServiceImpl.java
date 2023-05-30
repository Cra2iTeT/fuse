package com.fuse.service.impl;

import cn.hutool.core.util.RandomUtil;
import com.alibaba.excel.EasyExcel;
import com.fuse.common.SystemCode;
import com.fuse.config.SystemConfig;
import com.fuse.config.configure.SystemConfigure;
import com.fuse.domain.pojo.PredictResult;
import com.fuse.domain.to.PredictTo;
import com.fuse.domain.vo.CsvTimeDivideVo;
import com.fuse.domain.vo.R;
import com.fuse.exception.ObjectException;
import com.fuse.exception.PythonScriptRunException;
import com.fuse.listener.PredictResultListener;
import com.fuse.service.PredictService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import static com.fuse.common.SystemCode.CSV_LOAD_ERROR;
import static com.fuse.common.SystemCode.SUCCESS;
import static com.fuse.config.SystemConfig.CSV_TEMPORARY_SAVE_PATH;

/**
 * @author Cra2iTeT
 * @since 2023/4/29 14:40
 */
@Service
public class PredictServiceImpl implements PredictService {



    @Autowired
    private PredictResultListener predictResultListener;

    private final SystemConfigure systemConfigure;

    public PredictServiceImpl(SystemConfigure systemConfigure) {
        this.systemConfigure = systemConfigure;
    }

    @Override
    public R csvResolve(MultipartFile csv) throws PythonScriptRunException, IOException,
            InterruptedException {
        File file = generateFile();

        csv.transferTo(file);

        CsvTimeDivideVo csvTimeDivideVo = timeResolveByPython();
        csvTimeDivideVo.setToken(file.getPath());
        return new R<>(SystemCode.CSV_RESOLVE_SUCCESS.getCode(),
                SystemCode.SUCCESS.getMsg(), csvTimeDivideVo);
    }

    private File generateFile() {
        String prefix = CSV_TEMPORARY_SAVE_PATH;
        String filename = System.currentTimeMillis() + "_" + RandomUtil.randomNumbers(3);
        String path = prefix + "\\" + filename + ".csv";
        File file = new File(path);

        if (file.exists()) {
            filename = System.currentTimeMillis() + RandomUtil.randomNumbers(3)
                    + RandomUtil.randomNumbers(2);
            path = prefix + "\\" + filename + ".csv";
            file = new File(path);
        }
        return file;
    }

    private CsvTimeDivideVo timeResolveByPython() throws PythonScriptRunException, IOException,
            InterruptedException {
        String[] arguments = new String[]{systemConfigure.getPythonExePath(),
                SystemConfig.PYTHON_SCRIPT_Parent_PATH + "\\" +
                        SystemConfig.PYTHON_SCRIPT_TIME_DIVIDE_PATH};
        Process process = Runtime.getRuntime().exec(arguments);
        BufferedReader bufferedReader = new
                BufferedReader(new InputStreamReader(process.getInputStream()));

        //waitFor是用来显示脚本是否运行成功，1表示失败，0表示成功，还有其他的表示其他错误
        if (process.waitFor() != 0) {
            throw new PythonScriptRunException("python脚本执行错误,请与系统管理员联系");
        }

        CsvTimeDivideVo csvTimeDivideVo = new CsvTimeDivideVo();
        csvTimeDivideVo.setStartTime(Long.parseLong(bufferedReader.readLine()));
        csvTimeDivideVo.setEndTime(Long.parseLong(bufferedReader.readLine()));

        bufferedReader.close();
        return csvTimeDivideVo;
    }

    // 预测
    @Override
    public R predict(PredictTo predictTo) throws ObjectException, IOException, InterruptedException {
        String[] arguments = new String[]{systemConfigure.getPythonExePath(),
                SystemConfig.PYTHON_SCRIPT_Parent_PATH + "\\" +
                        SystemConfig.PYTHON_SCRIPT_PREDICT_PATH};
        Process process = Runtime.getRuntime().exec(arguments);
        BufferedReader bufferedReader = new
                BufferedReader(new InputStreamReader(process.getInputStream()));

        //waitFor是用来显示脚本是否运行成功，1表示失败，0表示成功，还有其他的表示其他错误
        if (process.waitFor() != 0) {
            throw new PythonScriptRunException("python脚本执行错误,请与系统管理员联系");
        }
        //转储csv
        loadCsv2Database(predictTo.getToken(),predictTo.getRegion());
        return new R(SUCCESS.getCode(),SUCCESS.getMsg());
    }

    //导入csv
    public void loadCsv2Database(String path,String location) throws ObjectException {
        try{

            predictResultListener.setLocation(location);
            EasyExcel.read(path, PredictResult.class, predictResultListener).doReadAll();
        }catch (Exception e){
            throw new ObjectException(CSV_LOAD_ERROR.getMsg());
        }

    }
}

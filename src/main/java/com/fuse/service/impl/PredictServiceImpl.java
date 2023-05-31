package com.fuse.service.impl;

import cn.hutool.core.util.RandomUtil;
import com.alibaba.excel.EasyExcel;
import com.fuse.config.configure.SystemConfigure;
import com.fuse.domain.pojo.PredictResult;
import com.fuse.domain.to.PredictTo;
import com.fuse.domain.vo.CsvTimeDivideVo;
import com.fuse.domain.vo.R;
import com.fuse.exception.PythonScriptRunException;
import com.fuse.listener.PredictResultListener;
import com.fuse.service.PredictService;
import com.fuse.util.PythonScriptUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;

import static com.fuse.common.SystemCode.CSV_RESOLVE_SUCCESS;
import static com.fuse.common.SystemCode.SUCCESS;
import static com.fuse.config.SystemConfig.*;

/**
 * @author Cra2iTeT
 * @since 2023/4/29 14:40
 */
@Service
public class PredictServiceImpl implements PredictService {

    private final PredictResultListener predictResultListener;

    private final SystemConfigure systemConfigure;

    public PredictServiceImpl(SystemConfigure systemConfigure, PredictResultListener predictResultListener) {
        this.systemConfigure = systemConfigure;
        this.predictResultListener = predictResultListener;
    }

    @Override
    public R csvResolve(MultipartFile csv) throws PythonScriptRunException, IOException,
            InterruptedException {
        File file = generateFile();

        csv.transferTo(file);

        CsvTimeDivideVo csvTimeDivideVo = timeResolveByPython();
        csvTimeDivideVo.setToken(file.getPath());
        return new R<>(CSV_RESOLVE_SUCCESS.getCode(), SUCCESS.getMsg(), csvTimeDivideVo);
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

    private CsvTimeDivideVo timeResolveByPython() throws PythonScriptRunException, IOException, InterruptedException {
        String[] arguments = new String[]{systemConfigure.getPythonExePath(),
                PYTHON_SCRIPT_Parent_PATH + "\\" + PYTHON_SCRIPT_TIME_DIVIDE_PATH};

        BufferedReader bufferedReader;
        bufferedReader = PythonScriptUtils.invokePythonScript(arguments);

        CsvTimeDivideVo csvTimeDivideVo = new CsvTimeDivideVo();
        csvTimeDivideVo.setStartTime(Long.parseLong(bufferedReader.readLine()));
        csvTimeDivideVo.setEndTime(Long.parseLong(bufferedReader.readLine()));

        bufferedReader.close();
        return csvTimeDivideVo;
    }

    @Override
    public R predict(PredictTo predictTo) throws PythonScriptRunException, IOException, InterruptedException {
        String[] arguments = new String[]{systemConfigure.getPythonExePath(),
                PYTHON_SCRIPT_Parent_PATH + "\\" + PYTHON_SCRIPT_PREDICT_PATH};

        BufferedReader bufferedReader;
        bufferedReader = PythonScriptUtils.invokePythonScript(arguments);
        String path = bufferedReader.readLine();

        //转储csv
        loadCsv2Database(path, predictTo.getRegion());

        bufferedReader.close();
        return new R<>(SUCCESS.getCode(), SUCCESS.getMsg());
    }

    public void loadCsv2Database(String path, String locationId) {
        predictResultListener.setLocation(locationId);
        EasyExcel.read(path, PredictResult.class, predictResultListener).doReadAll();
    }
}

package com.fuse.controller;

import cn.hutool.json.JSONUtil;
import com.fuse.common.SystemCode;
import com.fuse.config.RabbitmqConfig;
import com.fuse.domain.to.PredictTo;
import com.fuse.domain.vo.R;
import com.fuse.exception.ObjectException;
import com.fuse.exception.PythonScriptRunException;
import com.fuse.service.PredictService;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * @author Cra2iTeT
 * @since 2023/4/29 13:59
 */
@RestController
@RequestMapping("/predict")
public class PredictController {

    private final PredictService predictService;

    private final RabbitTemplate rabbitTemplate;

    public PredictController(PredictService predictService, RabbitTemplate rabbitTemplate) {
        this.predictService = predictService;
        this.rabbitTemplate = rabbitTemplate;
    }

    /**
     * 上传CSV文件解析最大最小时间
     *
     * @param csv
     * @return
     */
    @PostMapping("/csv")
    public R CsvTimeDivide(@RequestParam("csv") MultipartFile csv) {
        if (csv.isEmpty() || !"csv".equals(csv.getContentType())) {
            return new R<>(SystemCode.CSV_RESOLVE_ERROR_TYPE_MISMATCH.getCode(),
                    SystemCode.CSV_RESOLVE_ERROR_TYPE_MISMATCH.getMsg());
        }
        try {
            return predictService.csvResolve(csv);
        } catch (PythonScriptRunException e) {
            rabbitTemplate.convertAndSend(RabbitmqConfig.ROUTINGKEY_PYTHON_SCRIPT_EXCEPTION,
                    JSONUtil.toJsonStr(e));
        } catch (IOException | InterruptedException e) {
            ObjectException exception = new ObjectException("", ObjectException.class.getName(),
                    e.getMessage());
            rabbitTemplate.convertAndSend(RabbitmqConfig.ROUTINGKEY_OBJECT_EXCEPTION,
                    JSONUtil.toJsonStr(exception));
        }
        return new R<>(SystemCode.ERROR.getCode(), SystemCode.ERROR.getMsg());
    }

    public R predictByMultiModel(@RequestBody PredictTo predictTo) {
        try {
            return predictService.predict(predictTo);
        } catch (PythonScriptRunException e) {
            throw new RuntimeException(e);
        }
    }
}

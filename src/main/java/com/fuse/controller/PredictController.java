package com.fuse.controller;

import cn.hutool.json.JSONUtil;
import com.fuse.config.RabbitmqConfig;
import com.fuse.domain.to.PredictTo;
import com.fuse.domain.vo.R;
import com.fuse.exception.ObjectException;
import com.fuse.exception.PythonScriptRunException;
import com.fuse.service.PredictService;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.text.ParseException;

import static com.fuse.common.SystemCode.CSV_RESOLVE_ERROR_TYPE_MISMATCH;
import static com.fuse.common.SystemCode.ERROR;
import static com.fuse.config.RabbitmqConfig.ROUTINGKEY_OBJECT_EXCEPTION;
import static com.fuse.config.RabbitmqConfig.ROUTINGKEY_PYTHON_SCRIPT_EXCEPTION;

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
        /*if (csv.isEmpty() || !"csv".equals(csv.getContentType())) {
            return new R<>(CSV_RESOLVE_ERROR_TYPE_MISMATCH.getCode(), CSV_RESOLVE_ERROR_TYPE_MISMATCH.getMsg());
        }*/
        try {
            return predictService.csvResolve(csv);
        } catch (PythonScriptRunException e) {
            rabbitTemplate.convertAndSend(ROUTINGKEY_PYTHON_SCRIPT_EXCEPTION, JSONUtil.toJsonStr(e));
        } catch (IOException | InterruptedException | ParseException e) {
            ObjectException exception = new ObjectException("csv解析失败", ObjectException.class.getName(),
                    e.getMessage());
            rabbitTemplate.convertAndSend(ROUTINGKEY_OBJECT_EXCEPTION, JSONUtil.toJsonStr(exception));
        }
        return new R<>(ERROR.getCode(), ERROR.getMsg());
    }

    @PostMapping
    public R predictByMultiModel(@RequestBody PredictTo predictTo) {
        try {
            return predictService.predict(predictTo);
        } catch (PythonScriptRunException e) {
            rabbitTemplate.convertAndSend(ROUTINGKEY_PYTHON_SCRIPT_EXCEPTION, JSONUtil.toJsonStr(e));
        } catch (IOException | InterruptedException e) {
            ObjectException exception = new ObjectException("多模型预测失败", ObjectException.class.getName(),
                    e.getMessage());
            rabbitTemplate.convertAndSend(ROUTINGKEY_OBJECT_EXCEPTION, JSONUtil.toJsonStr(exception));
        }
        return new R<>(ERROR.getCode(), ERROR.getMsg());
    }
}

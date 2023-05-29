package com.fuse.controller;

import com.fuse.domain.to.PredictTo;
import com.fuse.domain.vo.R;
import com.fuse.exception.ObjectException;
import com.fuse.service.PredictService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Cra2iTeT
 * @since 2023/4/29 23:46
 */
@RestController
@RequestMapping("/predict")
public class PredictController {

    @Autowired
    private PredictService predictService;

    public R predictByMultiModel(@RequestBody PredictTo predictTo) {
        return null;
    }

    @GetMapping
    public void sendMessage() throws ObjectException {
        predictService.sendWebSocket();
    }
}

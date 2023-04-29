package com.fuse.controller;

import com.fuse.domain.to.PredictTo;
import com.fuse.domain.vo.R;
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

    public R predictByMultiModel(@RequestBody PredictTo predictTo) {
        return null;
    }
}

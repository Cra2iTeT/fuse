package com.fuse.service.impl;

import com.fuse.domain.pojo.PredictResult;
import com.fuse.domain.to.PredictTo;
import com.fuse.service.PredictService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Cra2iTeT
 * @since 2023/4/29 23:53
 */
@Service
public class PredictServiceImpl implements PredictService {
    @Override
    public List<PredictResult> predict(PredictTo predictTo) {
        return null;
    }
}

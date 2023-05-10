package com.fuse.service;

import com.fuse.domain.pojo.PredictResult;
import com.fuse.domain.to.PredictTo;
import com.fuse.exception.PythonScriptRunException;

import java.util.List;

/**
 * @author Cra2iTeT
 * @since 2023/4/29 23:53
 */
public interface PredictService {
    List<PredictResult> predict(PredictTo predictTo) throws PythonScriptRunException;
}

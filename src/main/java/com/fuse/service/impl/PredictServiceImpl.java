package com.fuse.service.impl;

import com.fuse.domain.pojo.PredictResult;
import com.fuse.domain.to.PredictTo;
import com.fuse.exception.ObjectException;
import com.fuse.exception.PythonScriptRunException;
import com.fuse.service.PredictService;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.fuse.common.SystemCode.SYSTEM_ERROR_RED;

/**
 * @author Cra2iTeT
 * @since 2023/4/29 23:53
 */
@Service
public class PredictServiceImpl implements PredictService {
    @Override
    public List<PredictResult> predict(PredictTo predictTo) throws PythonScriptRunException {
        return null;
    }

    @Override
    public void sendWebSocket() {

        throw new ObjectException(SYSTEM_ERROR_RED.getMsg(),SYSTEM_ERROR_RED.getMsg());

    }
}

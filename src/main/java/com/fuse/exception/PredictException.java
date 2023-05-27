package com.fuse.exception;

import com.fuse.common.ExceptionCode;

/**
 * @author Cra2iTeT
 * @since 2023/5/1 9:32
 */
public class PredictException extends ObjectException {

    public PredictException(String message) {
        this(message, "");
    }

    public PredictException(String message, String log) {
        super(message, PredictException.class.getName(),
                ExceptionCode.PREDICT_EXCEPTION, log);
    }

}

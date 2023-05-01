package com.fuse.exception;

/**
 * @author Cra2iTeT
 * @since 2023/5/1 9:32
 */
public class PredictException extends ObjectException {

    public PredictException() {
        this("","");
    }

    public PredictException(String message) {
        this(message,"");
    }

    public PredictException(String message, String log) {
        super(message,"WeatherFetchException.class",log);
    }
}

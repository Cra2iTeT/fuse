package com.fuse.exception;

/**
 * @author Cra2iTeT
 * @since 2023/5/1 9:32
 */
public class WeatherFetchException extends ObjectException {

    public WeatherFetchException() {
        this("","");
    }

    public WeatherFetchException(String message) {
        this(message,"");
    }

    public WeatherFetchException(String message,String log) {
        super(message,"WeatherFetchException.class",log);
    }
}

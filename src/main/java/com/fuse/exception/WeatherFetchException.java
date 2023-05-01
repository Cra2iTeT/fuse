package com.fuse.exception;

/**
 * @author Cra2iTeT
 * @since 2023/5/1 9:32
 */
public class WeatherFetchException extends ObjectException {

    public WeatherFetchException() {
        super("WeatherFetchException.class");
    }

    public WeatherFetchException(String message) {
        super(message,"WeatherFetchException.class");
    }
}

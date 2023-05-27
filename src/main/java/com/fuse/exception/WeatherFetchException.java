package com.fuse.exception;

import com.fuse.common.ExceptionCode;

/**
 * @author Cra2iTeT
 * @since 2023/5/1 9:32
 */
public class WeatherFetchException extends ObjectException {


    public WeatherFetchException(String message) {
        this(message, "");
    }

    public WeatherFetchException(String message, String log) {
        super(message, WeatherFetchException.class.getName(),
                ExceptionCode.WEATHER_FETCH_EXCEPTION, log);
    }
}

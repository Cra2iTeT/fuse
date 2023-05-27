package com.fuse.exception;

import com.fuse.common.ExceptionCode;

/**
 * @author Cra2iTeT
 * @since 2023/5/1 9:32
 */
public class WeatherException extends ObjectException {

    public WeatherException(String message) {
        this(message,"");
    }

    public WeatherException(String message, String log) {
        super(message, WeatherException.class.getName(),
                ExceptionCode.WEATHER_EXCEPTION, log);
    }
}

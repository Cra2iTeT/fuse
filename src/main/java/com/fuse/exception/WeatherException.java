package com.fuse.exception;

import static com.fuse.common.ExceptionCode.WEATHER_EXCEPTION;

/**
 * @author Cra2iTeT
 * @since 2023/5/1 9:32
 */
public class WeatherException extends ObjectException {

    public WeatherException(String message) {
        this(message, "");
    }

    public WeatherException(String message, String log) {
        super(message, WeatherException.class.getName(), WEATHER_EXCEPTION, log);
    }
}

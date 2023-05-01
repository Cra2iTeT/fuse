package com.fuse.config.configure;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * @author Cra2iTeT
 * @since 2023/5/1 8:54
 */
@Configuration
public class WeatherConfigure {

    @Value("${weather.key}")
    private String key;

    @Value("${weather.prefix}")
    private String prefix;

    public String getKey() {
        return key;
    }

    public String getPrefix() {
        return prefix;
    }
}

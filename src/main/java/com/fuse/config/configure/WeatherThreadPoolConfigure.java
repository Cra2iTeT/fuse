package com.fuse.config.configure;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author Cra2iTeT
 * @since 2023/4/30 11:07
 */
@Component
@Data
public class WeatherThreadPoolConfigure {
    @Value("${thread-pool.weather.core-size:2}")
    private int coreSize;

    @Value("${thread-pool.weather.max-size:6}")
    private int maxSize;

    @Value("${thread-pool.weather.queue-size:300}")
    private int queueSize;

    @Value("${thread-pool.weather.prefix:weather-Executor-}")
    private String prefix;

    @Value("${thread-pool.weather.keep-alive-seconds:60}")
    private int keepAliveSeconds;
}

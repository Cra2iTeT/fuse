package com.fuse.config;

import com.fuse.config.configure.WeatherThreadPoolConfigure;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author Cra2iTeT
 * @since 2023/4/30 11:06
 */
@Configuration
public class WeatherThreadPoolConfig {

    private final WeatherThreadPoolConfigure weatherThreadPoolConfigure;

    public WeatherThreadPoolConfig(WeatherThreadPoolConfigure weatherThreadPoolConfigure) {
        this.weatherThreadPoolConfigure = weatherThreadPoolConfigure;
    }

    @Bean
    public Executor weatherExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(weatherThreadPoolConfigure.getCoreSize());
        executor.setMaxPoolSize(weatherThreadPoolConfigure.getMaxSize());
        executor.setQueueCapacity(weatherThreadPoolConfigure.getQueueSize());
        executor.setThreadNamePrefix(weatherThreadPoolConfigure.getPrefix());
        executor.setKeepAliveSeconds(weatherThreadPoolConfigure.getKeepAliveSeconds());

        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());

        executor.initialize();
        return executor;
    }
}

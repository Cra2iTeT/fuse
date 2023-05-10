package com.fuse.config;

import com.fuse.config.configure.AutoThreadPoolConfigure;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author Cra2iTeT
 * @since 2023/4/30 11:06
 */
@Configuration
public class AutoThreadPoolConfig {

    private final AutoThreadPoolConfigure autoThreadPoolConfigure;

    public AutoThreadPoolConfig(AutoThreadPoolConfigure autoThreadPoolConfigure) {
        this.autoThreadPoolConfigure = autoThreadPoolConfigure;
    }

    @Bean
    public ThreadPoolTaskExecutor autoExecutors() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(autoThreadPoolConfigure.getCoreSize());
        executor.setMaxPoolSize(autoThreadPoolConfigure.getMaxSize());
        executor.setQueueCapacity(autoThreadPoolConfigure.getQueueSize());
        executor.setThreadNamePrefix(autoThreadPoolConfigure.getPrefix());
        executor.setKeepAliveSeconds(autoThreadPoolConfigure.getKeepAliveSeconds());

        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());

        executor.initialize();
        return executor;
    }
}

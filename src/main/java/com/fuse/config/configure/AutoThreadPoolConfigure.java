package com.fuse.config.configure;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * @author Cra2iTeT
 * @since 2023/4/30 11:07
 */
@Configuration
public class AutoThreadPoolConfigure {
    @Value("${thread-pool.auto.core-size:2}")
    private int coreSize;

    @Value("${thread-pool.auto.max-size:6}")
    private int maxSize;

    @Value("${thread-pool.auto.queue-size:300}")
    private int queueSize;

    @Value("${thread-pool.auto.prefix:weather-Executor-}")
    private String prefix;

    @Value("${thread-pool.auto.keep-alive-seconds:60}")
    private int keepAliveSeconds;

    public int getCoreSize() {
        return coreSize;
    }

    public int getMaxSize() {
        return maxSize;
    }

    public int getQueueSize() {
        return queueSize;
    }

    public String getPrefix() {
        return prefix;
    }

    public int getKeepAliveSeconds() {
        return keepAliveSeconds;
    }
}

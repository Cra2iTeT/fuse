package com.fuse.domain.pojo;

import lombok.Data;

/**
 * @author Cra2iTeT
 * @since 2023/5/1 16:14
 */
@Data
public class PredictResult {
    private long time;
    private String region;
    private int fanId;
    private String power;
    private String yd15;
}

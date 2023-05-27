package com.fuse.domain.pojo;

import lombok.Data;

/**
 * @author Cra2iTeT
 * @since 2023/5/1 11:45
 */
@Data
public class ErrorLog {
    private String logId;
    private long errorTime;
    private String errorMsg;
    private byte errorCode;
    private String errorType;
    private String log;
}

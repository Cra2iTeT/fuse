package com.fuse.exception;

import com.fuse.common.ExceptionCode;

/**
 * @author Cra2iTeT
 * @since 2023/5/1 11:49
 */
public class ObjectException extends RuntimeException {

    private long errorTime;
    private String errorType;
    private byte errorCode;
    private String log;

    private ObjectException() {
    }

    public ObjectException(String message) {
        this(message, "");
    }

    public ObjectException(String message, String log) {
        this(message, ObjectException.class.getName(), log);
    }

    public ObjectException(String message, String errorType, String log) {
        this(message, errorType, ExceptionCode.OBJECT_EXCEPTION, log);
    }

    public ObjectException(String message, String errorType, byte errorCode, String log) {
        super(message);
        this.errorCode = errorCode;
        this.errorTime = System.currentTimeMillis();
        this.errorType = errorType;
        this.log = log;
    }

    public long getErrorTime() {
        return errorTime;
    }

    public String getErrorType() {
        return errorType;
    }

    public String getLog() {
        return this.log;
    }

    public byte getCode() {
        return this.errorCode;
    }
}

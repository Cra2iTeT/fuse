package com.fuse.exception;

/**
 * @author Cra2iTeT
 * @since 2023/5/1 11:49
 */
public class ObjectException extends Exception {

    private long errorTime;
    private String errorType;
    private String log;

    private ObjectException() {
    }

    public ObjectException(String errorType) {
        this("", errorType,"");
    }

    public ObjectException(String message, String errorType, String log) {
        super(message);
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
}

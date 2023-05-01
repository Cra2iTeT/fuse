package com.fuse.exception;

/**
 * @author Cra2iTeT
 * @since 2023/4/29 22:26
 */
public class PythonScriptRunException extends ObjectException {
    public PythonScriptRunException() {
        this("", "");
    }

    public PythonScriptRunException(String message) {
        this(message, "");
    }

    public PythonScriptRunException(String message, String log) {
        super(message, "PythonScriptRunException.class", log);
    }
}

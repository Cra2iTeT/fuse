package com.fuse.exception;

/**
 * @author Cra2iTeT
 * @since 2023/4/29 22:26
 */
public class PythonScriptRunException extends ObjectException {
    public PythonScriptRunException() {
        super("PythonScriptRunException.class");
    }

    public PythonScriptRunException(String message) {
        super(message,"PythonScriptRunException.class");
    }
}

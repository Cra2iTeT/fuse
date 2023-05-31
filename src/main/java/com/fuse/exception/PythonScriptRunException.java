package com.fuse.exception;

import static com.fuse.common.ExceptionCode.PYTHON_SCRIPT_EXCEPTION;

/**
 * @author Cra2iTeT
 * @since 2023/4/29 22:26
 */
public class PythonScriptRunException extends ObjectException {

    public PythonScriptRunException(String message) {
        this(message, "");
    }

    public PythonScriptRunException(String message, String log) {
        super(message, PythonScriptRunException.class.getName(),
                PYTHON_SCRIPT_EXCEPTION, log);
    }
}

package com.fuse.config;

import org.springframework.stereotype.Component;

/**
 * @author Cra2iTeT
 * @since 2023/4/29 15:17
 */
public class SystemConfig {
    private SystemConfig(){}

    public static final String PYTHON_SCRIPT_Parent_PATH = "D:\\idea1\\fuse\\python\\script";
    public static final String PYTHON_SCRIPT_PREDICT_PATH = "predict.py";
    public static final String PYTHON_SCRIPT_TIME_DIVIDE_PATH = "TimeDivide.py";
    public static final String CSV_TEMPORARY_SAVE_PATH = "D:\\idea1\\fuse\\dateset";
}

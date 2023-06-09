package com.fuse.common;

/**
 * @author Cra2iTeT
 * @since 2023/4/29 14:12
 */
public enum SystemCode {

    CSV_RESOLVE_ERROR_TYPE_MISMATCH(10001, "只能上传CSV文件"),
    CSV_RESOLVE_SUCCESS(10002, "CSV文件解析成功"),
    CSV_RESOLVE_ERROR(10003, "CSV文件解析失败，请联系系统管理员"),
    CSV_LOAD_ERROR(10004, "CSV文件导入，请联系系统管理员"),
    PYTHON_SCRIPT_ERROR(10103, "python脚本执行异常，请联系管理员"),
    WEATHER_ERROR(10203, "天气脚本异常，请联系管理员"),
    MQ_ERROR(20003, "消息管理异常，请联系管理员"),

    SYSTEM_ERROR_RED(1001, "系统红色预警"),
    SYSTEM_ERROR_ORIGIN(1002, "系统橙色预警"),
    SYSTEM_ERROR_YELLOW(1003, "系统黄色预警"),
    SYSTEM_ERROR_BLUE(1004, "系统蓝色预警"),

    WEBSOCKET_CONNECT_OPEN(2001, "websocket连接建立成功"),
    WEBSOCKET_CONNECT_CLOSE(2002, "websocket连接关闭"),
    WEBSOCKET_CONNECT_ABNORMAL(2003, "websocket连接异常"),

    SUCCESS(200, "Success"),

    ERROR(401, "Error");

    private int code;
    private String msg;

    SystemCode(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return this.code;
    }

    public String getMsg() {
        return this.msg;
    }
}

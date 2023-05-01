package com.fuse.common;

/**
 * @author Cra2iTeT
 * @since 2023/4/29 14:12
 */
public enum SystemCode {

    CSV_RESOLVE_ERROR_TYPE_MISMATCH(10001, "只能上传CSV文件"),
    CSV_RESOLVE_SUCCESS(10002, "CSV文件解析成功"),
    CSV_RESOLVE_ERROR(10003, "CSV文件解析失败，请联系系统管理员"),
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

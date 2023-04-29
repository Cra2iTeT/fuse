package com.Cra2iTeT.commons;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author Cra2iTeT
 * @since 2023/3/5 23:27
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class R <T> implements Serializable {
    private int code;
    private String msg;
    private T data;

    public R(int code,String msg) {
        this(code,msg,null);
    }

    public R(int code) {
        this(code,null,null);
    }
}

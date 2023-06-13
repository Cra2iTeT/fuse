package com.fuse.util;

import com.fuse.exception.PythonScriptRunException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * @author Cra2iTeT
 * @since 2023/5/31 21:18
 */
public class PythonScriptUtils {
    public static BufferedReader invokePythonScript(String[] arguments) throws IOException, InterruptedException, PythonScriptRunException {
        Process process = Runtime.getRuntime().exec(arguments);
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        //waitFor是用来显示脚本是否运行成功，1表示失败，0表示成功，还有其他的表示其他错误
        System.out.println(process.waitFor());
        if (process.waitFor() != 0) {
            throw new PythonScriptRunException("python脚本执行错误,请与系统管理员联系");
        }


        return bufferedReader;
    }
}

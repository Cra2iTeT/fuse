package com.fuse.config.configure;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author Cra2iTeT
 * @since 2023/4/29 14:28
 */
@Component
public class SystemConfigure {

    @Value("${python.exe-path}")
    private String pythonExePath;

    public String getPythonExePath() {
        return pythonExePath;
    }

}

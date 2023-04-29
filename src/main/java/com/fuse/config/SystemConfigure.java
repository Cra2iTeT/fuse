package com.fuse.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author Cra2iTeT
 * @since 2023/4/29 14:28
 */
@Component
public class SystemConfig {

    @Value("${csv.temporary.save.path}")
    private String CsvTemporarySavePath;

    @Value("${python.path.exe}")
    private String pythonExePath;

    @Value("${python.path.script-parent}")
    private String pythonScriptParentPath;

    public String getCsvTemporarySavePath() {
        return CsvTemporarySavePath;
    }

    public String getPythonExePath() {
        return pythonExePath;
    }

    public String getPythonScriptParentPath() {
        return pythonScriptParentPath;
    }
}

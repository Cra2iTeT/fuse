package com.fuse.domain.to;

import com.fuse.domain.vo.CsvTimeDivideVo;
import org.springframework.validation.annotation.Validated;

/**
 * @author Cra2iTeT
 * @since 2023/4/29 23:49
 */
public class PredictTo extends CsvTimeDivideVo {
    private long predictStartTime;
    private long predictEndTime;
}

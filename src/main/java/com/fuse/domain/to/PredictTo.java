package com.fuse.domain.to;

import com.fuse.domain.vo.CsvTimeDivideVo;
import lombok.Data;
import org.springframework.validation.annotation.Validated;

/**
 * @author Cra2iTeT
 * @since 2023/4/29 23:49
 */
@Data
public class PredictTo extends CsvTimeDivideVo {
    private String region;
    private int fanId;
}

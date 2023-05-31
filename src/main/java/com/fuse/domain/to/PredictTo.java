package com.fuse.domain.to;

import com.fuse.domain.vo.CsvTimeDivideVo;
import lombok.Data;
import org.springframework.validation.annotation.Validated;

import java.util.List;

/**
 * @author Cra2iTeT
 * @since 2023/4/29 23:49
 */
@Data
public class PredictTo extends CsvTimeDivideVo {
    private String region;
    private List<Integer> fanIds;
}

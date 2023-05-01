package com.fuse.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author Cra2iTeT
 * @since 2023/4/29 14:07
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CsvTimeDivideVo implements Serializable {
    private String token;
    private long startTime;
    private long endTime;
}

package com.fuse.domain.pojo;

import com.alibaba.excel.annotation.ExcelProperty;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.apache.ibatis.annotations.Result;

import java.util.Date;

/**
 * @author Cra2iTeT
 * @since 2023/5/1 16:14
 */
@Data
public class PredictResult {

    @ExcelProperty(value = "TurbID" ,index = 0)
    private int fanId;


    @ExcelProperty(value = "Datetime" ,index = 1)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date datetime;

    @ExcelProperty(value = "ROUND(A.POWER,0)" ,index = 2)
    private String power;

    @ExcelProperty(value = "YD15" ,index = 3)
    private String yd15;

    private long time;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date date;

    private String region;


    public PredictResult(long time, String region, int fanId, String power, String yd15, Date date) {
        this.time = time;
        this.region = region;
        this.fanId = fanId;
        this.power = power;
        this.yd15 = yd15;
        this.date = date;
    }

    public PredictResult(int fanId, Date date, String power, String yd15) {
        this.fanId = fanId;
        this.date = date;
        this.power = power;
        this.yd15 = yd15;
    }

    public PredictResult() {

    }

    @Override
    public String toString() {
        return "PredictResult{" +
                "fanId=" + fanId +
                ", datetime=" + datetime +
                ", power='" + power + '\'' +
                ", yd15='" + yd15 + '\'' +
                ", time=" + time +
                ", date=" + date +
                ", region='" + region + '\'' +
                '}';
    }
}

package com.fuse.domain.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author Cra2iTeT
 * @since 2023/4/30 11:02
 */
@Data
public class CityWeatherEachHour {
    private String locationId;
    private String locationName;
    private long time;
    private Date date;
    private String temperature;
    private int windDirection;
    private int pressure;
    private String windSpeed;
    private String humidity;
}

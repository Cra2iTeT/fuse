package com.fuse.domain.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Cra2iTeT
 * @since 2023/4/30 11:02
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CityWeatherEachHour {
    private String locationId;
    private long time;
    private byte temperature;
    private int windDirection;
    private int pressure;
    private byte windSpeed;
    private byte humidity;
}
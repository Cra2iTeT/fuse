package com.fuse.listener;

import com.fuse.domain.pojo.ChinaCity;
import com.fuse.mapper.ChinaCityMapper;
import com.fuse.mapper.CityWeatherEachHourMapper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author Cra2iTeT
 * @since 2023/4/30 10:38
 */
@Component
public class WeatherUpdateService {

    private final ChinaCityMapper chinaCityMapper;

    private final CityWeatherEachHourMapper cityWeatherEachHourMapper;

    public WeatherUpdateService(ChinaCityMapper chinaCityMapper, CityWeatherEachHourMapper cityWeatherEachHourMapper) {
        this.chinaCityMapper = chinaCityMapper;
        this.cityWeatherEachHourMapper = cityWeatherEachHourMapper;
    }

    @Scheduled(fixedRate = 1000 * 60 * 60 * 22)
    public void updateWeather() {
        List<ChinaCity> chinaCities = fetchChinaCity();
    }

    private List<ChinaCity> fetchChinaCity() {
        return chinaCityMapper.getAllChinaCities();
    }

//    private
}

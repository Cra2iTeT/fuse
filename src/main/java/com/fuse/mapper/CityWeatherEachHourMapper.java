package com.fuse.mapper;

import com.fuse.domain.pojo.CityWeatherEachHour;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author Cra2iTeT
 * @since 2023/5/1 0:04
 */
@Mapper
public interface CityWeatherEachHourMapper {

    // TODO 批量导入sql语句没有写
    boolean saveOrUpdate(List<CityWeatherEachHour> cityWeatherEachHours);

    List<CityWeatherEachHour> get24hWeather(List<String> locationIds);

    List<CityWeatherEachHour> get48hWeather(List<String> locationIds);

    List<CityWeatherEachHour> get3dWeather(List<String> locationIds);
}

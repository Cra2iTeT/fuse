package com.fuse.mapper;

import com.fuse.domain.pojo.CityWeatherEachHour;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author Cra2iTeT
 * @since 2023/5/1 0:04
 */
@Mapper
public interface CityWeatherEachHourMapper {

    // TODO 批量导入sql语句没有

    int saveOrUpdate(CityWeatherEachHour cityWeatherEachHour);

    int save(CityWeatherEachHour cityWeatherEachHour);

    List<CityWeatherEachHour> get3dWeather(@Param("from") long from, @Param("to") long to, @Param("locationIds") List<String> locationIds);
}

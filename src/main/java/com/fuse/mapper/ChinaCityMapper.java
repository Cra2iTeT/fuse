package com.fuse.mapper;

import com.fuse.domain.pojo.ChinaCity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author Cra2iTeT
 * @since 2023/4/30 10:51
 */
@Mapper
public interface ChinaCityMapper {
    List<String> getFanCityIds();
    List<ChinaCity> getFanCities();
}

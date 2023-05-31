package com.fuse.mapper;

import com.fuse.domain.pojo.FanCity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author Cra2iTeT
 * @since 2023/5/10 10:12
 */
@Mapper
public interface FanCityMapper {
    List<FanCity> getAllFans();

    List<Integer> getFanId(String locationId);
}

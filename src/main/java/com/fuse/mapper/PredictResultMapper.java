package com.fuse.mapper;

import com.fuse.domain.pojo.PredictResult;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author Cra2iTeT
 * @since 2023/5/1 17:11
 */
@Mapper
public interface PredictResultMapper {
    int saveOrUpdate(PredictResult predictResults);
}

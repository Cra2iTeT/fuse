package com.fuse.mapper;

import com.fuse.domain.pojo.ErrorLog;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author Cra2iTeT
 * @since 2023/5/1 11:55
 */
@Mapper
public interface ErrorLogMapper {
    boolean saveErrorLog(ErrorLog errorLog);
}

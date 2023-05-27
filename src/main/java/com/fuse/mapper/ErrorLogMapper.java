package com.fuse.mapper;

import com.fuse.domain.pojo.ErrorLog;
import com.fuse.exception.ObjectException;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author Cra2iTeT
 * @since 2023/5/1 11:55
 */
@Mapper
public interface ErrorLogMapper {
    int save(ErrorLog errorLog);

    List<ErrorLog> getExceptionPage(@Param("code") byte code,
                                           @Param("current") int current,
                                           @Param("pageSize") int pageSize);
}

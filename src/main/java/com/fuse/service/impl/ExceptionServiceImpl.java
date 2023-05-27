package com.fuse.service.impl;

import com.fuse.domain.pojo.ErrorLog;
import com.fuse.exception.ObjectException;
import com.fuse.mapper.ErrorLogMapper;
import com.fuse.service.ExceptionService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author Cra2iTeT
 * @since 2023/5/27 15:39
 */
@Service
public class ExceptionServiceImpl implements ExceptionService {

    @Resource
    private ErrorLogMapper errorLogMapper;

    @Override
    public List<ErrorLog> getException(byte code, int current, int pageSize) {
        return errorLogMapper.getExceptionPage(code, current, pageSize);
    }
}

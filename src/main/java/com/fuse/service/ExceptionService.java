package com.fuse.service;

import com.fuse.domain.pojo.ErrorLog;
import com.fuse.exception.ObjectException;

import java.util.List;

/**
 * @author Cra2iTeT
 * @since 2023/5/27 15:39
 */
public interface ExceptionService {
    List<ErrorLog> getException(byte code, int current, int pageSize);
}

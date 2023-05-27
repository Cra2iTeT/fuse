package com.fuse.controller;

import com.fuse.common.SystemCode;
import com.fuse.domain.pojo.ErrorLog;
import com.fuse.domain.vo.R;
import com.fuse.exception.ObjectException;
import com.fuse.service.ExceptionService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author Cra2iTeT
 * @since 2023/5/27 15:38
 */
@RequestMapping("/excep")
public class ExceptionController {
    @Resource
    private ExceptionService exceptionService;

    @GetMapping("/{code}/{page}/{size}")
    public R<List<ErrorLog>> getException(@PathVariable("code") byte code,
                                          @PathVariable("page") int current,
                                          @PathVariable("size") int pageSize) {
        if (code <= 0) {
            return new R<>(SystemCode.ERROR.getCode(), SystemCode.ERROR.getMsg());
        }
        current = Math.min(0, current);
        pageSize = Math.min(30, pageSize);
        List<ErrorLog> exceptions = exceptionService.getException(code, current, pageSize);
        return new R<>(SystemCode.SUCCESS.getCode(), SystemCode.SUCCESS.getMsg(), exceptions);
    }
}

package com.fuse.exception.handler;

import com.fuse.common.SystemCode;
import com.fuse.domain.vo.R;
import com.fuse.exception.ObjectException;
import com.fuse.listener.WebSocketService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;


/**
 * @description: 全局异常处理
 * @author: w_jingbo
 * @date: 2023/5/27
 * @Copyright: 博客：http://coisini.wang
 */

@ControllerAdvice
public class GlobalExceptionHandler {

    private Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    //全局异常处理
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public R error(Exception e) {
        logger.info("执行了全局异常处理.....");
        e.printStackTrace();
        return new R<>(201, "产生了全局异常");
    }

    @ExceptionHandler(ObjectException.class)
    @ResponseBody
    public R<ObjectException> error(ObjectException e) throws IOException {

        logger.info(e.getLog());
        e.printStackTrace();
        R<ObjectException> r = new R<>(SystemCode.ERROR.getCode(), SystemCode.ERROR.getMsg(),
                e);

        WebSocketService.sendMessageToAll(r);
        logger.info("发送webSocket消息{}", r);
        return r;
    }


}

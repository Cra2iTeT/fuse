package com.fuse;

import com.fuse.domain.vo.R;
import com.fuse.exception.GlobalExceptionHandler;
import com.fuse.exception.ObjectException;
import com.fuse.listener.WebSocketService;
import com.fuse.service.PredictService;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;

/**
 * @description:
 * @author: w_jingbo
 * @date: 2023/5/27
 * @Copyright: 博客：http://coisini.wang
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class WebSocketTest {

    private Logger logger = LoggerFactory.getLogger(WebSocketTest.class);

    @Autowired
    private PredictService predictService;

    @Autowired
    private WebSocketService webSocketService;

    @Test
    public void testSendMessage() throws IOException {
        R r = new R(11,"11");
        WebSocketService.sendMessageToAll(r);
    }
}

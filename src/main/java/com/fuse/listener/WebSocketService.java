package com.fuse.listener;

import cn.hutool.json.JSONUtil;
import com.fuse.common.SystemCode;
import com.fuse.domain.vo.R;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * @author Cra2iTeT
 * @since 2023/5/1 14:34
 */
@Component
@ServerEndpoint(value = "/websocket")
public class WebSocketService {
    private static final CopyOnWriteArraySet<WebSocketService> webSocketSet = new CopyOnWriteArraySet<>();

    private static final Set<Session> sessions = Collections.synchronizedSet(new HashSet<>());

    private Logger logger = LoggerFactory.getLogger(WebSocketService.class);

    @OnOpen
    public void onOpen(Session session) {
        sessions.add(session);
        webSocketSet.add(this);
        logger.info("webSocket连接成功！");
    }

    @OnClose
    public void onClose() {
        webSocketSet.remove(this);
    }

    @OnMessage
    public void onMsg(Session session,String message) throws IOException {
        logger.info("接收到前端的消息：{}",message);

    }

    //向所有客户端发送消息（广播）
    public static void sendMessageToAll(R r) throws IOException {
        for (Session session : sessions) {
            if (session.isOpen()) {
                session.getBasicRemote().sendText(r.getMsg());
            }
        }
    }

}
package com.fuse.listener;

import cn.hutool.json.JSONUtil;
import com.fuse.common.SystemCode;
import com.fuse.domain.vo.R;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * @author Cra2iTeT
 * @since 2023/5/1 14:34
 */
@ServerEndpoint(value = "/websocket")
public class WebSocketService {
    private static final CopyOnWriteArraySet<WebSocketService> webSocketSet = new CopyOnWriteArraySet<>();

    private Session session;

    @OnOpen
    public void onOpen(Session session) {
        this.session = session;
        webSocketSet.add(this);
        sendMessage(new R<>(SystemCode.WEBSOCKET_CONNECT_OPEN.getCode(),
                SystemCode.WEBSOCKET_CONNECT_OPEN.getMsg(), null));
    }

    @OnClose
    public void onClose() {
        webSocketSet.remove(this);
    }

    private void sendMessage(R r) {
        this.session.getAsyncRemote().sendText(JSONUtil.toJsonStr(r));
    }

    public static void sendGroupMessage(R r) {
        for (WebSocketService webSocketService : webSocketSet) {
            webSocketService.sendMessage(r);
        }
    }

}

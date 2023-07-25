package com.cj.studycirclebackend.websocket;

import com.alibaba.fastjson2.JSONObject;
import com.cj.studycirclebackend.pojo.Letter;
import com.cj.studycirclebackend.service.LetterService;
import com.cj.studycirclebackend.vo.LetterOverviewVO;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class WebSocketHandler extends TextWebSocketHandler {
    private static final Logger logger = LoggerFactory.getLogger(WebSocketHandler.class);
    @Resource
    private LetterService letterService;
    public Map<Long, WebSocketSession> clients = new ConcurrentHashMap<>();
    public Map<WebSocketSession, Long> mapping = new ConcurrentHashMap<>();
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        // 在建立连接后触发，可以执行初始化逻辑
        String[] pathSegments = session.getUri().getPath().split("/");
        Long userId = Long.parseLong(pathSegments[pathSegments.length - 1]);

        logger.info("open: {}", session);
        this.clients.put(userId, session);
        this.mapping.put(session, userId);
        super.afterConnectionEstablished(session);
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        // 1. 处理接收到的 WebSocket 消息
        String payload = message.getPayload();
        // 2. 转换为对象
        LetterRequest request = JSONObject.parseObject(payload, LetterRequest.class);
        logger.info("【session】 {}, send letter: {}", session, request);
        Letter letter = letterService.saveLetter(request);
        LetterOverviewVO letterOverviewVO = letterService.getLetterOverviewVO(letter);
        // 3. 检查目标用户是否存在
        WebSocketSession webSocketSession = clients.get(request.getUserToId());
        // 4.0 不存在则不进行后续操作
        if (webSocketSession == null) {
            return;
        }
        // 4.1 存在则发送消息
        webSocketSession.sendMessage(new TextMessage(JSONObject.toJSONString(letterOverviewVO)));
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        // 在连接关闭后触发，进行一些清理工作
        Long userId = mapping.get(session);
        if (userId != null) {
            mapping.remove(session);
            clients.remove(userId);
        }
        logger.info("close websocket: {}" , session);
        super.afterConnectionClosed(session, status);
    }
}

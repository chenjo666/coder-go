package com.cj.codergobackend.websocket;

import com.alibaba.fastjson2.JSONObject;
import com.cj.codergobackend.constants.LetterType;
import com.cj.codergobackend.dto.LetterMessage;
import com.cj.codergobackend.dto.LetterPing;
import com.cj.codergobackend.dto.LetterResponse;
import com.cj.codergobackend.pojo.Letter;
import com.cj.codergobackend.service.LetterService;
import com.cj.codergobackend.vo.LetterOverviewVO;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.*;

@Component
public class WebSocketHandler extends TextWebSocketHandler {
    private static final Logger logger = LoggerFactory.getLogger(WebSocketHandler.class);
    @Resource
    private LetterService letterService;
    private final Map<String, WebSocketSession> clients = new ConcurrentHashMap<>();
    private final Map<WebSocketSession, String> mapping = new ConcurrentHashMap<>();
    private final CopyOnWriteArraySet<WebSocketSession> sessions = new CopyOnWriteArraySet<>();

    // 保存每个会话的最后一次通信时间，如果大于 60s 未响应 pong 或未发送数据，则断开连接
    private final Map<WebSocketSession, Long> lastResponseTimes = new ConcurrentHashMap<>();
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        // 在建立连接后触发，可以执行初始化逻辑
        String[] pathSegments = session.getUri().getPath().split("/");
        String userId = pathSegments[pathSegments.length - 1];

        logger.info("open: {}", session);
        this.clients.put(userId, session);
        this.mapping.put(session, userId);
        this.sessions.add(session);
        super.afterConnectionEstablished(session);
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        // 1. 处理接收到的 WebSocket 消息
        String payload = message.getPayload();
        // 2. 转换为对象
        LetterMessage msg = JSONObject.parseObject(payload, LetterMessage.class);
        // 响应 pong 消息
        if (msg.getType() == LetterType.MESSAGE_PONG) {
            handlerPing(session);
        }
        // 请求 data 消息
        if (msg.getType() == LetterType.MESSAGE_REQUEST) {
            logger.info("data:{}", msg);
            Map<String, String> map = (Map<String, String>) msg.getData();
            handlerRequest(msg.getFrom(), msg.getTo(), map.get("content"), map.get("time"));
        }
        // 3. 记录响应时间
        lastResponseTimes.put(session, System.currentTimeMillis());
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        closeConnect(session);
        super.afterConnectionClosed(session, status);
    }
    // 10s 发送心跳
    @Scheduled(fixedRate = 10000)
    private void triggerSendHeartbeat() {
        if (sessions.isEmpty()) {
            return;
        }
        logger.info("ping: {}", sessions);
        for (WebSocketSession session : sessions) {
            if (session.isOpen()) {
                try {
                    String userId = mapping.get(session);
                    LetterMessage msg = LetterMessage.builder()
                            .from("0")
                            .to(userId)
                            .type(LetterType.MESSAGE_PING)
                            .data(new LetterPing())
                            .build();
                    session.sendMessage(new TextMessage(JSONObject.toJSONString(msg)));
                    logger.info("发送心跳: {}", session);
                } catch (Exception e) {
                    logger.error("发生错误: {}", session, e);
                }
            }
        }
    }

    // 60s 检测响应
    @Scheduled(fixedRate = 60000)
    private void triggerCheckResponse() throws IOException {
        if (lastResponseTimes.isEmpty()) {
            return;
        }
        logger.info("check response: {}", lastResponseTimes);
        long currentTime = System.currentTimeMillis();

        for (WebSocketSession session : lastResponseTimes.keySet()) {
            long lastResponseTime = lastResponseTimes.get(session);
            if (currentTime - lastResponseTime > 60000) { // 60s 未响应
                closeConnect(session);
            }
        }
    }

    // 关闭连接: 1. 正常断开关闭        2. 心跳异常关闭
    private void closeConnect(WebSocketSession session) throws IOException {
        // 清理连接
        String userId = mapping.get(session);
        if (userId != null) {
            mapping.remove(session);
            clients.remove(userId);
            sessions.remove(session);
            lastResponseTimes.remove(session);
        }
        // 关闭连接
        if (session.isOpen()) {
            session.close();
        }
        logger.info("close websocket: {}" , session);
    }


    // 处理心跳响应
    private void handlerPing(WebSocketSession session) {
        logger.info("收到 pong: [{}, {}]", session, System.currentTimeMillis());
    }

    // 处理正常请求
    private void handlerRequest(String userFromId, String userToId, String content, String time) throws IOException {
        // 0. 创建一个私信记录
        Letter letter = letterService.saveLetter(Long.parseLong(userFromId), Long.parseLong(userToId), content, time);
        // 1. 检查目标用户是否存在
        WebSocketSession webSocketSession = clients.get(userToId);
        // 1.1 不存在则不进行后续操作
        if (webSocketSession == null) {
            return;
        }
        // 1.2 存在则发送消息
        LetterMessage message = LetterMessage.builder()
                .from(userFromId)
                .to(userToId)
                .type(LetterType.MESSAGE_RESPONSE)
                .data(getResponse(letterService.getLetterOverviewVO(letter.getUserFromId(), letter.getUserToId())))
                .build();
        webSocketSession.sendMessage(new TextMessage(JSONObject.toJSONString(message)));
        logger.info("send message: {}", message);
    }

    private LetterResponse getResponse(LetterOverviewVO letterOverviewVO) {
        return LetterResponse.builder()
                .userId(String.valueOf(letterOverviewVO.getUserId()))
                .userName(letterOverviewVO.getUserName())
                .userAvatar(letterOverviewVO.getUserAvatar())
                .isStar(letterOverviewVO.isStar())
                .isFan(letterOverviewVO.isFan())
                .isBlock(letterOverviewVO.isBlock())
                .newContent(letterOverviewVO.getNewContent())
                .newDate(letterOverviewVO.getNewDate())
                .totalUnread(letterOverviewVO.getTotalUnread())
                .build();
    }
}

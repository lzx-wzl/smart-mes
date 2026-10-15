package com.lzx.smartmes.device.websocket;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.concurrent.CopyOnWriteArraySet;

@Component
@Slf4j
public class DeviceStatusWebSocketHandler extends TextWebSocketHandler {

    private final CopyOnWriteArraySet<WebSocketSession> sessions =
            new CopyOnWriteArraySet<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        sessions.add(session);
    }

    @Override
    public void afterConnectionClosed(
            WebSocketSession session,
            CloseStatus status
    ) {
        sessions.remove(session);
    }

    public void broadcast(String payload) {

        for (WebSocketSession session : sessions) {

            try {

                if (session.isOpen()) {
                    session.sendMessage(
                            new TextMessage(payload)
                    );
                }

            } catch (IOException e) {

                log.warn(
                        "websocket delivery failed",
                        e
                );
            }
        }
    }
}
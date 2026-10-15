package com.lzx.smartmes.device.config;

import com.lzx.smartmes.device.websocket.DeviceStatusWebSocketHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketConfigurer {

    private final DeviceStatusWebSocketHandler handler;

    @Override
    public void registerWebSocketHandlers(
            WebSocketHandlerRegistry registry
    ) {

        registry
                .addHandler(
                        handler,
                        "/ws/device-status"
                )
                .setAllowedOriginPatterns("*");
    }
}
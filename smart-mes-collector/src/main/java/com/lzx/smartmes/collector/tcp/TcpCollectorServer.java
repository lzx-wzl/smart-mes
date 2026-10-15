package com.lzx.smartmes.collector.tcp;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lzx.smartmes.collector.service.TelemetryPublisher;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

@Component
@Slf4j
public class TcpCollectorServer {
    private final TelemetryPublisher publisher;
    private final ObjectMapper json;
    private final int port;
    private volatile boolean running = true;
    private ServerSocket server;
    public TcpCollectorServer(
            TelemetryPublisher publisher,
            ObjectMapper json,
            @Value("${smart-mes.tcp-port:9009}") int port
    ) {
        this.publisher = publisher;
        this.json = json;
        this.port = port;
    }
    @PostConstruct
    void start() {
        new Thread(() -> {
                    while (running) {
                        try {
                            server = new ServerSocket(port);
                            log.info("PLC collector listening on {}", port);
                            while (running) {
                                Socket socket = server.accept();
                                new Thread(() -> read(socket), "plc-client").start();
                            }
                        } catch (IOException e) {
                            if (running) {
                                log.error(
                                        "TCP collector socket error", e
                                );
                                try {
                                    Thread.sleep(1000);
                                } catch (InterruptedException ignored) {
                                    Thread.currentThread()
                                            .interrupt();
                                }
                            }
                        }
                    }
                },
                "plc-tcp-collector"
        ).start();
    }
    private void read(Socket socket) {
        try (
                socket;
                BufferedReader in =
                        new BufferedReader(
                                new InputStreamReader(
                                        socket.getInputStream()
                                )
                        )
        ) {
            log.info(
                    "PLC connected: {}",
                    socket.getRemoteSocketAddress()
            );
            String line;
            while ((line = in.readLine()) != null) {
                try {
                    publisher.publish(json.readTree(line)
                    );
                } catch (Exception e) {
                    log.warn(
                            "invalid PLC line dropped: {}",
                            e.getMessage()
                    );
                }
            }
        } catch (IOException e) {
            log.info(
                    "PLC disconnected: {}",
                    e.getMessage()
            );
        }
    }
    @PreDestroy
    void stop() throws IOException {
        running = false;
        if (server != null) {
            server.close();
        }
    }
}
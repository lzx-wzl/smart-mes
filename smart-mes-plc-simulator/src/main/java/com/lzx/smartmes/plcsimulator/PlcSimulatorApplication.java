package com.lzx.smartmes.plcsimulator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class PlcSimulatorApplication {

    public static void main(String[] args) {
        SpringApplication.run(PlcSimulatorApplication.class, args);
    }
}
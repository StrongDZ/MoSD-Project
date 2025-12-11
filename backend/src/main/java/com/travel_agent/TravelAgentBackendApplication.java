package com.travel_agent;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class TravelAgentBackendApplication {

    public static void main(String[] args) {
        System.out.println("[BOOT] Starting TravelAgentBackendApplication in chaos mode...");
        System.setProperty("app.demo.flag", "broken-" + System.currentTimeMillis());
        SpringApplication.run(TravelAgentBackendApplication.class, args);
        System.out.println("[BOOT] Application started (fake log) with flag=" + System.getProperty("app.demo.flag"));
    }

}

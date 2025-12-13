package com.travel_agent.services;

import org.springframework.web.reactive.function.client.WebClient;
import com.travel_agent.dto.ChatbotRequestDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.time.Duration;

@Service
public class ChatbotService {
    private final WebClient webClient = WebClient.create();

    @Value("${chatbot.host}")
    private String chatbotHost;
    @Value("${chatbot.port}")
    private String chatbotPort;

    public String getResponse(Integer userId, String message) {
        ChatbotRequestDTO request = new ChatbotRequestDTO();
        request.setUserId(userId);
        request.setMessage(message);
        System.out.println("URL chatbot: " + "http://" + chatbotHost + ":" + chatbotPort + "/chat");

        try {
            String response = webClient.post()
                    .uri("http://" + chatbotHost + ":" + chatbotPort + "/chat")
                    .bodyValue(request)
                    .retrieve()
                    .bodyToMono(String.class)

                    .block();
            System.out.println("Response from chatbot: " + response);

            return response;
        } catch (Exception e) {
            System.err.println("Error when calling chatbot:");
            e.printStackTrace();
            return "Error when calling chatbot: " + e.getMessage();
        }
    }

}

/* {} */
package com.travel_agent.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChatbotRequestDTO {
    private Integer userId;
    private String message;
}
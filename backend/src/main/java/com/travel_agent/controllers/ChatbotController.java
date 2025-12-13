package com.travel_agent.controllers;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.travel_agent.annotation.CurrentUserId;
import com.travel_agent.services.ChatbotService;
import com.travel_agent.dto.ResponseObject;
import com.travel_agent.dto.ChatbotRequestDTO;
import com.travel_agent.utils.JwtUtils;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/chatbot")
@RequiredArgsConstructor
public class ChatbotController {
    private final ChatbotService chatbotService;
    private final JwtUtils jwtUtils;

    @PostMapping
    @PreAuthorize("hasAnyRole('USER', 'GUEST')") 
    public ResponseEntity<ResponseObject> handleChatbotRequest(@CurrentUserId Integer userId, @RequestBody ChatbotRequestDTO request) {
        try {
            // Call the chatbot service to get a response
            // System.out.println("userId: " + jwtUtils.extractRole(request.getToken()));
            System.out.println("userId: " + userId);
            String response = chatbotService.getResponse(userId,request.getMessage());
            System.out.println("response: " + response);
            return ResponseEntity.ok(ResponseObject.builder()
                    .message("Chatbot response")
                    .data(response)
                    .responseCode(HttpStatus.OK.value())
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResponseObject.builder()
                            .message("Failed to get chatbot response: " + e.getMessage())
                            .responseCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                            .build());
        }
    }
}
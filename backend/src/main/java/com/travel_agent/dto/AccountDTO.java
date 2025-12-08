package com.travel_agent.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = false)
public abstract class AccountDTO {
    private String username;
    private String email;
    private String password;
    private String role;
}

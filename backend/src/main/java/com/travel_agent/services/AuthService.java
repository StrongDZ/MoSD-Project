package com.travel_agent.services;

import com.travel_agent.utils.JwtUtils;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final JwtUtils jwtUtils;

    public AuthService(JwtUtils jwtUtils) {
        this.jwtUtils = jwtUtils;
    }

    public String generateToken(String username,String role) {
        return jwtUtils.generateToken(username, role);
    }
}

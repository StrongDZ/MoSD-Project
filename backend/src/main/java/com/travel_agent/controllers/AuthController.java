package com.travel_agent.controllers;

import com.travel_agent.dto.CompanyDTO;
import com.travel_agent.dto.LoginRequest;
import com.travel_agent.dto.LoginResponse;
import com.travel_agent.dto.ResponseObject;
import com.travel_agent.services.AuthService;
import com.travel_agent.services.UserService;

import lombok.RequiredArgsConstructor;

import com.travel_agent.services.CompanyService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.travel_agent.dto.UserDTO;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final UserService userService;
    private final CompanyService companyService;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/login/user")
    public ResponseEntity<ResponseObject> loginUser(@RequestBody LoginRequest request) {
        try {
            // Check if the username is username or email then find the user
            UserDTO user = userService.findByUsernameOrEmail(request.getUsername());

            System.out.println(user);
            
            // Kiểm tra password - hỗ trợ cả plain text và BCrypt
            boolean passwordMatch;
            if (user.getPassword().startsWith("$2a$") || user.getPassword().startsWith("$2b$")) {
                // Password đã được hash bằng BCrypt
                passwordMatch = passwordEncoder.matches(request.getPassword(), user.getPassword());
            } else {
                // Password là plain text (chưa hash)
                passwordMatch = request.getPassword().equals(user.getPassword());
            }
            
            if (!passwordMatch) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseObject.builder()
                        .message("Invalid password")
                        .responseCode(HttpStatus.BAD_REQUEST.value())
                        .build());
            }

            String token = authService.generateToken(user.getUsername(), user.getRole());
            return ResponseEntity.ok(ResponseObject.builder()
                    .message("User logged in successfully")
                    .data(new LoginResponse(token, user.getRole(), user.getUsername()))
                    .responseCode(HttpStatus.OK.value())
                    .build());

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ResponseObject.builder()
                    .message("Invalid username or password")
                    .responseCode(HttpStatus.UNAUTHORIZED.value())
                    .build());
        }
    }

    @PostMapping("/login/company")
    public ResponseEntity<ResponseObject> loginCompany(@RequestBody LoginRequest request) {
        try {
            CompanyDTO company = companyService.findByUsernameOrEmail(request.getUsername());

            // Kiểm tra password bằng passwordEncoder.matches()
            if (!request.getPassword().equals(company.getPassword())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseObject.builder()
                        .message("Invalid password")
                        .responseCode(HttpStatus.BAD_REQUEST.value())
                        .build());
            }

            String token = authService.generateToken(company.getUsername(), company.getRole());
            return ResponseEntity.ok(ResponseObject.builder()
                    .message("Company logged in successfully")
                    .data(new LoginResponse(token, company.getRole(), company.getUsername()))
                    .responseCode(HttpStatus.OK.value())
                    .build());

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ResponseObject.builder()
                    .message("Invalid username or password")
                    .responseCode(HttpStatus.UNAUTHORIZED.value())
                    .build());
        }
    }
}


package com.travel_agent.controllers;

import com.travel_agent.services.UserService;
import com.travel_agent.annotation.CurrentUserId;
import com.travel_agent.dto.ResponseObject;
import com.travel_agent.dto.UserDTO;
import com.travel_agent.exceptions.ReflectionException;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;

// import java.time.LocalDate;
// import java.util.List;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping
    @PreAuthorize("hasRole('GUEST')")
    public ResponseEntity<ResponseObject> createUser(@RequestBody UserDTO userDTO) {
        String username = userDTO.getUsername();
        String password = userDTO.getPassword();
        String email = userDTO.getEmail();
        String role = userDTO.getRole() == null ? "user" : userDTO.getRole();
        if (username == null || password == null || email == null || !role.equals("user")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseObject.builder()
                    .message("Invalid input data")
                    .responseCode(HttpStatus.BAD_REQUEST.value())
                    .build());
        }
        
        UserDTO createdUser = userService.createUser(
            userDTO);


        return ResponseEntity.status(HttpStatus.CREATED).body(ResponseObject.builder()
                .message("User created successfully")
                .data(createdUser)
                .responseCode(HttpStatus.CREATED.value())
                .build());
    }

    @GetMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ResponseObject> getUserById(@CurrentUserId Integer userId) {
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseObject.builder()
                    .message("User has not logged in!")
                    .responseCode(HttpStatus.BAD_REQUEST.value())
                    .build());
        }

        UserDTO user = userService.getUserById(userId);
        return ResponseEntity.ok(ResponseObject.builder()
                .message("User retrieved successfully")
                .data(user)
                .responseCode(HttpStatus.OK.value())
                .build());
    }

    
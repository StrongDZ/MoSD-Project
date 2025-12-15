package com.travel_agent.controllers;

import com.travel_agent.annotation.CurrentUserId;
import com.travel_agent.dto.ResponseObject;
import com.travel_agent.dto.UserDTO;
import com.travel_agent.exceptions.ReflectionException;
import com.travel_agent.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // ================== PUBLIC / GUEST ENDPOINTS ==================

    @PostMapping
    @PreAuthorize("hasRole('GUEST')")
    public ResponseEntity<ResponseObject> createUser(@RequestBody UserDTO userDTO) {
        if (!isValidRegistrationData(userDTO)) {
            return buildResponse(HttpStatus.BAD_REQUEST, "Invalid input data", null);
        }

        UserDTO createdUser = userService.createUser(userDTO);
        return buildResponse(HttpStatus.CREATED, "User created successfully", createdUser);
    }

    // ================== AUTHENTICATED USER ENDPOINTS ==================

    @GetMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ResponseObject> getUserById(@CurrentUserId Integer userId) {
        if (userId == null) return buildNotLoggedInResponse();

        UserDTO user = userService.getUserById(userId);
        return buildResponse(HttpStatus.OK, "User retrieved successfully", user);
    }

    @PutMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ResponseObject> updateUser(@CurrentUserId Integer userId, 
                                                     @RequestBody UserDTO userDTO) throws ReflectionException {
        if (userId == null) return buildNotLoggedInResponse();

        UserDTO updatedUser = userService.updateUser(userId, userDTO);
        return buildResponse(HttpStatus.OK, "User updated successfully", updatedUser);
    }

    @DeleteMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ResponseObject> deleteUser(@CurrentUserId Integer userId) {
        if (userId == null) return buildNotLoggedInResponse();

        userService.deleteUser(userId);
        return buildResponse(HttpStatus.OK, "User deleted successfully", null);
    }

    // ================== HELPER METHODS (PRIVATE) ==================

    /**
     * Kiểm tra dữ liệu đầu vào cho việc tạo user
     */
    private boolean isValidRegistrationData(UserDTO userDTO) {
        String role = userDTO.getRole() == null ? "user" : userDTO.getRole();
        return userDTO.getUsername() != null 
                && userDTO.getPassword() != null 
                && userDTO.getEmail() != null 
                && role.equals("user");
    }

    /**
     * Hàm helper để tạo ResponseEntity nhanh gọn, tránh lặp code
     */
    private ResponseEntity<ResponseObject> buildResponse(HttpStatus status, String message, Object data) {
        return ResponseEntity.status(status).body(ResponseObject.builder()
                .message(message)
                .responseCode(status.value())
                .data(data)
                .build());
    }

    /**
     * Hàm helper riêng cho lỗi chưa đăng nhập (được dùng lặp lại nhiều lần)
     */
    private ResponseEntity<ResponseObject> buildNotLoggedInResponse() {
        return buildResponse(HttpStatus.BAD_REQUEST, "User has not logged in!", null);
    }
}
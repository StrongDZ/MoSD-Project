package com.travel_agent.services;

import com.travel_agent.dto.UserDTO;
import com.travel_agent.exceptions.ReflectionException;
import com.travel_agent.mappers.UserMapper;
import com.travel_agent.models.entity.UserEntity;
import com.travel_agent.repositories.UserRepository;
import com.travel_agent.utils.ReflectionUtils;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    // ================== CRUD OPERATIONS ==================

    // CREATE
    public UserDTO createUser(UserDTO userDTO) {
        UserEntity user = new UserEntity();
        
        // Mapping thủ công (Nên chuyển logic này vào Mapper nếu có thể)
        user.setUsername(userDTO.getUsername());
        user.setEmail(userDTO.getEmail());
        user.setDob(userDTO.getDob());
        user.setRole(userDTO.getRole());
        
        // Luôn mã hóa mật khẩu trước khi lưu
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));

        UserEntity savedUser = userRepository.save(user);
        return userMapper.toUserDTO(savedUser);
    }

    // READ (By ID)
    public UserDTO getUserById(Integer userId) {
        UserEntity user = getUserEntityById(userId);
        return userMapper.toUserDTO(user);
    }

    // UPDATE
    public UserDTO updateUser(Integer userId, UserDTO userDTO) throws ReflectionException {
        UserEntity user = getUserEntityById(userId);

        // Cập nhật các trường không null từ DTO sang Entity
        ReflectionUtils.updateEntityFields(user, userDTO);
        
        // Nếu userDTO có đổi pass, cần encode lại (Logic bổ sung để an toàn)
        if (userDTO.getPassword() != null && !userDTO.getPassword().isEmpty()) {
             user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        }

        UserEntity updatedUser = userRepository.save(user);
        return userMapper.toUserDTO(updatedUser);
    }

    // DELETE
    public void deleteUser(Integer userId) {
        UserEntity user = getUserEntityById(userId);
        userRepository.delete(user);
    }

    // LIST ALL
    public List<UserDTO> getAllUsers() {
        List<UserEntity> users = userRepository.findAll();
        return userMapper.toUserDTOs(users); // Đổi tên hàm mapper số nhiều cho chuẩn
    }

    // ================== SEARCH & AUTHENTICATION ==================

    public UserDTO findByUsernameOrEmail(String username) {
        UserEntity user = userRepository.findByUsernameOrEmail(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found with username/email: " + username));
        return userMapper.toUserDTO(user);
    }

    // Logic này đã được viết lại để hỗ trợ Password Encoder
    public UserDTO findByUsernameAndPassword(String username, String rawPassword) {
        // 1. Tìm user theo username trước
        UserEntity user = userRepository.findByUsernameOrEmail(username)
                .orElseThrow(() -> new IllegalArgumentException("Invalid username or password"));

        // 2. So sánh password raw với password đã mã hóa trong DB
        if (!passwordEncoder.matches(rawPassword, user.getPassword())) {
            throw new IllegalArgumentException("Invalid username or password");
        }

        return userMapper.toUserDTO(user);
    }

    // ================== PRIVATE HELPERS ==================

    // Helper để tìm User hoặc ném lỗi (Tránh lặp code)
    private UserEntity getUserEntityById(Integer userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));
    }
}
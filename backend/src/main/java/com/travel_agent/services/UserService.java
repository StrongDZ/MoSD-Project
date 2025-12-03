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

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    // CREATE: Tạo mới User
    public UserDTO createUser(UserDTO userDTO) {
        // Tạo UserEntity liên kết với Account
        UserEntity user = new UserEntity();
        user.setDob(userDTO.getDob());
        user.setUsername(userDTO.getUsername());
        user.setEmail(userDTO.getEmail());
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        user.setRole(userDTO.getRole());

        userRepository.save(user);

        return userMapper.UserToUserDTO(user);
    }

    // READ: Tìm User theo ID
    public UserDTO getUserById(Integer userId) {
        UserEntity user= userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));
        return userMapper.UserToUserDTO(user);
    }

    // UPDATE: Cập nhật thông tin User
    public UserDTO updateUser(Integer userId, UserDTO userDTO) throws ReflectionException {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));

        ReflectionUtils.updateEntityFields(user, userDTO);
        userRepository.save(user);
        return userMapper.UserToUserDTO(user);
    }

    // DELETE: Xóa User theo ID
    public void deleteUser(Integer userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));
        userRepository.delete(user);
    }

    // LIST ALL: Lấy tất cả User
    public List<UserDTO> getAllUsers() {
        List<UserEntity> users =  userRepository.findAll();
        return userMapper.UsersToUserDTOs(users);
    }

    // Find user by username
    public UserDTO findByUsernameOrEmail(String username) {
        UserEntity user = userRepository.findByUsernameOrEmail(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found with username: " + username));
        return userMapper.UserToUserDTO(user);
    }

    // Find user by username and password
    public UserDTO findByUsernameAndPassword(String username, String password) {
        UserEntity user = userRepository.findByUsernameAndPassword(username, password)
                .orElseThrow(() -> new IllegalArgumentException("Invalid username or password"));
        return userMapper.UserToUserDTO(user);
    }
}

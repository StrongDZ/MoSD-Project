package com.travel_agent.mappers;

import java.util.List;

import org.mapstruct.Mapper;

import com.travel_agent.dto.UserDTO;
import com.travel_agent.models.entity.UserEntity;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserDTO toUserDTO(UserEntity user);
    List<UserDTO> toUserDTOs(List<UserEntity> users);
}

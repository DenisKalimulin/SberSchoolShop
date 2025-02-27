package ru.kalimulin.mappers.userMapper;

import ru.kalimulin.dto.userDTO.UserRegistrationDTO;
import ru.kalimulin.dto.userDTO.UserResponseDTO;
import ru.kalimulin.models.User;

import java.util.List;


public interface UserMapper {

    UserResponseDTO toUserResponseDTO(User user);

    User toUser(UserRegistrationDTO userRegistrationDTO);

    List<UserResponseDTO> toUserResponseDTOList(List<User> users);

}
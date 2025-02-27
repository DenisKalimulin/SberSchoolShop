package ru.kalimulin.mappers.userMapper;

import org.springframework.stereotype.Component;
import ru.kalimulin.dto.userDTO.UserRegistrationDTO;
import ru.kalimulin.dto.userDTO.UserResponseDTO;
import ru.kalimulin.models.User;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class UserMapperImpl implements UserMapper {

    @Override
    public UserResponseDTO toUserResponseDTO(User user) {
        if (user == null) {
            return null;
        }

        // Проверка на null для roles
        Set<String> roleNames = (user.getRoles() != null) ? user.getRoles().stream()
                .map(role -> role.getRoleName().name()) // Преобразуем Enum RoleName в String
                .collect(Collectors.toSet()) : Set.of(); // Если roles == null, возвращаем пустое множество

        return UserResponseDTO.builder()
                .id(user.getId())
                .login(user.getLogin())
                .email(user.getEmail())
                .roles(roleNames)
                .build();
    }

    @Override
    public User toUser(UserRegistrationDTO userRegistrationDTO) {
        if (userRegistrationDTO == null) {
            return null;
        }

        return User
                .builder()
                .login(userRegistrationDTO.getLogin())
                .email(userRegistrationDTO.getEmail())
                .password(userRegistrationDTO.getPassword())
                .build();
    }

    @Override
    public List<UserResponseDTO> toUserResponseDTOList(List<User> users) {
        if (users == null || users.isEmpty()) {
            return List.of();
        }

        return users.stream()
                .map(this::toUserResponseDTO) // Преобразуем каждого пользователя в DTO
                .collect(Collectors.toList()); // Собираем в List
    }
}
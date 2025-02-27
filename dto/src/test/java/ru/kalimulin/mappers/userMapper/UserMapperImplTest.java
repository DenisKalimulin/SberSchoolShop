package ru.kalimulin.mappers.userMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.kalimulin.dto.userDTO.UserRegistrationDTO;
import ru.kalimulin.dto.userDTO.UserResponseDTO;
import ru.kalimulin.models.Role;
import ru.kalimulin.models.User;
import ru.kalimulin.enums.RoleName;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class UserMapperImplTest {

    private UserMapperImpl userMapper;

    @BeforeEach
    void setUp() {
        userMapper = new UserMapperImpl();
    }

    @Test
    void toUserResponseDTO() {
        User user = User.builder()
                .id(1L)
                .login("testUser")
                .email("test@example.com")
                .roles(Set.of(new Role(1L, RoleName.BUYER)))
                .build();

        UserResponseDTO responseDTO = userMapper.toUserResponseDTO(user);

        assertThat(responseDTO).isNotNull();
        assertThat(responseDTO.getId()).isEqualTo(1L);
        assertThat(responseDTO.getLogin()).isEqualTo("testUser");
        assertThat(responseDTO.getEmail()).isEqualTo("test@example.com");
        assertThat(responseDTO.getRoles()).containsExactly("BUYER");
    }


    @Test
    void toUser() {
        UserRegistrationDTO registrationDTO = UserRegistrationDTO.builder()
                .login("newUser")
                .email("new@example.com")
                .password("securePassword")
                .build();

        User user = userMapper.toUser(registrationDTO);

        assertThat(user).isNotNull();
        assertThat(user.getLogin()).isEqualTo("newUser");
        assertThat(user.getEmail()).isEqualTo("new@example.com");
        assertThat(user.getPassword()).isEqualTo("securePassword");
    }


    @Test
    void toUserResponseDTOList() {
        User user1 = User.builder()
                .id(3L)
                .login("user1")
                .email("user1@example.com")
                .roles(Set.of(new Role(1L, RoleName.BUYER)))
                .build();

        User user2 = User.builder()
                .id(4L)
                .login("user2")
                .email("user2@example.com")
                .roles(Set.of(new Role(2L, RoleName.ADMIN)))
                .build();

        List<UserResponseDTO> responseDTOList = userMapper.toUserResponseDTOList(List.of(user1, user2));

        assertThat(responseDTOList).hasSize(2);
        assertThat(responseDTOList.get(0).getLogin()).isEqualTo("user1");
        assertThat(responseDTOList.get(1).getLogin()).isEqualTo("user2");
        assertThat(responseDTOList.get(0).getRoles()).containsExactly("BUYER");
        assertThat(responseDTOList.get(1).getRoles()).containsExactly("ADMIN");
    }
}
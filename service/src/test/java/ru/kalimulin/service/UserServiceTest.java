package ru.kalimulin.service;

import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.kalimulin.dto.userDTO.*;
import ru.kalimulin.enums.RoleName;
import ru.kalimulin.mappers.userMapper.UserMapper;
import ru.kalimulin.models.Role;
import ru.kalimulin.models.User;
import ru.kalimulin.repositories.RoleRepository;
import ru.kalimulin.repositories.UserRepository;
import ru.kalimulin.serviceImpl.UserServiceImpl;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private UserRepository userRepository;
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private UserMapper userMapper;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private HttpSession session;

    private User testUser;
    private Role buyerRole;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setLogin("testUser");
        testUser.setEmail("testEmail@test.com");
        testUser.setPassword("hashedPassword");

        buyerRole = new Role();
        buyerRole.setRoleName(RoleName.BUYER);

        lenient().when(session.getAttribute("check")).thenReturn(true);
    }

    @Test
    void shouldRegisterUserSuccessfully() {
        UserRegistrationDTO userDTO = new UserRegistrationDTO("testUser", "testEmail@test.com", "password");
        Set<String> roles = Set.of("BUYER");
        UserResponseDTO responseDTO = new UserResponseDTO(1L, "testUser", "testEmail@test.com", roles);

        when(roleRepository.findByRoleName(RoleName.BUYER)).thenReturn(Optional.of(buyerRole));
        when(userRepository.findByLogin(anyString())).thenReturn(Optional.empty());
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("hashedPassword");
        when(userMapper.toUser(any(UserRegistrationDTO.class))).thenReturn(testUser); // Используем testUser из @BeforeEach
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(userMapper.toUserResponseDTO(any(User.class))).thenReturn(responseDTO);

        UserResponseDTO result = userService.registerUser(userDTO);

        assertNotNull(result);
        assertEquals("testUser", result.getLogin());
        assertEquals("testEmail@test.com", result.getEmail());
        assertEquals(roles, result.getRoles());
    }

    @Test
    void shouldAuthenticateUserSuccessfully() {
        LoginRequestDTO loginRequest = new LoginRequestDTO("testUser", "password");

        when(userRepository.findByLogin("testUser")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("password", "hashedPassword")).thenReturn(true);

        LoginResponseDTO result = userService.authenticateUser(loginRequest);

        assertNotNull(result);
        assertEquals("Успешный вход!", result.getMessage());
    }

    @Test
    void shouldUpdateUserSuccessfully() {
        UserUpdateDTO updateDTO = new UserUpdateDTO(1L, "newUser", "newEmail@test.com", "newPassword");

        Set<String> roles = Set.of("BUYER");

        when(session.getAttribute("userLogin")).thenReturn("testUser");
        when(userRepository.findByLogin("testUser")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.encode("newPassword")).thenReturn("newHashedPassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(userMapper.toUserResponseDTO(any(User.class))).thenReturn(new UserResponseDTO(1L, "newUser", "newEmail@test.com", roles));

        UserResponseDTO result = userService.updateUser(session, updateDTO);

        assertNotNull(result);
        assertEquals("newUser", result.getLogin());
        assertEquals("newEmail@test.com", result.getEmail());
    }

    @Test
    void shouldGetUserByIdSuccessfully() {
        Set<String> roles = Set.of("BUYER");
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userMapper.toUserResponseDTO(any(User.class))).thenReturn(new UserResponseDTO(1L, "testUser", "testEmail@test.com", roles));

        UserResponseDTO result = userService.getUserById(1L);

        assertNotNull(result);
        assertEquals("testUser", result.getLogin());
    }

    @Test
    void shouldGetAllUsersSuccessfully() {
        Set<String> roles = Set.of("BUYER");
        when(userRepository.findAll()).thenReturn(List.of(testUser));
        when(userMapper.toUserResponseDTOList(anyList())).thenReturn(List.of(new UserResponseDTO(1L, "testUser", "testEmail@test.com", roles)));

        List<UserResponseDTO> result = userService.getAllUsers();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("testUser", result.get(0).getLogin());
    }

    @Test
    void shouldGetThisUserProfileSuccessfully() {
        Set<String> roles = Set.of("BUYER");
        when(session.getAttribute("userLogin")).thenReturn("testUser");
        when(userRepository.findByLogin("testUser")).thenReturn(Optional.of(testUser));
        when(userMapper.toUserResponseDTO(any(User.class))).thenReturn(new UserResponseDTO(1L, "testUser", "testEmail@test.com", roles));

        UserResponseDTO result = userService.getThisUserProfile(session);

        assertNotNull(result);
        assertEquals("testUser", result.getLogin());
    }

    @Test
    void shouldDeleteUserByLoginSuccessfully() {
        when(session.getAttribute("userLogin")).thenReturn("testUser");
        when(userRepository.findByLogin("testUser")).thenReturn(Optional.of(testUser));

        userService.deleteUserByLogin(session);

        verify(userRepository, times(1)).delete(testUser);
    }
}

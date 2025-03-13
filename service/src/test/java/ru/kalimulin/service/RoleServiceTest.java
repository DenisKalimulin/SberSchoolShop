package ru.kalimulin.service;

import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.kalimulin.customExceptions.roleExceptions.RoleAlreadyAssignedException;
import ru.kalimulin.customExceptions.roleExceptions.RoleNotFoundException;
import ru.kalimulin.customExceptions.userExceptions.AdminRoleNotFoundException;
import ru.kalimulin.customExceptions.userExceptions.UserAlreadyHasAdminRoleException;
import ru.kalimulin.customExceptions.userExceptions.UserNotFoundException;
import ru.kalimulin.dto.userDTO.UserResponseDTO;
import ru.kalimulin.enums.RoleName;
import ru.kalimulin.mappers.userMapper.UserMapper;
import ru.kalimulin.models.Role;
import ru.kalimulin.models.User;
import ru.kalimulin.repositories.RoleRepository;
import ru.kalimulin.repositories.UserRepository;
import ru.kalimulin.serviceImpl.RoleServiceImpl;
import ru.kalimulin.stubService.PaymentService;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RoleServiceTest {
    @InjectMocks
    private RoleServiceImpl roleService;

    @Mock
    private RoleRepository roleRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private PaymentService paymentService;
    @Mock
    private UserMapper userMapper;
    @Mock
    private HttpSession session;

    private User testUser;
    private Role sellerRole;
    private Role adminRole;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setLogin("testUser");
        testUser.setEmail("testEmail@test.com");
        testUser.setRoles(new HashSet<>());

        sellerRole = new Role();
        sellerRole.setRoleName(RoleName.SELLER);

        adminRole = new Role();
        adminRole.setRoleName(RoleName.ADMIN);

        lenient().when(session.getAttribute("userLogin")).thenReturn("testUser");
        lenient().when(session.getAttribute("check")).thenReturn(true);
    }

    @Test
    void shouldPurchaseSellerRoleSuccessfully() {
        UserResponseDTO responseDTO = new UserResponseDTO(1L, "testUser", "testEmail@test.com", Set.of("SELLER"));

        when(userRepository.findByLogin("testUser")).thenReturn(Optional.of(testUser));
        when(roleRepository.findByRoleName(RoleName.SELLER)).thenReturn(Optional.of(sellerRole));
        when(userMapper.toUserResponseDTO(any(User.class))).thenReturn(responseDTO);

        roleService.purchaseSellerRole(session);

        assertTrue(testUser.getRoles().contains(sellerRole));
        verify(paymentService, times(1)).withdrawFunds("testUser", new BigDecimal("999.99"));
        verify(userRepository, times(1)).save(testUser);
    }

    @Test
    void shouldThrowExceptionWhenUserAlreadyHasSellerRole() {
        testUser.getRoles().add(sellerRole);
        when(userRepository.findByLogin("testUser")).thenReturn(Optional.of(testUser));
        when(roleRepository.findByRoleName(RoleName.SELLER)).thenReturn(Optional.of(sellerRole));

        assertThrows(RoleAlreadyAssignedException.class, () -> roleService.purchaseSellerRole(session));
        verify(paymentService, never()).withdrawFunds(anyString(), any());
    }

    @Test
    void shouldThrowExceptionWhenSellerRoleNotFound() {
        when(userRepository.findByLogin("testUser")).thenReturn(Optional.of(testUser));
        when(roleRepository.findByRoleName(RoleName.SELLER)).thenReturn(Optional.empty());

        assertThrows(RoleNotFoundException.class, () -> roleService.purchaseSellerRole(session));
    }

    @Test
    void shouldThrowExceptionWhenUserNotFound() {
        when(userRepository.findByLogin("testUser")).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> roleService.purchaseSellerRole(session));
    }

    @Test
    void shouldAddAdminRoleSuccessfully() {
        UserResponseDTO responseDTO = new UserResponseDTO(1L, "testUser", "testEmail@test.com", Set.of("ADMIN"));

        when(userRepository.findByEmail("testEmail@test.com")).thenReturn(Optional.of(testUser));
        when(roleRepository.findByRoleName(RoleName.ADMIN)).thenReturn(Optional.of(adminRole));
        when(userMapper.toUserResponseDTO(any(User.class))).thenReturn(responseDTO);

        UserResponseDTO result = roleService.addAdminRole("testEmail@test.com");

        assertNotNull(result);
        assertTrue(testUser.getRoles().contains(adminRole));
        verify(userRepository, times(1)).save(testUser);
    }

    @Test
    void shouldThrowExceptionWhenUserAlreadyHasAdminRole() {
        testUser.getRoles().add(adminRole);
        when(userRepository.findByEmail("testEmail@test.com")).thenReturn(Optional.of(testUser));

        assertThrows(UserAlreadyHasAdminRoleException.class, () -> roleService.addAdminRole("testEmail@test.com"));
    }

    @Test
    void shouldThrowExceptionWhenAdminRoleNotFound() {
        when(userRepository.findByEmail("testEmail@test.com")).thenReturn(Optional.of(testUser));
        when(roleRepository.findByRoleName(RoleName.ADMIN)).thenReturn(Optional.empty());

        assertThrows(AdminRoleNotFoundException.class, () -> roleService.addAdminRole("testEmail@test.com"));
    }

    @Test
    void shouldThrowExceptionWhenUserNotFoundByEmail() {
        when(userRepository.findByEmail("notfound@test.com")).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> roleService.addAdminRole("notfound@test.com"));
    }
}

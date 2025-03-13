package ru.kalimulin.serviceImpl;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
import ru.kalimulin.service.RoleService;
import ru.kalimulin.stubService.PaymentService;
import ru.kalimulin.util.SessionUtils;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PaymentService paymentService;
    private final UserMapper userMapper;

    private static final Logger logger = LoggerFactory.getLogger(RoleServiceImpl.class);
    private static final BigDecimal SELLER_ROLE_PRICE = BigDecimal.valueOf(999.99);

    @Transactional
    @Override
    public UserResponseDTO purchaseSellerRole(HttpSession session) {
        User user = userRepository.findByLogin(SessionUtils.getUserLogin(session))
                .orElseThrow(() -> new UserNotFoundException("Пользователь с логином "
                        + SessionUtils.getUserLogin(session) + " не найден"));
        logger.info("Пользователь пытается купить роль SELLER");


        Role sellerRole = roleRepository.findByRoleName(RoleName.SELLER)
                .orElseThrow(() -> new RoleNotFoundException("Роль SELLER не найдена"));

        if(user.getRoles().contains(sellerRole)) {
            throw new RoleAlreadyAssignedException("Вы уже являетесь продавцом!");
        }

        paymentService.withdrawFunds(user.getLogin(), SELLER_ROLE_PRICE);

        user.getRoles().add(sellerRole);

        userRepository.save(user);

        logger.info("Пользователь успешно приобрел роль SELLER");
        return userMapper.toUserResponseDTO(user);
    }

    @Transactional
    @Override
    public UserResponseDTO addAdminRole(String email) {
        logger.info("Добавление роли ADMIN пользователю");
        User user = findUserByEmail(email);

        boolean hasAdminRole = user.getRoles().stream()
                .anyMatch(role -> role.getRoleName() == RoleName.ADMIN);

        if (hasAdminRole) {
            logger.warn("Пользователь уже является администратором");
            throw new UserAlreadyHasAdminRoleException("У пользователя уже есть роль администратора");
        }

        Role adminRole = roleRepository.findByRoleName(RoleName.ADMIN)
                .orElseThrow(() -> new AdminRoleNotFoundException("Роль администратора не найдена"));

        user.getRoles().add(adminRole);
        userRepository.save(user);

        logger.info("Пользователь теперь является администратором");
        return userMapper.toUserResponseDTO(user);
    }

    private User findUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    logger.warn("Пользователь не найден");
                    return new UserNotFoundException("Пользователь с таким email " + email + " не найден");
                });
    }
}

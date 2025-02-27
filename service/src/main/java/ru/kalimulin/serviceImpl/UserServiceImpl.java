package ru.kalimulin.serviceImpl;

import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kalimulin.customExceptions.roleExceptions.RoleNotFoundException;
import ru.kalimulin.customExceptions.userExceptions.InvalidEmailOrPasswordException;
import ru.kalimulin.customExceptions.userExceptions.UserAlreadyExistsException;
import ru.kalimulin.customExceptions.userExceptions.UserNotFoundException;
import ru.kalimulin.dto.userDTO.*;
import ru.kalimulin.enums.RoleName;
import ru.kalimulin.mappers.userMapper.UserMapper;
import ru.kalimulin.models.Role;
import ru.kalimulin.models.User;
import ru.kalimulin.repositories.RoleRepository;
import ru.kalimulin.repositories.UserRepository;
import ru.kalimulin.service.UserService;
import ru.kalimulin.util.SessionUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
    @Autowired
    public UserServiceImpl(UserRepository userRepository,
                           RoleRepository roleRepository,
                           UserMapper userMapper,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    @Override
    public UserResponseDTO registerUser(UserRegistrationDTO userRegistrationDTO) {
        logger.info("Регистрация нового пользователя: {}", userRegistrationDTO.getLogin());
        checkUserUniqueness(userRegistrationDTO.getLogin(), userRegistrationDTO.getEmail());

        Set<Role> defaultRole = new HashSet<>();
        Role role = roleRepository.findByRoleName(RoleName.BUYER)
                .orElseThrow(() -> new RoleNotFoundException("Роль BUYER не найдена"));
        defaultRole.add(role);

        User user = userMapper.toUser(userRegistrationDTO);
        user.setPassword(passwordEncoder.encode(userRegistrationDTO.getPassword()));
        user.setRoles(defaultRole);
        User savedUser = userRepository.save(user);

        logger.info("Пользователь зарегистрирован: {}", savedUser.getLogin());
        return userMapper.toUserResponseDTO(savedUser);
    }

    @Override
    public LoginResponseDTO authenticateUser(LoginRequestDTO loginRequestDTO) {
        logger.info("Попытка входа пользователя: {}", loginRequestDTO.getLogin());
        User user = findUserByLogin(loginRequestDTO.getLogin());

        if (!passwordEncoder.matches(loginRequestDTO.getPassword(), user.getPassword())) {
            logger.warn("Ошибка аутентификации для пользователя: {}", loginRequestDTO.getLogin());
            throw new InvalidEmailOrPasswordException("Неверный логин или пароль");
        }

        logger.info("Успешный вход пользователя: {}", loginRequestDTO.getLogin());
        return LoginResponseDTO.builder()
                .message("Успешный вход!")
                .build();
    }


    @Transactional
    @Override
    public UserResponseDTO updateUser(HttpSession session, UserUpdateDTO userUpdateDTO) {
        String userLogin = SessionUtils.getUserLogin(session);
        logger.info("Обновление профиля пользователя: {}", userLogin);
        User user = findUserByLogin(userLogin);

        checkUserUniqueness(userUpdateDTO.getLogin(), userUpdateDTO.getEmail());

        if (userUpdateDTO.getLogin() != null) {
            user.setLogin(userUpdateDTO.getLogin());
        }

        if (userUpdateDTO.getEmail() != null) {
            user.setEmail(userUpdateDTO.getEmail());
        }

        if (userUpdateDTO.getPassword() != null) {
            user.setPassword(passwordEncoder.encode(userUpdateDTO.getPassword()));
        }

        userRepository.save(user);
        logger.info("Профиль пользователя {} обновлен", userLogin);
        return userMapper.toUserResponseDTO(user);
    }

    @Transactional
    @Override
    public UserResponseDTO getUserById(Long id) {
        logger.info("Поиск пользователя по ID: {}", id);
        User user = findUserById(id);
        return userMapper.toUserResponseDTO(user);
    }

    @Transactional
    @Override
    public List<UserResponseDTO> getAllUsers() {
        logger.info("Получение списка всех пользователей");
        List<User> users = userRepository.findAll();
        return userMapper.toUserResponseDTOList(users);
    }

    @Transactional
    @Override
    public UserResponseDTO getThisUserProfile(HttpSession session) {
        String userLogin = SessionUtils.getUserLogin(session);
        logger.info("Получение профиля текущего пользователя: {}", userLogin);
        User user = findUserByLogin(userLogin);
        return userMapper.toUserResponseDTO(user);
    }

    @Transactional
    @Override
    public UserResponseDTO getProfileByLogin(String login) {
        logger.info("Поиск профиля пользователя по логину: {}", login);
        User user = userRepository.findByLogin(login)
                .orElseThrow(() -> new UserNotFoundException("Пользователя с таким логином: " + login + " не существует"));

        return userMapper.toUserResponseDTO(user);
    }

    @Transactional
    @Override
    public void deleteUserByLogin(HttpSession session) {
        String userLogin = SessionUtils.getUserLogin(session);
        logger.info("Удаление пользователя: {}", userLogin);

        User user = findUserByLogin(userLogin);
        userRepository.delete(user);

        logger.info("Пользователь {} удален", userLogin);
    }

    /**
     * Проверка уникальности логина и email.
     * Если логин или email уже заняты, выбрасывает исключение.
     *
     * @param login логин пользователя.
     * @param email email пользователя.
     * @throws UserAlreadyExistsException если логин или email уже заняты.
     */
    private void checkUserUniqueness(String login, String email) {
        if (login != null && userRepository.findByLogin(login).isPresent()) {
            logger.warn("Попытка регистрации с уже существующим логином: {}", login);
            throw new UserAlreadyExistsException("Логин уже используется");
        }
        if (email != null && userRepository.findByEmail(email).isPresent()) {
            logger.warn("Попытка регистрации с уже существующим email: {}", email);
            throw new UserAlreadyExistsException("Email уже используется");
        }
    }

    /**
     * Получение пользователя по id.
     *
     * @param id уникальный идентификатор пользователя.
     * @return найденный пользователь.
     * @throws UserNotFoundException если пользователь с таким id не найден.
     */
    private User findUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Пользователь с ID {} не найден", id);
                    return new UserNotFoundException("Пользователь с id " + id + " не найден");
                });
    }

    /**
     * Получение пользователя по email.
     *
     * @param email email пользователя.
     * @return найденный пользователь.
     * @throws UserNotFoundException если пользователь с таким email не найден.
     */
    private User findUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    logger.warn("Пользователь с email {} не найден", email);
                    return new UserNotFoundException("Пользователь с таким email " + email + " не найден");
                });
    }

    /**
     * Получение пользователя по логину.
     *
     * @param login логин пользователя.
     * @return найденный пользователь.
     * @throws UserNotFoundException если пользователь с таким логином не найден.
     */
    private User findUserByLogin(String login) {
        return userRepository.findByLogin(login)
                .orElseThrow(() -> {
                    logger.warn("Пользователь с логином {} не найден", login);
                    return new UserNotFoundException("Пользователь с таким логином " + login + " не найден");
                });
    }
}
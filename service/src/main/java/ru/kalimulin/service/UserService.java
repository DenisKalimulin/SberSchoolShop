package ru.kalimulin.service;

import jakarta.servlet.http.HttpSession;
import ru.kalimulin.dto.userDTO.*;
import ru.kalimulin.customExceptions.userExceptions.*;

import java.util.List;

/**
 * Сервис для работы с пользователями.
 * Содержит методы для регистрации, аутентификации, обновления, получения и удаления пользователей.
 */
public interface UserService {

    /**
     * Регистрация нового пользователя.
     * Проверяет уникальность логина и email. Если они уже заняты, выбрасывает исключение.
     * Создаёт нового пользователя и сохраняет его в базе данных.
     *
     * @param userRegistrationDTO DTO с данными для регистрации пользователя.
     * @return UserResponseDTO с информацией о зарегистрированном пользователе.
     * @throws UserAlreadyExistsException если логин или email уже заняты.
     */
    UserResponseDTO registerUser(UserRegistrationDTO userRegistrationDTO);

    /**
     * Аутентификация пользователя.
     * Проверяет email и пароль. Если они некорректны, выбрасывает исключение.
     *
     * @param loginRequestDTO DTO с email и паролем для аутентификации.
     * @return LoginResponseDTO с сообщением об успешной аутентификации.
     * @throws InvalidEmailOrPasswordException если email или пароль некорректны.
     */
    LoginResponseDTO authenticateUser(LoginRequestDTO loginRequestDTO);


    /**
     * Обновление данных пользователя.
     * Проверяет уникальность логина и email перед обновлением.
     * Если логин или email уже заняты, выбрасывает исключение.
     *
     * @param session       сессия текущего пользователя.
     * @param userUpdateDTO DTO с данными для обновления пользователя.
     * @return UserResponseDTO с обновлёнными данными пользователя.
     * @throws UserNotFoundException      если пользователь с таким логином не найден.
     * @throws UserAlreadyExistsException если новый логин или email уже заняты.
     */
    UserResponseDTO updateUser(HttpSession session, UserUpdateDTO userUpdateDTO);

    /**
     * Получение пользователя по его идентификатору.
     *
     * @param id уникальный идентификатор пользователя.
     * @return UserResponseDTO с данными пользователя.
     * @throws UserNotFoundException если пользователь с таким id не найден.
     */
    UserResponseDTO getUserById(Long id);

    /**
     * Получение всех пользователей.
     *
     * @return список всех пользователей в формате UserResponseDTO.
     */
    List<UserResponseDTO> getAllUsers();

    /**
     * Получение пользователя по email из текущей сессии.
     *
     * @param session сессия текущего пользователя.
     * @return UserResponseDTO с данными пользователя.
     * @throws UserNotFoundException если пользователь с таким логином не найден.
     */
    UserResponseDTO getThisUserProfile(HttpSession session);

    /**
     * Получение профиля пользователя по его логину.
     *
     * @param login строка с логином пользователя.
     * @return UserResponseDTO с данными пользователя.
     */
    UserResponseDTO getProfileByLogin(String login);


    /**
     * Удаление пользователя по email из текущей сессии.
     *
     * @param session сессия текущего пользователя.
     * @throws UserNotFoundException если пользователь с таким email не найден.
     */
    void deleteUserByLogin(HttpSession session);
}

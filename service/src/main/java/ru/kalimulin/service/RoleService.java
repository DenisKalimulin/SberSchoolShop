package ru.kalimulin.service;

import jakarta.servlet.http.HttpSession;
import ru.kalimulin.customExceptions.userExceptions.AdminRoleNotFoundException;
import ru.kalimulin.customExceptions.userExceptions.UserAlreadyHasAdminRoleException;
import ru.kalimulin.customExceptions.userExceptions.UserNotFoundException;
import ru.kalimulin.dto.userDTO.UserResponseDTO;

/**
 * Сервис для управления ролями пользователя
 */
public interface RoleService {

    /**
     * Покупка роли SELLER
     *
     * @param session - аутентифицированный пользователь из сессии
     * @return обновленный пользователь с ролью SELLER
     */
    UserResponseDTO purchaseSellerRole(HttpSession session);

    /**
     * Добавление роли администратора пользователю.
     * Проверяет наличие роли ADMIN у пользователя. Если такая роль уже есть, выбрасывает исключение.
     *
     * @param email email пользователя, которому необходимо добавить роль администратора.
     * @return UserResponseDTO с обновлёнными данными пользователя.
     * @throws UserNotFoundException            если пользователь с указанным email не найден.
     * @throws UserAlreadyHasAdminRoleException если у пользователя уже есть роль администратора.
     * @throws AdminRoleNotFoundException       если роль администратора не найдена.
     */
    UserResponseDTO addAdminRole(String email);
}

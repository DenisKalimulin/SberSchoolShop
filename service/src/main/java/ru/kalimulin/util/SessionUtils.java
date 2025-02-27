package ru.kalimulin.util;

import jakarta.servlet.http.HttpSession;
import ru.kalimulin.customExceptions.userExceptions.UnauthorizedException;

import java.util.Optional;

/**
 * Утилитный класс для работы с сессией пользователя.
 * Позволяет извлекать логин текущего пользователя.
 */
public final class SessionUtils {

    private static final String USER_LOGIN_ATTRIBUTE = "userLogin";


    private SessionUtils() {
        throw new UnsupportedOperationException("Этот класс нельзя инстанцировать.");
    }

    /**
     * Получает логин пользователя из сессии.
     *
     * @param session текущая сессия пользователя.
     * @return логин пользователя.
     * @throws UnauthorizedException если пользователь не авторизован.
     */
    public static String getUserLogin(HttpSession session) {
        if (session == null || !isSessionValid(session)) {
            return null;
        }

        return Optional.ofNullable((String) session.getAttribute(USER_LOGIN_ATTRIBUTE))
                .orElseThrow(() -> new UnauthorizedException("Вы не авторизованы. Войдите в систему!"));
    }

    private static boolean isSessionValid(HttpSession session) {
        try {
            session.getAttribute("check"); // Попытка обращения к атрибуту
            return true;
        } catch (IllegalStateException e) {
            return false;
        }
    }
}

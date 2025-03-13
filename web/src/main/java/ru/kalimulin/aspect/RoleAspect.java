package ru.kalimulin.aspect;

import jakarta.servlet.http.HttpSession;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.kalimulin.annotation.RoleRequired;
import ru.kalimulin.models.User;
import ru.kalimulin.repositories.UserRepository;

import java.util.Set;
import java.util.stream.Collectors;

@Aspect
@Component
public class RoleAspect {
    private final HttpSession session;

    private final UserRepository userRepository;

    @Autowired
    public RoleAspect(HttpSession session, UserRepository userRepository) {
        this.session = session;
        this.userRepository = userRepository;
    }

    @Before("@annotation(roleRequired)")
    public void checkRole(RoleRequired roleRequired) {
        String userLogin = (String) session.getAttribute("userLogin");
        if (userLogin == null) {
            throw new AccessDeniedException("Неавторизованный доступ");
        }

        User user = userRepository.findByLogin(userLogin)
                .orElseThrow(() -> new AccessDeniedException("Пользователь не найден"));

        Set<String> userRoles = user.getRoles().stream()
                .map(role -> role.getRoleName().name())
                .collect(Collectors.toSet());


        boolean hasRole = false;
        for (String requiredRole : roleRequired.value()) {
            if (userRoles.contains(requiredRole)) {
                hasRole = true;
                break;
            }
        }

        if (!hasRole) {
            throw new AccessDeniedException("Доступ запрещён");
        }
    }

    public static class AccessDeniedException extends RuntimeException {
        public AccessDeniedException(String message) {
            super(message);
        }
    }
}
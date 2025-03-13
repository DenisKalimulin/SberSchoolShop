package ru.kalimulin.annotation;

import java.lang.annotation.*;

@Target({ ElementType.METHOD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RoleRequired {
    String[] value(); // Список необходимых ролей
}
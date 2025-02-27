package ru.kalimulin.dto.userDTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserRegistrationDTO {
    @NotBlank(message = "Поле с логином не может быть пустым")
    @Size(min = 3, max = 128, message = "Login должно содержать от 2 до 128 символов")
    private String login;

    @Email(message = "Email должен быть валидным")
    @NotBlank(message = "Email не должен быть пустым")
    private String email;

    @NotBlank(message = "Пароль не может быть пустым")
    @Size(min = 8, max = 64, message = "Длина пароля должна быть от 8 до 64 символов")
    private String password;
}
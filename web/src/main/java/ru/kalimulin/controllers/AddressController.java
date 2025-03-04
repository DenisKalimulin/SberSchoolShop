package ru.kalimulin.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.kalimulin.dto.addressDTO.AddressCreateDTO;
import ru.kalimulin.dto.addressDTO.AddressResponseDTO;
import ru.kalimulin.dto.addressDTO.AddressUpdateDTO;
import ru.kalimulin.service.AddressService;

import java.util.List;

@RestController
@RequestMapping("/shop/address")
@RequiredArgsConstructor
@Tag(name = "Адреса", description = "Управление адресами пользователей")
public class AddressController {
    private final AddressService addressService;
    private static final Logger logger = LoggerFactory.getLogger(AddressController.class);


    @Operation(summary = "Создать адрес для пользователя", description = "Создает адрес для текущего пользователя")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Адрес успешно создан"),
            @ApiResponse(responseCode = "400", description = "Некорректные данные"),
            @ApiResponse(responseCode = "401", description = "Пользователь не авторизован"),
            @ApiResponse(responseCode = "500", description = "Ошибка сервера")
    })
    @PostMapping("/create")
    public ResponseEntity<AddressResponseDTO> createAddress(
            @Parameter(description = "Данные для создания адреса", required = true)
            @RequestBody AddressCreateDTO addressCreateDTO, HttpSession session) {
        logger.info("Запрос на создание нового адреса: {}", addressCreateDTO);
        AddressResponseDTO addressResponseDTO = addressService.createAddress(addressCreateDTO, session);
        logger.info("Адрес успешно создан: {}", addressResponseDTO);
        return ResponseEntity.ok(addressResponseDTO);
    }

    @Operation(
            summary = "Получить список всех адресов пользователя",
            description = "Возвращает список всех адресов текущего пользователя"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Список адресов получен"),
            @ApiResponse(responseCode = "401", description = "Пользователь не авторизован"),
            @ApiResponse(responseCode = "500", description = "Ошибка сервера")
    })
    @GetMapping("/list")
    public ResponseEntity<List<AddressResponseDTO>> getAllAddresses(HttpSession session) {
        logger.info("Запрос на получение всех адресов пользователя.");
        List<AddressResponseDTO> addresses = addressService.getAddresses(session);
        logger.info("Список адресов успешно получен. Количество адресов: {}", addresses.size());
        return ResponseEntity.ok(addresses);
    }

    @Operation(
            summary = "Обновить адрес по ID",
            description = "Обновляет адрес пользователя по его идентификатору"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Адрес успешно обновлен"),
            @ApiResponse(responseCode = "404", description = "Адрес не найден"),
            @ApiResponse(responseCode = "401", description = "Пользователь не авторизован"),
            @ApiResponse(responseCode = "400", description = "Некорректные данные"),
            @ApiResponse(responseCode = "500", description = "Ошибка сервера")
    })
    @PutMapping("/update/{addressId}")
    public ResponseEntity<AddressResponseDTO> updateAddress(@PathVariable Long addressId,
                                                            @RequestBody AddressUpdateDTO addressUpdateDTO,
                                                            HttpSession session) {
        logger.info("Запрос на обновление адреса: {}", addressUpdateDTO);
        AddressResponseDTO addressResponseDTO = addressService.updateAddress(addressId, addressUpdateDTO, session);
        logger.info("Адрес успешно обновлен: {}", addressResponseDTO);
        return ResponseEntity.ok(addressResponseDTO);
    }

    @Operation(
            summary = "Удалить адрес по ID",
            description = "Удаляет адрес пользователя по его идентификатору"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Адрес успешно удален"),
            @ApiResponse(responseCode = "404", description = "Адрес не найден"),
            @ApiResponse(responseCode = "401", description = "Пользователь не авторизован"),
            @ApiResponse(responseCode = "500", description = "Ошибка сервера")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteAddress(@PathVariable Long id, HttpSession session) {
        logger.warn("Запрос на удаление адреса с ID: {}", id);
        addressService.deleteAddress(id, session);
        logger.info("Адрес с ID {} успешно удален", id);
        return ResponseEntity.ok("Адрес успешно удален");
    }
}
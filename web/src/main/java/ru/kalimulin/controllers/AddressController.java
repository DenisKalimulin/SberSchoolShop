package ru.kalimulin.controllers;

import jakarta.servlet.http.HttpSession;
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
public class AddressController {
    private final AddressService addressService;
    private static final Logger logger = LoggerFactory.getLogger(AddressController.class);

    @Autowired
    public AddressController(AddressService addressService) {
        this.addressService = addressService;
    }

    @PostMapping("/create")
    public ResponseEntity<AddressResponseDTO> createAddress(@RequestBody AddressCreateDTO addressCreateDTO,
                                                            HttpSession session) {
        logger.info("Запрос на создание нового адреса: {}", addressCreateDTO);
        AddressResponseDTO addressResponseDTO = addressService.createAddress(addressCreateDTO, session);
        logger.info("Адрес успешно создан: {}", addressResponseDTO);
        return ResponseEntity.ok(addressResponseDTO);
    }

    @GetMapping("/list")
    public ResponseEntity<List<AddressResponseDTO>> getAllAddresses(HttpSession session) {
        logger.info("Запрос на получение всех адресов пользователя.");
        List<AddressResponseDTO> addresses = addressService.getAddresses(session);
        logger.info("Список адресов успешно получен. Количество адресов: {}", addresses.size());
        return ResponseEntity.ok(addresses);
    }

    @PutMapping("/update/{addressId}")
    public ResponseEntity<AddressResponseDTO> updateAddress(@PathVariable Long addressId,
                                                            @RequestBody AddressUpdateDTO addressUpdateDTO,
                                                            HttpSession session) {
        logger.info("Запрос на обновление адреса: {}", addressUpdateDTO);
        AddressResponseDTO addressResponseDTO = addressService.updateAddress(addressId, addressUpdateDTO, session);
        logger.info("Адрес успешно обновлен: {}", addressResponseDTO);
        return ResponseEntity.ok(addressResponseDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteAddress(@PathVariable Long id, HttpSession session) {
        logger.warn("Запрос на удаление адреса с ID: {}", id);
        addressService.deleteAddress(id, session);
        logger.info("Адрес с ID {} успешно удален", id);
        return ResponseEntity.ok("Адрес успешно удален");
    }
}
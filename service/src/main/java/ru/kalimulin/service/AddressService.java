package ru.kalimulin.service;

import jakarta.servlet.http.HttpSession;
import ru.kalimulin.dto.addressDTO.AddressCreateDTO;
import ru.kalimulin.dto.addressDTO.AddressResponseDTO;
import ru.kalimulin.dto.addressDTO.AddressUpdateDTO;

import java.util.List;

/**
 * Сервис для работы с адресами.
 * Содержит методы для создания адреса, получения списка адресов пользователя,
 * обновления адреса и удаления адреса.
 */
public interface AddressService {

    AddressResponseDTO createAddress(AddressCreateDTO addressCreateDTO, HttpSession session);

    List<AddressResponseDTO> getAddresses(HttpSession session);

    AddressResponseDTO updateAddress(Long addressId, AddressUpdateDTO addressUpdateDTO, HttpSession session);

    void deleteAddress(Long id, HttpSession session);
}

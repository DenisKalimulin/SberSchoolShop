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

    /**
     * Создает новый адрес для пользователя.
     *
     * @param addressCreateDTO данные для создания адреса
     * @param session текущая сессия пользователя
     * @return созданный адрес в виде DTO
     */
    AddressResponseDTO createAddress(AddressCreateDTO addressCreateDTO, HttpSession session);

    /**
     * Получает список всех адресов пользователя.
     *
     * @param session текущая сессия пользователя
     * @return список адресов пользователя в виде DTO
     */
    List<AddressResponseDTO> getAddresses(HttpSession session);

    /**
     * Обновляет существующий адрес пользователя.
     *
     * @param addressId идентификатор адреса
     * @param addressUpdateDTO данные для обновления адреса
     * @param session текущая сессия пользователя
     * @return обновленный адрес в виде DTO
     */
    AddressResponseDTO updateAddress(Long addressId, AddressUpdateDTO addressUpdateDTO, HttpSession session);

    /**
     * Удаляет адрес пользователя.
     *
     * @param id идентификатор адреса
     * @param session текущая сессия пользователя
     */
    void deleteAddress(Long id, HttpSession session);
}
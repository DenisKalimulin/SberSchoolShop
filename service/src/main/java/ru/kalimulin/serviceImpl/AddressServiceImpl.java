package ru.kalimulin.serviceImpl;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kalimulin.customExceptions.addressExceptions.AddressNotFoundException;
import ru.kalimulin.customExceptions.userExceptions.UnauthorizedAccessException;
import ru.kalimulin.customExceptions.userExceptions.UserNotFoundException;
import ru.kalimulin.dto.addressDTO.AddressCreateDTO;
import ru.kalimulin.dto.addressDTO.AddressResponseDTO;
import ru.kalimulin.dto.addressDTO.AddressUpdateDTO;
import ru.kalimulin.mappers.addressMapper.AddressMapper;
import ru.kalimulin.models.Address;
import ru.kalimulin.models.User;
import ru.kalimulin.repositories.AddressRepository;
import ru.kalimulin.repositories.UserRepository;
import ru.kalimulin.service.AddressService;
import ru.kalimulin.util.SessionUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AddressServiceImpl implements AddressService {
    private final AddressRepository addressRepository;
    private final UserRepository userRepository;
    private final AddressMapper addressMapper;

    private static final Logger logger = LoggerFactory.getLogger(AddressServiceImpl.class);

    @Transactional
    @Override
    public AddressResponseDTO createAddress(AddressCreateDTO addressCreateDTO, HttpSession session) {
        User user = getUser(session);
        logger.info("Запрос на создание адреса от пользователя");
        Address address = addressMapper.toAddress(addressCreateDTO);
        address.setUser(user);

        user.getAddresses().add(address);

        userRepository.save(user);

        logger.info("Адрес успешно создан");
        return addressMapper.toAddressResponseDTO(address);
    }

    @Transactional
    @Override
    public List<AddressResponseDTO> getAddresses(HttpSession session) {
        User user = getUser(session);
        logger.info("Запрос на получение списка адресов");

        List<Address> addresses = addressRepository.findAddressByUser(user);

        return addressMapper.toAddressResponseDTOList(addresses);
    }

    @Transactional
    @Override
    public AddressResponseDTO updateAddress(Long addressId, AddressUpdateDTO addressUpdateDTO, HttpSession session) {
        User user = getUser(session);
        logger.info("Запрос на обновление адреса");

        Address address = getAddressById(addressId);

        if (!address.getUser().equals(user)) {
            logger.warn("Попытка изменения чужого адреса");
            throw new UnauthorizedAccessException("Этот адрес не принадлежит текущему пользователю.");
        }

        if (addressUpdateDTO.getCountry() != null) {
            address.setCountry(addressUpdateDTO.getCountry());
        }
        if (addressUpdateDTO.getRegion() != null) {
            address.setRegion(addressUpdateDTO.getRegion());
        }
        if (addressUpdateDTO.getCity() != null) {
            address.setCity(addressUpdateDTO.getCity());
        }
        if (addressUpdateDTO.getStreet() != null) {
            address.setStreet(addressUpdateDTO.getStreet());
        }
        if (addressUpdateDTO.getZipCode() != null) {
            address.setZipCode(addressUpdateDTO.getZipCode());
        }

        addressRepository.save(address);
        logger.info("Адрес обновлен пользователем");
        return addressMapper.toAddressResponseDTO(address);
    }


    @Transactional
    @Override
    public void deleteAddress(Long id, HttpSession session) {
        User user = getUser(session);
        logger.info("Запрос на удаление адреса");

        Address address = getAddressById(id);

        user.getAddresses().remove(address);
        addressRepository.delete(address);
        logger.info("Адрес успешно удален");
    }

    /**
     * Получает пользователя по текущей сессии.
     *
     * @param session текущая сессия пользователя
     * @return объект пользователя
     * @throws UserNotFoundException если пользователь не найден
     */
    private User getUser(HttpSession session) {
        String userLogin = SessionUtils.getUserLogin(session);
        return userRepository.findByLogin(userLogin)
                .orElseThrow(() -> new UserNotFoundException("Пользователь с login " + userLogin + " не найден"));
    }

    /**
     * Получает адрес по его идентификатору.
     *
     * @param id идентификатор адреса
     * @return объект адреса
     * @throws AddressNotFoundException если адрес не найден
     */
    private Address getAddressById(Long id) {
        return addressRepository.findById(id)
                .orElseThrow(() -> new AddressNotFoundException("Адрес с id " + id + " не найден"));
    }
}
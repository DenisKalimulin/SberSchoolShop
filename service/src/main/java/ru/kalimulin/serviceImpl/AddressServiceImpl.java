package ru.kalimulin.serviceImpl;

import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
public class AddressServiceImpl implements AddressService {
    private final AddressRepository addressRepository;
    private final UserRepository userRepository;
    private final AddressMapper addressMapper;

    private static final Logger logger = LoggerFactory.getLogger(AddressServiceImpl.class);

    @Autowired
    public AddressServiceImpl(AddressRepository addressRepository,
                              UserRepository userRepository,
                              AddressMapper addressMapper) {
        this.addressRepository = addressRepository;
        this.userRepository = userRepository;
        this.addressMapper = addressMapper;
    }

    @Transactional
    @Override
    public AddressResponseDTO createAddress(AddressCreateDTO addressCreateDTO, HttpSession session) {
        User user = getUser(session);
        logger.info("Запрос на создание адреса от пользователя: {}", user.getLogin());
        Address address = addressMapper.toAddress(addressCreateDTO);
        address.setUser(user);

        user.getAddresses().add(address);

        userRepository.save(user);

        logger.info("Адрес успешно создан для пользователя {}: {}", user.getLogin(), address);
        return addressMapper.toAddressResponseDTO(address);
    }

    @Transactional
    @Override
    public List<AddressResponseDTO> getAddresses(HttpSession session) {
        User user = getUser(session);
        logger.info("Запрос на получение списка адресов пользователя: {}", user.getLogin());

        List<Address> addresses = addressRepository.findAddressByUser(user);

        logger.info("Адрес успешно создан для пользователя {}:", user.getLogin());
        return addressMapper.toAddressResponseDTOList(addresses);
    }

    @Transactional
    @Override
    public AddressResponseDTO updateAddress(Long addressId, AddressUpdateDTO addressUpdateDTO, HttpSession session) {
        User user = getUser(session);
        logger.info("Запрос на обновление адреса с id {} от пользователя {}",addressId, user.getLogin());

        Address address = getAddressById(addressId);

        if (!address.getUser().equals(user)) {
            logger.warn("Попытка изменения чужого адреса пользователем {}", user.getLogin());
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
        logger.info("Адрес с id {} успешно обновлен пользователем {}", address.getId(), user.getLogin());
        return addressMapper.toAddressResponseDTO(address);
    }


    @Transactional
    @Override
    public void deleteAddress(Long id, HttpSession session) {
        User user = getUser(session);
        logger.info("Запрос на удаление адреса с id {} от пользователя {}", id, user.getLogin());

        Address address = getAddressById(id);

        user.getAddresses().remove(address);
        addressRepository.delete(address);
        logger.info("Адрес с id {} успешно удален пользователем {}", id, user.getLogin());
    }

    /**
     * @param session
     * @return
     */
    private User getUser(HttpSession session) {
        String userLogin = SessionUtils.getUserLogin(session);

        return userRepository.findByLogin(userLogin)
                .orElseThrow(() -> new UserNotFoundException("Пользователь с login " + userLogin + " не найден"));
    }

    /**
     * @param id
     * @return
     */
    private Address getAddressById(Long id) {
        return addressRepository.findById(id)
                .orElseThrow(() -> new AddressNotFoundException("Адрес с id " + id + " не найден"));
    }
}
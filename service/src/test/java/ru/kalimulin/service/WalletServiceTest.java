package ru.kalimulin.service;

import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.kalimulin.customExceptions.walletExceptions.InsufficientFundsException;
import ru.kalimulin.customExceptions.walletExceptions.InvalidPinException;
import ru.kalimulin.dto.walletDTO.*;
import ru.kalimulin.kafka.KafkaEmailEventPublisher;
import ru.kalimulin.kafka.WalletEventProducer;
import ru.kalimulin.mappers.walletMapper.WalletMapper;
import ru.kalimulin.models.User;
import ru.kalimulin.models.Wallet;
import ru.kalimulin.repositories.UserRepository;
import ru.kalimulin.repositories.WalletRepository;
import ru.kalimulin.serviceImpl.WalletServiceImpl;
import ru.kalimulin.stubService.PaymentService;
import org.mindrot.jbcrypt.BCrypt;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WalletServiceTest {
    @InjectMocks
    private WalletServiceImpl walletService;

    @Mock
    private WalletRepository walletRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private WalletMapper walletMapper;
    @Mock
    private PaymentService paymentService;
    @Mock
    private WalletEventProducer walletEventProducer;
    @Mock
    private KafkaEmailEventPublisher kafkaEmailEventPublisher;
    @Mock
    private HttpSession session;

    private User testUser;
    private Wallet testWallet;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setLogin("testUser");
        testUser.setEmail("testEmail");

        String hashedPin = BCrypt.hashpw("1234", BCrypt.gensalt());

        testWallet = Wallet.builder()
                .walletNumber("123456789012")
                .user(testUser)
                .balance(BigDecimal.ZERO)
                .pin(hashedPin)
                .build();
    }


    @Test
    void shouldCreateWalletSuccessfully() {
        WalletCreateDTO walletCreateDTO = new WalletCreateDTO("1234");
        WalletResponseDTO walletResponseDTO = new WalletResponseDTO("123456789012", BigDecimal.ZERO);

        when(session.getAttribute("userLogin")).thenReturn("testUser");
        when(session.getAttribute("check")).thenReturn(true);
        when(userRepository.findByLogin("testUser")).thenReturn(Optional.of(testUser));
        when(walletRepository.save(any(Wallet.class))).thenReturn(testWallet);
        when(walletMapper.toWalletResponseDTO(any(Wallet.class))).thenReturn(walletResponseDTO);

        WalletResponseDTO result = walletService.createWallet(walletCreateDTO, session);

        assertNotNull(result);
        assertEquals("123456789012", result.getWalletNumber());
        assertEquals(BigDecimal.ZERO, result.getBalance());

        verify(walletRepository, times(1)).save(any(Wallet.class));
    }


    @Test
    void shouldReturnUserWallet() {
        WalletResponseDTO responseDTO = new WalletResponseDTO("123456789012", BigDecimal.ZERO);

        when(session.getAttribute("userLogin")).thenReturn("testUser");
        when(session.getAttribute("check")).thenReturn(true);
        when(userRepository.findByLogin("testUser")).thenReturn(Optional.of(testUser));
        when(walletRepository.findByUser(testUser)).thenReturn(Optional.of(testWallet));
        when(walletMapper.toWalletResponseDTO(testWallet)).thenReturn(responseDTO);

        WalletResponseDTO result = walletService.getUserWallet(session);

        assertNotNull(result);
        assertEquals("123456789012", result.getWalletNumber());

        verify(walletRepository, times(1)).findByUser(testUser);
    }

    @Test
    void shouldThrowExceptionWhenInsufficientFunds() {
        when(session.getAttribute("userLogin")).thenReturn("testUser");
        when(session.getAttribute("check")).thenReturn(true);
        when(userRepository.findByLogin("testUser")).thenReturn(Optional.of(testUser));
        when(walletRepository.findByUser(testUser)).thenReturn(Optional.of(testWallet));

        Wallet recipientWallet = new Wallet();
        recipientWallet.setWalletNumber("987654321098");
        recipientWallet.setUser(new User());
        recipientWallet.setBalance(BigDecimal.ZERO);

        when(walletRepository.findByWalletNumber("987654321098")).thenReturn(Optional.of(recipientWallet));

        assertThrows(InsufficientFundsException.class, () ->
                walletService.transfer("987654321098", BigDecimal.valueOf(100), "1234", session)
        );
    }

    @Test
    void shouldTransferFundsSuccessfully() {
        testWallet.setBalance(BigDecimal.valueOf(200));

        Wallet recipientWallet = new Wallet();
        recipientWallet.setWalletNumber("987654321098");
        recipientWallet.setUser(new User());
        recipientWallet.setBalance(BigDecimal.ZERO);

        when(session.getAttribute("userLogin")).thenReturn("testUser");
        when(session.getAttribute("check")).thenReturn(true);
        when(userRepository.findByLogin("testUser")).thenReturn(Optional.of(testUser));
        when(walletRepository.findByUser(testUser)).thenReturn(Optional.of(testWallet));
        when(walletRepository.findByWalletNumber("987654321098")).thenReturn(Optional.of(recipientWallet));
        when(walletRepository.save(any(Wallet.class))).thenReturn(testWallet, recipientWallet);

        walletService.transfer("987654321098", BigDecimal.valueOf(100), "1234", session);

        assertEquals(BigDecimal.valueOf(100), testWallet.getBalance());
        assertEquals(BigDecimal.valueOf(100), recipientWallet.getBalance());

        verify(walletRepository, times(2)).save(any(Wallet.class));
        verify(kafkaEmailEventPublisher, times(2)).sendWalletNotification(any());
        verify(walletEventProducer, times(1)).sendWalletTransaction(any());
    }

    @Test
    void shouldDepositSuccessfully() {
        WalletUpdateBalanceDTO walletUpdateBalanceDTO = new WalletUpdateBalanceDTO(BigDecimal.valueOf(500));

        when(session.getAttribute("userLogin")).thenReturn("testUser");
        when(session.getAttribute("check")).thenReturn(true);
        when(userRepository.findByLogin("testUser")).thenReturn(Optional.of(testUser));
        when(walletRepository.findByUser(testUser)).thenReturn(Optional.of(testWallet));
        when(paymentService.processPayment("testUser", BigDecimal.valueOf(500))).thenReturn(true);

        walletService.deposit(session, walletUpdateBalanceDTO);

        assertEquals(BigDecimal.valueOf(500), testWallet.getBalance());

        verify(walletRepository, times(1)).save(any(Wallet.class));
        verify(kafkaEmailEventPublisher, times(1)).sendWalletNotification(any());
        verify(walletEventProducer, times(1)).sendWalletTransaction(any());
    }

    @Test
    void shouldThrowExceptionWhenOldPinIsIncorrect() {
        WalletUpdatePinDTO walletUpdatePinDTO = new WalletUpdatePinDTO("wrongPin", "5678");

        when(session.getAttribute("userLogin")).thenReturn("testUser");
        when(session.getAttribute("check")).thenReturn(true);
        when(userRepository.findByLogin("testUser")).thenReturn(Optional.of(testUser));
        when(walletRepository.findByUser(testUser)).thenReturn(Optional.of(testWallet));

        assertThrows(InvalidPinException.class, () -> walletService.changePin(session, walletUpdatePinDTO));
    }

    @Test
    void shouldChangePinSuccessfully() {
        WalletUpdatePinDTO walletUpdatePinDTO = new WalletUpdatePinDTO("4444", "1234");

        when(session.getAttribute("userLogin")).thenReturn("testUser");
        when(session.getAttribute("check")).thenReturn(true);

        when(userRepository.findByLogin("testUser")).thenReturn(Optional.of(testUser));
        when(walletRepository.findByUser(testUser)).thenReturn(Optional.of(testWallet));

        walletService.changePin(session, walletUpdatePinDTO);

        assertTrue(BCrypt.checkpw("4444", testWallet.getPin()));

        verify(walletRepository, times(1)).save(any(Wallet.class));
        verify(kafkaEmailEventPublisher, times(1)).sendWalletNotification(any());
    }
}

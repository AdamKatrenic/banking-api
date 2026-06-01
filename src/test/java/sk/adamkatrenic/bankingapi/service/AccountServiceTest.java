package sk.adamkatrenic.bankingapi.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sk.adamkatrenic.bankingapi.entity.Account;
import sk.adamkatrenic.bankingapi.entity.User;
import sk.adamkatrenic.bankingapi.repository.AccountRepository;
import sk.adamkatrenic.bankingapi.repository.UserRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AccountService accountService;

    private User testUser;
    private Account testAccount;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("test@test.com");
        testUser.setFullName("Test User");

        testAccount = new Account();
        testAccount.setId(1L);
        testAccount.setUser(testUser);
        testAccount.setAccountNumber("SK123456789");
        testAccount.setBalance(BigDecimal.valueOf(1000));
    }

    @Test
    void shouldCreateAccountSuccessfully() {
        when(userRepository.findByEmail("test@test.com"))
                .thenReturn(Optional.of(testUser));
        when(accountRepository.save(any(Account.class)))
                .thenReturn(testAccount);

        var result = accountService.createAccount("test@test.com");

        assertNotNull(result);
        assertEquals("SK123456789", result.getAccountNumber());
        assertEquals(BigDecimal.valueOf(1000), result.getBalance());
        verify(accountRepository, times(1)).save(any(Account.class));
    }

    @Test
    void shouldThrowWhenUserNotFound() {
        when(userRepository.findByEmail("notfound@test.com"))
                .thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> accountService.createAccount("notfound@test.com"));

        assertEquals("User not found", ex.getMessage());
    }

    @Test
    void shouldReturnAccountsForUser() {
        when(userRepository.findByEmail("test@test.com"))
                .thenReturn(Optional.of(testUser));
        when(accountRepository.findByUserId(1L))
                .thenReturn(List.of(testAccount));

        var result = accountService.getMyAccounts("test@test.com");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("SK123456789", result.get(0).getAccountNumber());
    }
}
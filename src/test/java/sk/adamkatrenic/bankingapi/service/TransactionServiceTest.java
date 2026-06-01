package sk.adamkatrenic.bankingapi.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sk.adamkatrenic.bankingapi.dto.TransactionRequest;
import sk.adamkatrenic.bankingapi.entity.Account;
import sk.adamkatrenic.bankingapi.entity.Transaction;
import sk.adamkatrenic.bankingapi.entity.TransactionType;
import sk.adamkatrenic.bankingapi.repository.AccountRepository;
import sk.adamkatrenic.bankingapi.repository.TransactionRepository;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private TransactionService transactionService;

    private Account account;
    private TransactionRequest request;

    @BeforeEach
    void setUp() {
        account = new Account();
        account.setId(1L);
        account.setAccountNumber("SK123456789");
        account.setBalance(BigDecimal.valueOf(1000));

        request = new TransactionRequest();
        request.setAccountNumber("SK123456789");
        request.setAmount(BigDecimal.valueOf(200));
    }

    @Test
    void shouldDepositSuccessfully() {
        when(accountRepository.findByAccountNumber("SK123456789"))
                .thenReturn(Optional.of(account));
        when(transactionRepository.save(any(Transaction.class)))
                .thenAnswer(i -> i.getArgument(0));

        var result = transactionService.deposit(request);

        assertNotNull(result);
        assertEquals("DEPOSIT", result.getType());
        assertEquals(BigDecimal.valueOf(200), result.getAmount());
        assertEquals(BigDecimal.valueOf(1200), account.getBalance());
    }

    @Test
    void shouldWithdrawSuccessfully() {
        when(accountRepository.findByAccountNumber("SK123456789"))
                .thenReturn(Optional.of(account));
        when(transactionRepository.save(any(Transaction.class)))
                .thenAnswer(i -> i.getArgument(0));

        var result = transactionService.withdraw(request);

        assertNotNull(result);
        assertEquals("WITHDRAWAL", result.getType());
        assertEquals(BigDecimal.valueOf(800), account.getBalance());
    }

    @Test
    void shouldThrowWhenInsufficientFunds() {
        request.setAmount(BigDecimal.valueOf(9999));
        when(accountRepository.findByAccountNumber("SK123456789"))
                .thenReturn(Optional.of(account));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> transactionService.withdraw(request));

        assertEquals("Insufficient funds", ex.getMessage());
    }

    @Test
    void shouldTransferSuccessfully() {
        Account toAccount = new Account();
        toAccount.setAccountNumber("SK987654321");
        toAccount.setBalance(BigDecimal.valueOf(500));

        request.setToAccountNumber("SK987654321");

        when(accountRepository.findByAccountNumber("SK123456789"))
                .thenReturn(Optional.of(account));
        when(accountRepository.findByAccountNumber("SK987654321"))
                .thenReturn(Optional.of(toAccount));
        when(transactionRepository.save(any(Transaction.class)))
                .thenAnswer(i -> i.getArgument(0));

        var result = transactionService.transfer(request);

        assertNotNull(result);
        assertEquals("TRANSFER", result.getType());
        assertEquals(BigDecimal.valueOf(800), account.getBalance());
        assertEquals(BigDecimal.valueOf(700), toAccount.getBalance());
    }

    @Test
    void shouldThrowWhenAccountNotFound() {
        when(accountRepository.findByAccountNumber("INVALID"))
                .thenReturn(Optional.empty());

        request.setAccountNumber("INVALID");

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> transactionService.deposit(request));

        assertTrue(ex.getMessage().contains("Account not found"));
    }
}
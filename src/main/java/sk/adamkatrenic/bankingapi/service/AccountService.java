package sk.adamkatrenic.bankingapi.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sk.adamkatrenic.bankingapi.dto.AccountResponse;
import sk.adamkatrenic.bankingapi.entity.Account;
import sk.adamkatrenic.bankingapi.entity.User;
import sk.adamkatrenic.bankingapi.repository.AccountRepository;
import sk.adamkatrenic.bankingapi.repository.TransactionRepository;
import sk.adamkatrenic.bankingapi.repository.UserRepository;

import java.math.BigDecimal;
import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;

    public AccountResponse createAccount(String email) {

        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));

        Account account = new Account();
        account.setUser(user);
        account.setAccountNumber("SK" + UUID.randomUUID()
                .toString().replace("-", "").substring(0, 16).toUpperCase());
        account.setBalance(BigDecimal.valueOf(10000));

        Account saved = accountRepository.save(account);
        return toResponse(saved);
    }

    public List<AccountResponse>getMyAccounts(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));

        return accountRepository.findByUserId(user.getId())
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public AccountResponse deleteAccount(Long accountId, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        if (!account.getUser().getId().equals(user.getId())) {
            throw new org.springframework.security.access.AccessDeniedException("You are not authorized to delete this account");
        }

        AccountResponse response = new AccountResponse();
        response.setId(account.getId());
        response.setAccountNumber(account.getAccountNumber());

        transactionRepository.deleteByFromAccountId(accountId);
        transactionRepository.deleteByToAccountId(accountId);

        accountRepository.delete(account);

        return response;
    }

    private AccountResponse toResponse(Account account){
        AccountResponse response = new AccountResponse();
        response.setId(account.getId());
        response.setAccountNumber(account.getAccountNumber());
        response.setBalance(account.getBalance());
        response.setCreatedAt(account.getCreatedAt());
        return response;
    }
}

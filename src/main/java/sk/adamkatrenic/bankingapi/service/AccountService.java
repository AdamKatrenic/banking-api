package sk.adamkatrenic.bankingapi.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sk.adamkatrenic.bankingapi.dto.AccountResponse;
import sk.adamkatrenic.bankingapi.entity.Account;
import sk.adamkatrenic.bankingapi.entity.User;
import sk.adamkatrenic.bankingapi.repository.AccountRepository;
import sk.adamkatrenic.bankingapi.repository.UserRepository;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final UserRepository userRepository;

    public AccountResponse createAccount(String email) {

        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));

        Account account = new Account();
        account.setUser(user);
        account.setAccountNumber("SK" + UUID.randomUUID()
                .toString().replace("-", "").substring(0, 16).toUpperCase());

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

    private AccountResponse toResponse(Account account){
        AccountResponse response = new AccountResponse();
        response.setId(account.getId());
        response.setAccountNumber(account.getAccountNumber());
        response.setBalance(account.getBalance());
        response.setCreatedAt(account.getCreatedAt());
        return response;
    }
}

package sk.adamkatrenic.bankingapi.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sk.adamkatrenic.bankingapi.dto.TransactionRequest;
import sk.adamkatrenic.bankingapi.dto.TransactionResponse;
import sk.adamkatrenic.bankingapi.entity.Account;
import sk.adamkatrenic.bankingapi.entity.Transaction;
import sk.adamkatrenic.bankingapi.entity.TransactionType;
import sk.adamkatrenic.bankingapi.repository.AccountRepository;
import sk.adamkatrenic.bankingapi.repository.TransactionRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    @Transactional
    public TransactionResponse deposit(TransactionRequest request) {
        Account account = findAccount(request.getAccountNumber());
        account.setBalance(account.getBalance().add(request.getAmount()));
        accountRepository.save(account);

        return saveTransaction(null, account, request.getAmount(), TransactionType.DEPOSIT);
    }

    @Transactional
    public TransactionResponse withdraw(TransactionRequest request) {
        Account account = findAccount(request.getAccountNumber());

        if (account.getBalance().compareTo(request.getAmount()) < 0) {
            throw new RuntimeException("Insufficient funds");
        }

        account.setBalance(account.getBalance().subtract(request.getAmount()));
        accountRepository.save(account);

        return saveTransaction(account, null, request.getAmount(), TransactionType.WITHDRAWAL);
    }

    @Transactional
    public TransactionResponse transfer(TransactionRequest request) {
        Account from = findAccount(request.getAccountNumber());
        Account to = findAccount(request.getToAccountNumber());

        if (from.getBalance().compareTo(request.getAmount()) < 0) {
            throw new RuntimeException("Insufficient funds");
        }

        from.setBalance(from.getBalance().subtract(request.getAmount()));
        to.setBalance(to.getBalance().add(request.getAmount()));

        accountRepository.save(from);
        accountRepository.save(to);

        return saveTransaction(from, to, request.getAmount(), TransactionType.TRANSFER);
    }

    public List<TransactionResponse> getHistory(String accountNumber) {
        Account account = findAccount(accountNumber);
        return transactionRepository
                .findByFromAccountIdOrToAccountIdOrderByCreatedAtDesc(
                        account.getId(), account.getId())
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private Account findAccount(String accountNumber) {
        return accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new RuntimeException("Account not found: " + accountNumber));
    }

    private TransactionResponse saveTransaction(Account from, Account to,
                                                java.math.BigDecimal amount, TransactionType type) {
        Transaction tx = new Transaction();
        tx.setFromAccount(from);
        tx.setToAccount(to);
        tx.setAmount(amount);
        tx.setType(type);
        return toResponse(transactionRepository.save(tx));
    }

    private TransactionResponse toResponse(Transaction tx) {
        TransactionResponse r = new TransactionResponse();
        r.setId(tx.getId());
        r.setType(tx.getType().name());
        r.setAmount(tx.getAmount());
        r.setFromAccount(tx.getFromAccount() != null ?
                tx.getFromAccount().getAccountNumber() : null);
        r.setToAccount(tx.getToAccount() != null ?
                tx.getToAccount().getAccountNumber() : null);
        r.setCreatedAt(tx.getCreatedAt());
        return r;
    }
}
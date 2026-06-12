package sk.adamkatrenic.bankingapi.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sk.adamkatrenic.bankingapi.entity.Account;
import sk.adamkatrenic.bankingapi.entity.Transaction;
import sk.adamkatrenic.bankingapi.repository.AccountRepository;
import sk.adamkatrenic.bankingapi.repository.TransactionRepository;

import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExportService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    public byte[] exportTransactionsCsv(String accountNumber) {
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        List<Transaction> transactions = transactionRepository
                .findByFromAccountIdOrToAccountIdOrderByCreatedAtDesc(
                        account.getId(), account.getId());

        StringBuilder csv = new StringBuilder();
        csv.append("ID,Type,Amount,From Account,To Account,Date\n");

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        for (Transaction tx : transactions) {
            csv.append(tx.getId()).append(",");
            csv.append(tx.getType()).append(",");
            csv.append(tx.getAmount()).append(",");
            csv.append(tx.getFromAccount() != null ? tx.getFromAccount().getAccountNumber() : "").append(",");
            csv.append(tx.getToAccount() != null ? tx.getToAccount().getAccountNumber() : "").append(",");
            csv.append(tx.getCreatedAt().format(formatter)).append("\n");
        }

        return csv.toString().getBytes(StandardCharsets.UTF_8);
    }
}
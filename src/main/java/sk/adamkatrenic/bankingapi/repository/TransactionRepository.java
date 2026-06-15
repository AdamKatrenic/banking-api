package sk.adamkatrenic.bankingapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sk.adamkatrenic.bankingapi.entity.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    Page<Transaction> findByFromAccountIdOrToAccountIdOrderByCreatedAtDesc(
            Long fromAccountId, Long toAccountId, Pageable pageable);
}

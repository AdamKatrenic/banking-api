package sk.adamkatrenic.bankingapi.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class TransactionResponse {

    private Long id;
    private String type;
    private BigDecimal amount;
    private String fromAccount;
    private String toAccount;
    private LocalDateTime createdAt;
}

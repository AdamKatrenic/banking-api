package sk.adamkatrenic.bankingapi.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class TransactionRequest {

    @NotBlank
    private String accountNumber;

    @Positive
    private BigDecimal amount;
    private String toAccountNumber;
}

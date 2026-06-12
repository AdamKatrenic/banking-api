package sk.adamkatrenic.bankingapi.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import sk.adamkatrenic.bankingapi.service.ExportService;

@RestController
@RequestMapping("/api/export")
@RequiredArgsConstructor
public class ExportController {

    private final ExportService exportService;

    @GetMapping("/transactions/{accountNumber}")
    public ResponseEntity<byte[]> exportTransactions(
            @PathVariable String accountNumber,
            @AuthenticationPrincipal UserDetails userDetails) {

        byte[] csv = exportService.exportTransactionsCsv(accountNumber);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=transactions_" + accountNumber + ".csv")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(csv);
    }
}
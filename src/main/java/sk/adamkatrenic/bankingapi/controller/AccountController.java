package sk.adamkatrenic.bankingapi.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import sk.adamkatrenic.bankingapi.dto.AccountResponse;
import sk.adamkatrenic.bankingapi.service.AccountService;

import java.util.List;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @PostMapping
    public ResponseEntity<AccountResponse> createAccount(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(accountService.createAccount(userDetails.getUsername()));
    }

    @GetMapping
    public ResponseEntity<List<AccountResponse>> getMyAccounts(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(accountService.getMyAccounts(userDetails.getUsername()));
    }

    @DeleteMapping("/{accountId}")
    public ResponseEntity<AccountResponse> deleteAccount(@PathVariable Long accountId, @AuthenticationPrincipal UserDetails userDetails) {

        return ResponseEntity.ok(accountService.deleteAccount(accountId, userDetails.getUsername()));
    }
}

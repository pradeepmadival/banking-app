package com.pradeep.bankingapp.controller;

import com.pradeep.bankingapp.model.Account;
import com.pradeep.bankingapp.model.Transaction;
import com.pradeep.bankingapp.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/accounts")
public class AccountController {

    @Autowired
    private AccountService accountService;

    // Create account
    @PostMapping
    public ResponseEntity<Account> createAccount(@RequestBody Map<String, Object> request) {
        String holderName = (String) request.get("holderName");
        Double initialDeposit = Double.valueOf(request.get("initialDeposit").toString());
        String type = (String) request.get("type");

        Account account = accountService.createAccount(holderName, initialDeposit, type);
        return new ResponseEntity<>(account, HttpStatus.CREATED);
    }

    // Get all accounts
    @GetMapping
    public ResponseEntity<List<Account>> getAllAccounts() {
        return ResponseEntity.ok(accountService.getAllAccounts());
    }

    // Get account by number
    @GetMapping("/{accountNumber}")
    public ResponseEntity<Account> getAccount(@PathVariable String accountNumber) {
        return ResponseEntity.ok(accountService.getAccountByNumber(accountNumber));
    }

    // Deposit
    @PostMapping("/{accountNumber}/deposit")
    public ResponseEntity<Account> deposit(@PathVariable String accountNumber, @RequestBody Map<String, Double> request) {
        Account account = accountService.deposit(accountNumber, request.get("amount"));
        return ResponseEntity.ok(account);
    }

    // Withdraw
    @PostMapping("/{accountNumber}/withdraw")
    public ResponseEntity<Account> withdraw(@PathVariable String accountNumber, @RequestBody Map<String, Double> request) {
        Account account = accountService.withdraw(accountNumber, request.get("amount"));
        return ResponseEntity.ok(account);
    }

    // Transfer
    @PostMapping("/transfer")
    public ResponseEntity<String> transfer(@RequestBody Map<String, Object> request) {
        String from = (String) request.get("fromAccountNumber");
        String to = (String) request.get("toAccountNumber");
        Double amount = Double.valueOf(request.get("amount").toString());

        accountService.transfer(from, to, amount);
        return ResponseEntity.ok("Transfer successful");
    }

    // Transaction history
    @GetMapping("/{accountNumber}/transactions")
    public ResponseEntity<List<Transaction>> getTransactionHistory(@PathVariable String accountNumber) {
        return ResponseEntity.ok(accountService.getTransactionHistory(accountNumber));
    }
}
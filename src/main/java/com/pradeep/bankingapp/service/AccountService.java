package com.pradeep.bankingapp.service;

import com.pradeep.bankingapp.exception.AccountNotFoundException;
import com.pradeep.bankingapp.exception.InsufficientFundsException;
import com.pradeep.bankingapp.exception.InvalidTransactionException;
import com.pradeep.bankingapp.model.*;
import com.pradeep.bankingapp.repository.AccountRepository;
import com.pradeep.bankingapp.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class AccountService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    // Create a new account
    public Account createAccount(String holderName, Double initialDeposit, String type) {
        if (initialDeposit < 0) {
            throw new InvalidTransactionException("Initial deposit cannot be negative");
        }

        String accountNumber = generateAccountNumber();
        Account account;

        if (type.equalsIgnoreCase("SAVINGS")) {
            account = new SavingsAccount(accountNumber, holderName, initialDeposit);
        } else if (type.equalsIgnoreCase("CURRENT")) {
            account = new CurrentAccount(accountNumber, holderName, initialDeposit);
        } else {
            throw new InvalidTransactionException("Invalid account type: " + type);
        }

        return accountRepository.save(account);
    }

    // Deposit money
    @Transactional
    public Account deposit(String accountNumber, Double amount) {
        if (amount <= 0) {
            throw new InvalidTransactionException("Deposit amount must be positive");
        }

        Account account = getAccountByNumber(accountNumber);
        account.deposit(amount);
        accountRepository.save(account);

        logTransaction(accountNumber, TransactionType.DEPOSIT, amount, "Deposit successful");
        return account;
    }

    // Withdraw money
    @Transactional
    public Account withdraw(String accountNumber, Double amount) {
        if (amount <= 0) {
            throw new InvalidTransactionException("Withdrawal amount must be positive");
        }

        Account account = getAccountByNumber(accountNumber);

        if (account instanceof CurrentAccount currentAccount) {
            if (!currentAccount.canWithdraw(amount)) {
                throw new InsufficientFundsException("Withdrawal exceeds overdraft limit");
            }
        } else {
            if (account.getBalance() < amount) {
                throw new InsufficientFundsException("Insufficient balance");
            }
        }

        account.withdraw(amount);
        accountRepository.save(account);

        logTransaction(accountNumber, TransactionType.WITHDRAW, amount, "Withdrawal successful");
        return account;
    }

    // Transfer money between accounts
    @Transactional
    public void transfer(String fromAccountNumber, String toAccountNumber, Double amount) {
        if (fromAccountNumber.equals(toAccountNumber)) {
            throw new InvalidTransactionException("Cannot transfer to the same account");
        }

        withdraw(fromAccountNumber, amount);
        deposit(toAccountNumber, amount);

        logTransaction(fromAccountNumber, TransactionType.TRANSFER_SENT, amount, "Transfer to " + toAccountNumber);
        logTransaction(toAccountNumber, TransactionType.TRANSFER_RECEIVED, amount, "Transfer from " + fromAccountNumber);
    }

    // Get account by number
    public Account getAccountByNumber(String accountNumber) {
        return accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new AccountNotFoundException("Account not found: " + accountNumber));
    }

    // Get all accounts
    public List<Account> getAllAccounts() {
        return accountRepository.findAll();
    }

    // Get transaction history
    public List<Transaction> getTransactionHistory(String accountNumber) {
        return transactionRepository.findByAccountNumberOrderByTimestampDesc(accountNumber);
    }

    // Helper: log a transaction
    private void logTransaction(String accountNumber, TransactionType type, Double amount, String remarks) {
        Transaction txn = new Transaction();
        txn.setAccountNumber(accountNumber);
        txn.setType(type);
        txn.setAmount(amount);
        txn.setRemarks(remarks);
        transactionRepository.save(txn);
    }

    // Helper: generate unique account number
    private String generateAccountNumber() {
        return "ACC" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
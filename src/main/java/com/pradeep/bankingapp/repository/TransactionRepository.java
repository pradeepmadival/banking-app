package com.pradeep.bankingapp.repository;

import com.pradeep.bankingapp.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByAccountNumberOrderByTimestampDesc(String accountNumber);
}
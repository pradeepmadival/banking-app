package com.pradeep.bankingapp.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Entity
@DiscriminatorValue("SAVINGS")
@Getter
@Setter
@NoArgsConstructor
public class SavingsAccount extends Account {

    private Double interestRate = 4.0; // default 4% annual

    public SavingsAccount(String accountNumber, String holderName, Double balance) {
        super(accountNumber, holderName, balance);
    }

    @Override
    public Double calculateInterest() {
        return getBalance() * (interestRate / 100);
    }
}
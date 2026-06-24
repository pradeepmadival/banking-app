package com.pradeep.bankingapp.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Entity
@DiscriminatorValue("CURRENT")
@Getter
@Setter
@NoArgsConstructor
public class CurrentAccount extends Account {

    private Double overdraftLimit = 5000.0; // default overdraft allowed

    public CurrentAccount(String accountNumber, String holderName, Double balance) {
        super(accountNumber, holderName, balance);
    }

    @Override
    public Double calculateInterest() {
        return 0.0; // current accounts typically don't earn interest
    }

    public boolean canWithdraw(Double amount) {
        return (getBalance() - amount) >= -overdraftLimit;
    }
}
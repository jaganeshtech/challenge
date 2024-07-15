package com.dws.challenge.service;

import com.dws.challenge.domain.Account;
import com.dws.challenge.domain.Transaction;
import com.dws.challenge.exception.InvalidAccountException;
import com.dws.challenge.repository.AccountsRepository;
import lombok.Getter;
import lombok.Synchronized;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BalanceTransferService {

    @Getter
    private final AccountsRepository accountsRepository;

    @Autowired
    private final EmailNotificationService emailNotificationService;

    @Autowired
    public BalanceTransferService(AccountsRepository accountsRepository, EmailNotificationService emailNotificationService) {
        this.accountsRepository = accountsRepository;
        this.emailNotificationService = emailNotificationService;
    }


    @Synchronized
    public String balanceTransfer(Transaction transaction) throws InvalidAccountException {

        Account fromAccount = this.accountsRepository.getAccount(transaction.getAccountFromId());
        Account toAccount = this.accountsRepository.getAccount(transaction.getAccountToId());

        if (fromAccount == null) {
            throw new InvalidAccountException("Source Account ID " + transaction.getAccountFromId() + " not found ");
        }
        if (toAccount == null) {
            throw new InvalidAccountException("Beneficiary Account ID not found ");
        }

        if (fromAccount.getBalance().compareTo(transaction.getAmount()) >= 0) {

            fromAccount.setBalance(fromAccount.getBalance().subtract(transaction.getAmount()));
            toAccount.setBalance(toAccount.getBalance().add(transaction.getAmount()));
            this.accountsRepository.update(fromAccount);
            this.accountsRepository.update(toAccount);
            this.emailNotificationService.notifyAboutTransfer(fromAccount, "Amount " + transaction.getAmount() + " Transaction completed successfully to beneficiary account " + transaction.getAccountToId());
            this.emailNotificationService.notifyAboutTransfer(toAccount, "Credited Amount " + transaction.getAmount() + " from " + transaction.getAccountFromId());
            return "Transaction successful";
        } else {
            this.emailNotificationService.notifyAboutTransfer(fromAccount, "Amount " + transaction.getAmount() + " Transaction failed to beneficiary account " + transaction.getAccountToId() + ",due to Insufficient Balance");
            throw new RuntimeException("Transaction Failed ,Insufficient Balance");
        }
    }
}

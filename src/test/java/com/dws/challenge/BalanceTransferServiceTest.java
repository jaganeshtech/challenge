package com.dws.challenge;

import com.dws.challenge.domain.Account;
import com.dws.challenge.domain.Transaction;
import com.dws.challenge.service.AccountsService;
import com.dws.challenge.service.BalanceTransferService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class BalanceTransferServiceTest {

  @Autowired
  private AccountsService accountsService;
  @Autowired
  private BalanceTransferService balanceTransferService;

  @BeforeEach
  void prepareMockMvc() {
    // Reset the existing accounts before each test.
    accountsService.getAccountsRepository().clearAccounts();
    Account account1 = new Account("Id-1");
    account1.setBalance(new BigDecimal(1000));
    this.accountsService.createAccount(account1);

    Account account2 = new Account("Id-2");
    account2.setBalance(new BigDecimal(1000));
    this.accountsService.createAccount(account2);
  }

  @Test
  void validAmountTransfer() {
    Transaction transaction = new Transaction("Id-1","Id-2",new BigDecimal(100));
    this.balanceTransferService.balanceTransfer(transaction);

    assertThat(this.accountsService.getAccount("Id-2").getBalance()).isEqualTo(new BigDecimal(1100));
  }

  @Test
  void overdraftAmountTransfer() {
    Transaction transaction = new Transaction("Id-1", "Id-2", new BigDecimal(1030));
    try {
      this.balanceTransferService.balanceTransfer(transaction);
    } catch(Exception e) {
      assertThat(e.getMessage()).isEqualTo("Transaction Failed ,Insufficient Balance");
    }
  }

  @Test
  void negativeAmountTransfer() {
    Transaction transaction = new Transaction("Id-1", "Id-2", new BigDecimal(-1030));
    try {
      this.balanceTransferService.balanceTransfer(transaction);
    } catch(Exception e) {
      assertThat(e.getMessage()).isEqualTo("Transaction Failed ,Insufficient Balance");
    }
  }
  @Test
  void emptyFromAccountIDTransfer() {
    Transaction transaction = new Transaction("", "Id-2", new BigDecimal(100));
    try {
      this.balanceTransferService.balanceTransfer(transaction);
    } catch(Exception e) {
      assertThat(e.getMessage()).isEqualTo("Source Account ID  not found ");
    }
  }
  @Test
  void emptyToAccountIDTransfer() {
    Transaction transaction = new Transaction("Id-1", "", new BigDecimal(100));
    try {
      this.balanceTransferService.balanceTransfer(transaction);
    } catch(Exception e) {
      assertThat(e.getMessage()).isEqualTo("Beneficiary Account ID not found ");
    }
  }

}

package com.dws.challenge.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class Transaction {

  @NotNull
  @NotEmpty
  private  String accountFromId;
  @NotNull
  @NotEmpty
  private  String accountToId;




  @NotNull
  @Min(value = 0, message = "Transfer Value must be Positive.")
  private BigDecimal amount;



  @JsonCreator
  public Transaction(@JsonProperty("accountFromId") String accountFromId,
                     @JsonProperty("accountToId") String accountToId,
                     @JsonProperty("amount") BigDecimal amount) {
    this.accountFromId = accountFromId;
    this.accountToId = accountToId;
    this.amount= amount;

  }
}

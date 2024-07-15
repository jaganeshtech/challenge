package com.dws.challenge;

import com.dws.challenge.domain.Account;
import com.dws.challenge.service.AccountsService;
import com.dws.challenge.service.BalanceTransferService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@WebAppConfiguration
class BalanceTransferControllerTest {

    private MockMvc mockMvc;

    @Autowired
    private AccountsService accountsService;
    @Autowired
    private BalanceTransferService balanceTransferService;
    @Autowired
    private WebApplicationContext webApplicationContext;

    @BeforeEach
    void prepareMockMvc() {
        this.mockMvc = webAppContextSetup(this.webApplicationContext).build();

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
    void validBalanceTransfer() throws Exception {
        // transfer amount from ID-1 to ID-2 when ID-1 have enough balance
        this.mockMvc.perform(post("/v1/balanceTransfer").contentType(MediaType.APPLICATION_JSON)
                .content("{\"accountFromId\": \"Id-1\",\"accountToId\" :\"Id-2\",\"amount\" :10.00 }")).andExpect(status().isOk());
    }

    @Test
    void invalidBalanceTransfer() throws Exception {
        // transfer amount from ID-1 to ID-2 when ID-1 have enough balance
        this.mockMvc.perform(post("/v1/balanceTransfer").contentType(MediaType.APPLICATION_JSON)
                .content("{\"accountFromId\": \"Id-1\",\"accountToId\" :\"Id-2\",\"amount\" :1011.00 }")).andExpect(status().isBadRequest());
    }

    @Test
    void invalidBalanceTransferMessageCheck() throws Exception {
        // transfer amount from ID-1 to ID-2 when ID-1 have not having enough balance
        this.mockMvc.perform(post("/v1/balanceTransfer").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"accountFromId\": \"Id-1\",\"accountToId\" :\"Id-2\",\"amount\" :1011.00 }")).andExpect(status().isBadRequest())
                .andExpect(content().string("Transaction Failed ,Insufficient Balance"));
        ;
    }

    @Test
    void validBalanceTransferMessageCheck() throws Exception {
        // transfer amount from ID-1 to ID-2 when ID-1 have enough balance
        this.mockMvc.perform(post("/v1/balanceTransfer").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"accountFromId\": \"Id-1\",\"accountToId\" :\"Id-2\",\"amount\" :900.00 }")).andExpect(status().isOk())
                .andExpect(content().string("Transaction successful"));
    }

    @Test
    void negativeAmountTransferStatusCheck() throws Exception {
        // transfer amount from ID-1 to ID-2 when ID-1 have enough balance
        this.mockMvc.perform(post("/v1/balanceTransfer").contentType(MediaType.APPLICATION_JSON)
                .content("{\"accountFromId\": \"Id-1\",\"accountToId\" :\"Id-2\",\"amount\" :-900.00 }")).andExpect(status().isBadRequest());
    }

    @Test
    void withoutFromIDTransferCheck() throws Exception {
        // transfer amount without source ID  to ID-2
        this.mockMvc.perform(post("/v1/balanceTransfer").contentType(MediaType.APPLICATION_JSON)
                .content("{\"accountFromId\": \"\",\"accountToId\" :\"Id-2\",\"amount\" :-900.00 }")).andExpect(status().isBadRequest());
    }

    @Test
    void withoutToIDTransferCheck() throws Exception {
        // transfer amount  from ID-1 without beneficiary (to account)
        this.mockMvc.perform(post("/v1/balanceTransfer").contentType(MediaType.APPLICATION_JSON)
                .content("{\"accountFromId\" :\"Id-1\",\"amount\" :900.00 }")).andExpect(status().isBadRequest());
    }


    @Test
    void unknownIDTransferCheck() throws Exception {
        // unknown beneficiary transfer check
        this.mockMvc.perform(post("/v1/balanceTransfer").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"accountFromId\": \"Id-1\",\"accountToId\" :\"Id-3\",\"amount\" :900.00 }")).andExpect(status().isBadRequest())
                .andExpect(content().string("Account ID Id-3 not found "));
        ;
    }

}

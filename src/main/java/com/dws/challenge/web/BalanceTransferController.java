package com.dws.challenge.web;

import com.dws.challenge.domain.Transaction;
import com.dws.challenge.service.BalanceTransferService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/balanceTransfer")
@Slf4j
public class BalanceTransferController {

    private final BalanceTransferService balanceTransferService;

    @Autowired
    public BalanceTransferController(BalanceTransferService balanceTransferService) {
        this.balanceTransferService = balanceTransferService;

    }


    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> balanceTransfer(@RequestBody @Valid Transaction transaction) {
        log.info("Transfer request" + transaction);
        String message = null;
        try {
            message = this.balanceTransferService.balanceTransfer(transaction);
        } catch (Exception exception) {
            return new ResponseEntity<>(exception.getMessage(), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(message, HttpStatus.OK);
    }


}

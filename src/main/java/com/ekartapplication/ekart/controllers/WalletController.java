package com.ekartapplication.ekart.controllers;

import com.ekartapplication.ekart.model.AddMoneyRequest;
import com.ekartapplication.ekart.dao.WalletDAO;
import com.ekartapplication.ekart.model.SuccessResponse;
import com.ekartapplication.ekart.service.wallet.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;

@RestController
@RequestMapping("wallet/v1")
public class WalletController {

    @Autowired
    WalletService service;

    @PutMapping(value = "/addmoney", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SuccessResponse> addAmountToWallet(@RequestBody AddMoneyRequest amount) {
        String message = service.addAmount(amount);
        return new ResponseEntity<>(new SuccessResponse(HttpStatus.OK.value(), Collections.singletonMap("message", message)), HttpStatus.OK);
    }


    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SuccessResponse> checkBalance(@RequestParam String userId) {
        WalletDAO walletDetails = service.checkBalance(userId);
        return new ResponseEntity<>(new SuccessResponse(HttpStatus.OK.value(), Collections.singleton(walletDetails)), HttpStatus.OK);
    }
}

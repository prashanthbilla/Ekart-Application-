package com.ekartapplication.ekart.controllers;


import com.ekartapplication.ekart.model.AddMultipleProductsRequest;
import com.ekartapplication.ekart.model.KartDetailsResponse;
import com.ekartapplication.ekart.model.SuccessResponse;
import com.ekartapplication.ekart.service.kart.KartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;

@RestController
@RequestMapping("kart/v1")
public class KartController {

    @Autowired
    KartService service;

    @PutMapping(value = "/addproducts", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAuthority('ROLE_USER') or hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<SuccessResponse> addProductsToWallet(@RequestBody AddMultipleProductsRequest addProducts) {
        String message = service.addMultipleProducts(addProducts);
        return new ResponseEntity<>(new SuccessResponse(HttpStatus.OK.value(), Collections.singletonMap("message", message)), HttpStatus.OK);
    }


    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAuthority('ROLE_USER') or hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<SuccessResponse> getKartDetails(@RequestParam String userId) {
        KartDetailsResponse kartDetailsResponse = service.getKartDetails(userId);
        return new ResponseEntity<>(new SuccessResponse(HttpStatus.OK.value(), Collections.singleton(kartDetailsResponse)), HttpStatus.OK);
    }

    @DeleteMapping(value = "/delete", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAuthority('ROLE_USER') or hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<SuccessResponse> deleteProductFromKart(@RequestParam String userId, @RequestParam String productId) {
        String response = service.deleteProductFromKart(userId, productId);
        return new ResponseEntity<>(new SuccessResponse(HttpStatus.OK.value(), Collections.singleton(response)), HttpStatus.OK);
    }

}

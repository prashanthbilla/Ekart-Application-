package com.ekartapplication.ekart.controllers;

import com.ekartapplication.ekart.dao.PaymentDAO;
import com.ekartapplication.ekart.model.*;
import com.ekartapplication.ekart.service.payment.PaymentService;
import com.ekartapplication.ekart.utils.enums.PaymentFilterModeSingle;
import com.ekartapplication.ekart.utils.enums.PaymentFilterMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/payment/v1")
public class PaymentController {

    @Autowired
    PaymentService service;

    @PutMapping(value = "/products", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAuthority('ROLE_USER') or hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<SuccessResponse> buyAllProductsInKart(@RequestBody PaymentRequestAllKart payment) {
        PaymentResponse response = service.buyAllProductsInKart(payment);
        return new ResponseEntity<>(new SuccessResponse(HttpStatus.OK.value(), Collections.singleton(response)), HttpStatus.OK);
    }

    @PutMapping(value = "/buysomeproducts", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAuthority('ROLE_USER') or hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<SuccessResponse> buySomeProducts(@RequestBody PaymentRequestSomeKart payment) {
        PaymentResponse response = service.buySomeProducts(payment);
        return new ResponseEntity<>(new SuccessResponse(HttpStatus.OK.value(), Collections.singleton(response)), HttpStatus.OK);
    }

    @GetMapping(value = "/getAll", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SuccessResponse> getPaymentDetails(@RequestParam PaymentFilterMode mode,
                                                             @RequestParam(required = false) String userId,
                                                             @RequestParam(required = false) String paymentStatus,
                                                             @RequestParam(required = false) String paymentMode) {


        List<PaymentDAO> paymentDAOList = service.getDetailsAll(mode, userId, paymentStatus, paymentMode);
        return new ResponseEntity<>(new SuccessResponse(HttpStatus.OK.value(), Collections.singleton(paymentDAOList)), HttpStatus.OK);
    }

    @GetMapping(value = "/get", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> getPaymentDetailsSingle(@RequestParam PaymentFilterModeSingle mode,
                                                          @RequestParam(required = false) String paymentId,
                                                          @RequestParam(required = false) String orderId) {


        PaymentDAO paymentDAO = service.getDetailsSingle(mode, paymentId, orderId);
        return new ResponseEntity<>(new SuccessResponse(HttpStatus.OK.value(), Collections.singleton(paymentDAO)), HttpStatus.OK);
    }

}

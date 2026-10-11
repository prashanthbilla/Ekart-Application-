package com.ekartapplication.ekart.controllers;


import com.ekartapplication.ekart.model.OrderResponse;
import com.ekartapplication.ekart.model.SuccessResponse;
import com.ekartapplication.ekart.model.SuccessResponseForOrder;
import com.ekartapplication.ekart.service.order.OrderService;
import com.ekartapplication.ekart.utils.enums.OrderFilterMode;
import com.ekartapplication.ekart.utils.enums.OrderFilterSingle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Date;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/order/v1")
@EnableScheduling
public class OrderController {

    @Autowired
    OrderService orderService;

    @GetMapping(value = "/getAll", produces = MediaType.APPLICATION_JSON_VALUE)
    //@PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<SuccessResponseForOrder> getOrderDetails(@RequestParam OrderFilterMode mode,
                                                                   @RequestParam(required = false) String userId,
                                                                   @RequestParam(required = false) Date orderDate,
                                                                   @RequestParam(required = false) String paymentStatus,
                                                                   @RequestParam(required = false) String orderStatus) {

        List<OrderResponse> orderResponseList = orderService.getOrderDetailsAll(mode, userId, orderDate, paymentStatus, orderStatus);
        return new ResponseEntity<>(new SuccessResponseForOrder(HttpStatus.OK.value(), orderResponseList), HttpStatus.OK);
    }

    @GetMapping(value = "/get", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SuccessResponse> getOrderDetailsByIds(@RequestParam OrderFilterSingle mode,
                                                                @RequestParam(required = false) String orderId,
                                                                @RequestParam(required = false) String transactionId) {

        OrderResponse orderResponse = orderService.getOrderDetailsByIds(mode, orderId, transactionId);
        return new ResponseEntity<>(new SuccessResponse(HttpStatus.OK.value(), Collections.singleton(orderResponse)), HttpStatus.OK);
    }


    @GetMapping(value = "/getOrders", produces = MediaType.APPLICATION_JSON_VALUE)
    //@PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Object> getOrderDetailsOnly(@RequestParam OrderFilterMode mode,
                                                           @RequestParam(required = false) String userId,
                                                           @RequestParam(required = false) Date orderDate,
                                                           @RequestParam(required = false) String paymentStatus,
                                                           @RequestParam(required = false) String orderStatus) {

        List<OrderResponse> orderResponseList = orderService.getOrderDetailsAll(mode, userId, orderDate, paymentStatus, orderStatus);
        return new ResponseEntity<>(orderResponseList, HttpStatus.OK);
    }

}

package com.ekartapplication.ekart.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class PaymentRequestSomeKart {
    private String userId;
    private String paymentMode;
    private List<String> products;
}

package com.ekartapplication.ekart.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class PaymentResponse {
    private PaymentSuccessfulResponse success;
    private PaymentUnSuccessfulResponse failure;

}

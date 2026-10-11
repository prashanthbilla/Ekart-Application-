package com.ekartapplication.ekart.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class PaymentUnSuccessfulResponse {

    private String userId;
    private String userName;
    private String paymentStatus;
    private Date paymentDate;
    private String reason;

}

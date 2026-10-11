package com.ekartapplication.ekart.model;

import com.ekartapplication.ekart.dao.PaymentDAO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;

@AllArgsConstructor@NoArgsConstructor
@Data
public class PaymentSuccessfulResponse {

    private PaymentDAO paymentDAO;
    private String paymentStatus;
    private Date paymentDate;

}

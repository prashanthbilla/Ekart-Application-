package com.ekartapplication.ekart.service.payment;

import com.ekartapplication.ekart.dao.PaymentDAO;
import com.ekartapplication.ekart.model.PaymentRequestAllKart;
import com.ekartapplication.ekart.model.PaymentRequestSomeKart;
import com.ekartapplication.ekart.model.PaymentResponse;
import com.ekartapplication.ekart.utils.enums.PaymentFilterModeSingle;
import com.ekartapplication.ekart.utils.enums.PaymentFilterMode;

import java.util.List;

public interface PaymentService {
    PaymentResponse buyAllProductsInKart(PaymentRequestAllKart payment);

    PaymentResponse buySomeProducts(PaymentRequestSomeKart payment);

    List<PaymentDAO> getDetailsAll(PaymentFilterMode mode, String userId, String paymentStatus, String paymentMode);

    PaymentDAO getDetailsSingle(PaymentFilterModeSingle mode, String paymentId, String orderId);
}

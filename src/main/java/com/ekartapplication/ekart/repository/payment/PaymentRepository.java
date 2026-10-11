package com.ekartapplication.ekart.repository.payment;

import com.ekartapplication.ekart.dao.PaymentDAO;

import java.util.List;

public interface PaymentRepository {
    boolean savePayment(PaymentDAO paymentDAO);

    List<PaymentDAO> getAllPaymentDetails();

    List<PaymentDAO> getAllPaymentDetailsByUserId(String userId);

    List<PaymentDAO> getAllPaymentDetailsByPaymentStatus(String paymentStatus);

    List<PaymentDAO> getAllPaymentDetailsByPPaymentMode(String paymentMode);

    PaymentDAO getPaymentDetailsByOrderId(String orderId);

    PaymentDAO getPaymentDetailsByPaymentId(String paymentId);
}

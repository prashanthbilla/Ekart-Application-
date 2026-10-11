package com.ekartapplication.ekart.service.payment;

import com.ekartapplication.ekart.dao.*;
import com.ekartapplication.ekart.model.*;
import com.ekartapplication.ekart.repository.kart.KartRepository;
import com.ekartapplication.ekart.repository.order.OrderRepository;
import com.ekartapplication.ekart.repository.payment.PaymentRepository;
import com.ekartapplication.ekart.repository.product.ProductRepository;
import com.ekartapplication.ekart.repository.wallet.WalletRepository;
import com.ekartapplication.ekart.service.kart.KartService;
import com.ekartapplication.ekart.service.user.UserService;
import com.ekartapplication.ekart.service.wallet.WalletService;
import com.ekartapplication.ekart.utils.IDGenUtil;
import com.ekartapplication.ekart.utils.enums.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.util.*;

@Service
public class PaymentServiceImpl implements PaymentService {

    @Autowired
    UserService userService;

    @Autowired
    WalletService walletService;

    @Autowired
    KartService kartService;

    @Autowired
    KartRepository kartRepository;

    @Autowired
    WalletRepository walletRepository;

    @Autowired
    PaymentRepository paymentRepository;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    OrderRepository orderRepository;


    @Override
    public PaymentResponse buyAllProductsInKart(PaymentRequestAllKart payment) {
        PaymentResponse response = null;
        double remainingBal;
        response = new PaymentResponse();
        StringBuilder address = new StringBuilder();
        KartDAO kartDAO = new KartDAO();
        OrderDAO orderDao = new OrderDAO();
        String mode = payment.getPaymentMode();
        PaymentDAO paymentDAO = new PaymentDAO();
        UserDAO user = userService.getUserByUserName(payment.getUserId());
        WalletDAO walletDAO = walletService.checkBalance(payment.getUserId());
        KartDetailsResponse kartResponse = kartService.getKartDetails(payment.getUserId());
        int earnedRewardPoints = kartResponse.getGetRewardPoints();
        paymentDAO.setPaymentId(UUID.randomUUID().toString());
        paymentDAO.setPaymentMode(mode);
        paymentDAO.setUserId(payment.getUserId());
        paymentDAO.setUserFullName(user.getFullName());
        paymentDAO.setPaymentDate(new Date(System.currentTimeMillis()));
        double totalWalletAmount = walletDAO.getAvlBal();
        double totalProductsPriceAfterDiscount = kartResponse.getPay();
        Map<String, Integer> products = kartResponse.getProducts();
        switch (mode) {
            case "WALLET":
                if (totalWalletAmount > totalProductsPriceAfterDiscount && kartResponse.getProductCount() != 0) {

                    orderDao.setOrderId(UUID.randomUUID().toString());

                    //payment
                    remainingBal = (totalWalletAmount - kartResponse.getPay());
                    paymentDAO.setPaymentStatus(PaymentStatus.SUCCESS.toString());
                    paymentDAO.setPaidAmount(kartResponse.getPay());
                    paymentDAO.setTransactionId(payment.getUserId() + IDGenUtil.getUniqueAlphaNumericString(8));
                    paymentDAO.setOrderId(orderDao.getOrderId());
                    paymentDAO.setDiscountPrice(kartResponse.getDiscountPrice());
                    paymentDAO.setTotalProducts(kartResponse.getProductCount());
                    paymentDAO.setTotalPrice(kartResponse.getTotalPrice());
                    paymentDAO.setProducts(this.getProductsWithMap(products).toString());
                    boolean isInsertedPayment = paymentRepository.savePayment(paymentDAO);
                    if (!isInsertedPayment)
                        return null;

                    //kart
                    kartDAO.setKartId(kartResponse.getKartId());
                    kartDAO.setUserId(kartResponse.getUserId());
                    kartDAO.setProducts("");
                    kartDAO.setPayTotal(0);
                    kartDAO.setTotalProducts(0);
                    kartDAO.setTotalProductsPrice(0);
                    kartDAO.setEarnRewardPoints(0);
                    kartDAO.setDiscountPrice(0);
                    boolean isUpdatedKart = kartRepository.updateOneProductToKart(kartDAO);
                    //wallet
                    walletDAO.setAvlBal(remainingBal);
                    walletDAO.setLastTransaction(paymentDAO.getPaidAmount());
                    walletDAO.setUsageRewardPoints(walletDAO.getUsageRewardPoints() + earnedRewardPoints);
                    boolean isUpdatedWallet = walletRepository.addMoneyToWalletUpdate(walletDAO);

                    //order

                    orderDao.setUserId(payment.getUserId());
                    orderDao.setProducts(this.getProductsWithMap(products).toString());
                    orderDao.setProductCount(paymentDAO.getTotalProducts());
                    orderDao.setTotalPrice(kartResponse.getTotalPrice());
                    orderDao.setDiscountPrice(paymentDAO.getDiscountPrice());
                    orderDao.setPay(paymentDAO.getPaidAmount());
                    orderDao.setOrderDate(paymentDAO.getPaymentDate());
                    orderDao.setMobileNumber(user.getMobileNum());
                    orderDao.setPaymentStatus(PaymentStatus.SUCCESS.toString());
                    orderDao.setPaidAmount(paymentDAO.getPaidAmount());
                    orderDao.setPaymentId(paymentDAO.getPaymentId());
                    orderDao.setTransactionId(paymentDAO.getTransactionId());
                    orderDao.setUserFullName(user.getFullName());
                    orderDao.setOrderStatus(OrderStatus.ORDERED.toString());
                    orderDao.setDeliveryDate("");
                    // setting Address for oder
                    if (address.isEmpty())
                        address.append(user.getHouseNo());
                    address.append(",").append(user.getLandmark());
                    address.append(",").append(user.getCity());
                    address.append(",").append(user.getPincode());
                    orderDao.setUserAddress(address.toString());
                    boolean isSavedOderDetails = orderRepository.saveOrder(orderDao);

                    //setting successful response

                    PaymentSuccessfulResponse successfulResponse = new PaymentSuccessfulResponse();
                    successfulResponse.setPaymentStatus(PaymentStatus.SUCCESS.toString());
                    successfulResponse.setPaymentDate(paymentDAO.getPaymentDate());
                    successfulResponse.setPaymentDAO(paymentDAO);
                    response.setFailure(null);
                    response.setSuccess(successfulResponse);

                } else {

                    //setting unsuccessful response

                    PaymentUnSuccessfulResponse unSuccessfulResponse = new PaymentUnSuccessfulResponse();
                    unSuccessfulResponse.setUserId(user.getEmailId());
                    unSuccessfulResponse.setUserName(user.getFullName());
                    unSuccessfulResponse.setPaymentDate(new Date(System.currentTimeMillis()));
                    unSuccessfulResponse.setPaymentStatus(PaymentStatus.FAILURE.toString());
                    unSuccessfulResponse.setReason("Insufficient Balance in Wallet, Add some money OR remove some products...");
                    response.setFailure(unSuccessfulResponse);
                    response.setSuccess(null);
                    return response;
                }
                break;
            case "PHONE_PAY":
                // PHONE_PAY service to be implemented
                break;
            case "PAYTM":
                // PAYTM service to be implemented
                break;
            case "GOOGLE_PAY":
                // GOOGLE_PAY service to be implemented
                break;
            case "DEBIT_CARD":
                // DEBIT_CARD service to be implemented
                break;
            case "CREDIT_CARD":
                // CREDIT_CARD service to be implemented
                break;
            default:
                break;
        }
        return response;
    }

    @Override
    public PaymentResponse buySomeProducts(PaymentRequestSomeKart payment) {
        PaymentResponse responseSuccess;
        PaymentResponse responseFailure;
        StringBuilder buyProductsForPayment = new StringBuilder();
        StringBuilder address = new StringBuilder();
        KartDAO kartDAO = new KartDAO();
        OrderDAO orderDAO = new OrderDAO();
        PaymentDAO paymentDAO = new PaymentDAO();
        String mode = payment.getPaymentMode();
        UserDAO user = userService.getUserByUserName(payment.getUserId());
        WalletDAO walletDAO = walletService.checkBalance(payment.getUserId());
        KartDetailsResponse kartResponse = kartService.getKartDetails(payment.getUserId());
        paymentDAO.setPaymentId(UUID.randomUUID().toString());
        paymentDAO.setPaymentMode(mode);
        paymentDAO.setUserId(payment.getUserId());
        paymentDAO.setUserFullName(user.getFullName());
        paymentDAO.setPaymentDate(new Date(System.currentTimeMillis()));
        double totalWalletAmount = walletDAO.getAvlBal();
        double totalProductsPriceAfterDiscount = 0;
        double productPrice = 0;
        double discountPrice = 0;
        int userRewardPoints = 0;
        int productCount = 0;
        StringBuilder remainingKartProducts;
        responseFailure = new PaymentResponse();
        responseSuccess = new PaymentResponse();
        responseFailure.setSuccess(null);
        responseSuccess.setFailure(null);
        PaymentUnSuccessfulResponse paymentUnSuccessfulResponse = new PaymentUnSuccessfulResponse();
        paymentUnSuccessfulResponse.setPaymentStatus(PaymentStatus.FAILURE.toString());
        paymentUnSuccessfulResponse.setPaymentDate(new Date(System.currentTimeMillis()));
        paymentUnSuccessfulResponse.setUserId(payment.getUserId());
        paymentUnSuccessfulResponse.setUserName(user.getFullName());
        responseFailure.setFailure(paymentUnSuccessfulResponse);

        if (kartResponse.getProductCount() < payment.getProducts().size())
            return responseFailure;
        List<String> buyingProducts = payment.getProducts();
        Map<String, Integer> kartProducts = kartResponse.getProducts();
        // Wallet and Kart logic
        for (String buyProduct : buyingProducts) {
            kartProducts.put(buyProduct, kartProducts.get(buyProduct) - 1);
            Product product = productRepository.getOneProduct(buyProduct);
            productPrice = productPrice + product.getActualPrice();
            discountPrice = discountPrice + ((product.getOfferPercentage() * product.getActualPrice()) / 100);
            userRewardPoints = userRewardPoints + product.getUsageRewardPoints();
            productCount = productCount + 1;
        }
        totalProductsPriceAfterDiscount = totalProductsPriceAfterDiscount + (productPrice - discountPrice);

        for (int i = 0; i < buyingProducts.size(); i++) {
            if (i == 0) {
                buyProductsForPayment.append(buyingProducts.get(i));
            } else {
                buyProductsForPayment.append(",").append(buyingProducts.get(i));
            }
        }

        remainingKartProducts = this.getProductsWithMap(kartProducts);

        if (totalWalletAmount > totalProductsPriceAfterDiscount) {

            // Payment Logic
            orderDAO.setOrderId(UUID.randomUUID().toString());
            paymentDAO.setTransactionId(payment.getUserId() + IDGenUtil.getUniqueAlphaNumericString(8));
            paymentDAO.setPaymentStatus(PaymentStatus.SUCCESS.toString());
            paymentDAO.setPaidAmount(totalProductsPriceAfterDiscount);
            paymentDAO.setOrderId(orderDAO.getOrderId());
            paymentDAO.setDiscountPrice(discountPrice);
            paymentDAO.setProducts(buyProductsForPayment.toString());
            paymentDAO.setTotalPrice(productPrice);
            paymentDAO.setTotalProducts(productCount);
            boolean isInsertedPayment = paymentRepository.savePayment(paymentDAO);

            //Kart
            kartDAO.setKartId(kartResponse.getKartId());
            kartDAO.setUserId(kartResponse.getUserId());
            kartDAO.setProducts(remainingKartProducts.toString());
            kartDAO.setTotalProducts(kartResponse.getProductCount() - productCount);
            kartDAO.setTotalProductsPrice(kartResponse.getTotalPrice() - productPrice);
            kartDAO.setDiscountPrice(kartResponse.getDiscountPrice() - discountPrice);
            kartDAO.setPayTotal(kartResponse.getTotalPrice() - totalProductsPriceAfterDiscount);
            kartDAO.setEarnRewardPoints(kartResponse.getGetRewardPoints() - userRewardPoints);
            kartDAO.setPayTotal(kartDAO.getTotalProductsPrice() - kartDAO.getDiscountPrice());
            boolean isUpdatedKart = kartRepository.updateOneProductToKart(kartDAO);

            //Wallet

            walletDAO.setAvlBal(totalWalletAmount - totalProductsPriceAfterDiscount);
            walletDAO.setLastTransaction(totalProductsPriceAfterDiscount);
            walletDAO.setUsageRewardPoints(walletDAO.getUsageRewardPoints() + userRewardPoints);
            boolean isUpdatedWallet = walletRepository.addMoneyToWalletUpdate(walletDAO);

            //order

            orderDAO.setUserId(payment.getUserId());
            orderDAO.setProducts(buyProductsForPayment.toString());
            orderDAO.setProductCount(paymentDAO.getTotalProducts());
            orderDAO.setTotalPrice(productPrice);
            orderDAO.setDiscountPrice(paymentDAO.getDiscountPrice());
            orderDAO.setPay(paymentDAO.getPaidAmount());
            orderDAO.setOrderDate(paymentDAO.getPaymentDate());
            orderDAO.setMobileNumber(user.getMobileNum());
            orderDAO.setPaymentStatus(PaymentStatus.SUCCESS.toString());
            orderDAO.setPaidAmount(paymentDAO.getPaidAmount());
            orderDAO.setPaymentId(paymentDAO.getPaymentId());
            orderDAO.setTransactionId(paymentDAO.getTransactionId());
            orderDAO.setOrderStatus(OrderStatus.ORDERED.toString());
            orderDAO.setDeliveryDate("");
            orderDAO.setUserFullName(user.getFullName());

            // setting Address for oder

            if (address.isEmpty())
                address.append(user.getHouseNo());
            address.append(",").append(user.getLandmark());
            address.append(",").append(user.getCity());
            address.append(",").append(user.getPincode());
            orderDAO.setUserAddress(address.toString());
            boolean isSavedOderDetails = orderRepository.saveOrder(orderDAO);

            //setting successful response

            PaymentSuccessfulResponse paymentSuccessfulResponse = new PaymentSuccessfulResponse();
            paymentSuccessfulResponse.setPaymentDate(new Date(System.currentTimeMillis()));
            paymentSuccessfulResponse.setPaymentStatus(PaymentStatus.SUCCESS.toString());
            responseSuccess.setFailure(null);
            paymentSuccessfulResponse.setPaymentDAO(paymentDAO);
            responseSuccess.setSuccess(paymentSuccessfulResponse);

            return responseSuccess;

        } else {

            //setting unsuccessful response
            responseFailure.setSuccess(null);
            paymentUnSuccessfulResponse.setReason("Insufficient bal, add some amount OR remove some products");
            return responseFailure;
        }

    }

    @Override
    public List<PaymentDAO> getDetailsAll(PaymentFilterMode mode, String userId, String paymentStatus, String paymentMode) {
        List<PaymentDAO> paymentDAOList = switch (mode) {
            case NONE -> paymentRepository.getAllPaymentDetails();
            case USER_ID -> paymentRepository.getAllPaymentDetailsByUserId(userId);
            case PAYMENT_STATUS -> paymentRepository.getAllPaymentDetailsByPaymentStatus(paymentStatus);
            case PAYMENT_MODE -> paymentRepository.getAllPaymentDetailsByPPaymentMode(paymentMode);
        };
        if (Objects.isNull(paymentDAOList))
            return new ArrayList<>();
        return paymentDAOList;
    }

    @Override
    public PaymentDAO getDetailsSingle(PaymentFilterModeSingle mode, String paymentId, String orderId) {
        PaymentDAO paymentDAO = switch (mode) {
            case ORDER_ID -> paymentRepository.getPaymentDetailsByOrderId(orderId);
            case PAYMENT_ID -> paymentRepository.getPaymentDetailsByPaymentId(paymentId);
        };
        if (Objects.isNull(paymentDAO))
            return null;
        return paymentDAO;
    }

    public StringBuilder getProductsWithMap(Map<String, Integer> products) {
        StringBuilder remainingKartProducts = new StringBuilder();
        for (String product : products.keySet()) {
            int n = products.get(product);
            while (n > 0) {
                if (remainingKartProducts.isEmpty()) {
                    remainingKartProducts.append(product);
                } else {
                    remainingKartProducts.append(",").append(product);
                }
                n--;
            }
        }
        return remainingKartProducts;
    }
}


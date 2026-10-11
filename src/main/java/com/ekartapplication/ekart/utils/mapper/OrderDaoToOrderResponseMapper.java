package com.ekartapplication.ekart.utils.mapper;

import com.ekartapplication.ekart.dao.OrderDAO;
import com.ekartapplication.ekart.model.OrderResponse;
import com.ekartapplication.ekart.model.UserAddress;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;


public class OrderDaoToOrderResponseMapper {

    public List<OrderResponse> orderDaoToOrderResponse(List<OrderDAO> orderDAOList) {
        List<OrderResponse> list = new ArrayList<>();
        OrderResponse orderResponse;
        for (OrderDAO orderDAO1 : orderDAOList) {
            orderResponse = this.daoToResponseOne(orderDAO1);
            list.add(orderResponse);
        }
        return list;
    }

    public OrderResponse daoToResponseOne(OrderDAO orderDAO1) {

        UserAddress userAddress = new UserAddress();
        OrderResponse response = new OrderResponse();

        response.setOrderId(orderDAO1.getOrderId());
        response.setUserId(orderDAO1.getUserId());
        response.setProducts(orderDAO1.getProducts());
        response.setProductCount(orderDAO1.getProductCount());
        response.setTotalPrice(orderDAO1.getTotalPrice());
        response.setDiscountPrice(orderDAO1.getDiscountPrice());
        response.setPay(orderDAO1.getPay());
        response.setOrderDate(orderDAO1.getOrderDate());
        response.setMobileNumber(orderDAO1.getMobileNumber());
        response.setPaymentStatus(orderDAO1.getPaymentStatus());
        response.setPaidAmount(orderDAO1.getPaidAmount());
        response.setPaymentId(orderDAO1.getPaymentId());
        response.setUserFullName(orderDAO1.getUserFullName());
        response.setTransactionId(orderDAO1.getTransactionId());
        response.setOrderStatus(orderDAO1.getOrderStatus());
        response.setDeliveryDate(orderDAO1
                .getDeliveryDate());
        String userAddress1 = orderDAO1.getUserAddress();
        String[] userAddressList = userAddress1.split(",");
        userAddress.setHouseNo(userAddressList[0]);
        userAddress.setLandmark(userAddressList[1]);
        userAddress.setCity(userAddressList[2]);
        userAddress.setPincode(userAddressList[3]);
        userAddress.setMobileNo(orderDAO1.getMobileNumber());
        userAddress.setUserFullName(orderDAO1.getUserFullName());
        response.setUserAddress(userAddress);
        return response;
    }
}

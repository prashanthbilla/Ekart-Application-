package com.ekartapplication.ekart.service.kart;

import com.ekartapplication.ekart.model.AddMultipleProductsRequest;
import com.ekartapplication.ekart.model.KartDetailsResponse;

public interface KartService {
    KartDetailsResponse getKartDetails(String userId);

    String addMultipleProducts(AddMultipleProductsRequest addProducts);

    String deleteProductFromKart(String userId, String productId);
}

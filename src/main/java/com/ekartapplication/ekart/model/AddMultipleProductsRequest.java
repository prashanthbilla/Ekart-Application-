package com.ekartapplication.ekart.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@NoArgsConstructor
@AllArgsConstructor
@Data
public class AddMultipleProductsRequest {
    private String userId;
    private List<String> products;
}

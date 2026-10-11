package com.ekartapplication.ekart.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import java.util.List;


@AllArgsConstructor
@NoArgsConstructor
@Data
public class SuccessResponseForOrder {
    private int status;
    private String message;
    private List<OrderResponse> data;

    public SuccessResponseForOrder(int status, List<OrderResponse> data) {

        this.status = status;
        this.data = data;

        if (status == HttpStatus.OK.value()) {
            this.message = "success";
        }
    }
}

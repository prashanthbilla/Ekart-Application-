package com.ekartapplication.ekart.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class SuccessResponse {
    private int status;
    private String message;
    private Object data;

    public SuccessResponse(int status, Object data) {

        this.status = status;
        this.data = data;

        if (status == HttpStatus.OK.value()) {
            this.message = "success";
        }
    }
}

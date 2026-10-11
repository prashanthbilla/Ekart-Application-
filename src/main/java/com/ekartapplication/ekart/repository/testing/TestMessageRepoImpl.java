package com.ekartapplication.ekart.repository.testing;

import com.ekartapplication.ekart.dao.AuthRequest;
import org.springframework.stereotype.Repository;

@Repository
public class TestMessageRepoImpl implements TestMessageRepo {
    @Override
    public String getMessage() {
        AuthRequest authRequest = new AuthRequest("RequestMessage--Hi", "ResponseMessage - Hello");
        return "THIS IS THE SPRING APPLICATION - " + authRequest.toString();
    }
}

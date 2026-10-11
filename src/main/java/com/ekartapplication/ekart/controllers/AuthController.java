package com.ekartapplication.ekart.controllers;

import com.ekartapplication.ekart.dao.AuthRequest;
import com.ekartapplication.ekart.model.SuccessResponse;
import com.ekartapplication.ekart.service.jwt.JwtService;
import com.ekartapplication.ekart.service.testing.TestMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth/v1")
public class AuthController {


    @Autowired
    private JwtService jwtService;
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private TestMessageService testMessage;

    @PostMapping("/authenticate")
    public ResponseEntity<Object> authGenerateToken(@RequestBody AuthRequest authRequest) {
        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword()));
        if (authentication.isAuthenticated()) {
            Map<String, String> response = new HashMap<>();
            response.put("token", jwtService.generateToken(authRequest.getUsername()));
            return ResponseEntity.ok(response);
        } else {
            throw new UsernameNotFoundException("invalid user request !");
        }
    }

    @GetMapping("/mes")
    public ResponseEntity<SuccessResponse> greetings() {
        return new ResponseEntity<>(new SuccessResponse(HttpStatus.OK.value(), "Success"), HttpStatus.OK);
    }
}

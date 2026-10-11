package com.ekartapplication.ekart.controllers;

import com.ekartapplication.ekart.dao.AuthRequest;
import com.ekartapplication.ekart.model.SuccessResponse;
import com.ekartapplication.ekart.service.jwt.JwtService;
import com.ekartapplication.ekart.service.testing.TestMessageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthControllerMockitoTest {

    @Mock
    private JwtService jwtService;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private TestMessageService testMessage;

    @Mock
    private Authentication authentication;

    private AuthController controller;

    @BeforeEach
    void setUp() {
        controller = new AuthController();
        ReflectionTestUtils.setField(controller, "jwtService", jwtService);
        ReflectionTestUtils.setField(controller, "authenticationManager", authenticationManager);
        ReflectionTestUtils.setField(controller, "testMessage", testMessage);
    }

    @Test
    void authenticateReturnsTokenAfterSuccessfulAuthentication() {
        AuthRequest request = new AuthRequest("jane", "correct-password");
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(jwtService.generateToken("jane")).thenReturn("test-jwt");

        Object response = controller.authGenerateToken(request).getBody();

        assertEquals("test-jwt", ((Map<?, ?>) response).get("token"));
        verify(jwtService).generateToken("jane");
    }

    @Test
    void authenticateDoesNotGenerateTokenWhenAuthenticationFails() {
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(false);

        assertThrows(UsernameNotFoundException.class,
                () -> controller.authGenerateToken(new AuthRequest("jane", "wrong-password")));
    }

    @Test
    void messageEndpointReturnsSuccessResponse() {
        SuccessResponse response = controller.greetings().getBody();

        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals("Success", response.getData());
    }
}

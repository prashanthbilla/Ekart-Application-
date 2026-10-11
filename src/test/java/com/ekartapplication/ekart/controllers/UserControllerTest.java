package com.ekartapplication.ekart.controllers;

import com.ekartapplication.ekart.dao.UserDAO;
import com.ekartapplication.ekart.exception.exceptions.InvalidUserDataException;
import com.ekartapplication.ekart.model.SuccessResponse;
import com.ekartapplication.ekart.model.User;
import com.ekartapplication.ekart.service.user.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private PasswordEncoder passwordEncoder;

    private UserController controller;

    @BeforeEach
    void setUp() {
        controller = new UserController();
        ReflectionTestUtils.setField(controller, "service", userService);
        ReflectionTestUtils.setField(controller, "passwordEncoder", passwordEncoder);
    }

    @Test
    void saveUserEncodesPasswordAndReturnsCreatedUserId() {
        User user = new User();
        user.setUserName("jane");
        user.setPassword("plain-password");
        when(passwordEncoder.encode("plain-password")).thenReturn("encoded-password");
        when(userService.createUser(user)).thenReturn("user-id");

        ResponseEntity<SuccessResponse> response = controller.saveUser(user);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("encoded-password", user.getPassword());
        assertEquals(Map.of("userId", "user-id"), response.getBody().getData());
        verify(userService).createUser(user);
    }

    @Test
    void saveUserRejectsNullUserOrBlankPasswordBeforeEncoding() {
        assertThrows(InvalidUserDataException.class, () -> controller.saveUser(null));

        User user = new User();
        assertThrows(InvalidUserDataException.class, () -> controller.saveUser(user));

        user.setPassword(" ");
        assertThrows(InvalidUserDataException.class, () -> controller.saveUser(user));

        verify(passwordEncoder, never()).encode(any());
        verify(userService, never()).createUser(any());
    }

    @Test
    void getUserReturnsServiceResultInSuccessEnvelope() {
        UserDAO user = new UserDAO();
        when(userService.getUser("jane")).thenReturn(user);

        ResponseEntity<SuccessResponse> response = controller.getUser("jane");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(Collections.singleton(user), response.getBody().getData());
    }

    @Test
    void updateUserReturnsUpdatedUserId() {
        User user = new User();
        when(userService.updateUser(user)).thenReturn("user-id");

        ResponseEntity<SuccessResponse> response = controller.updateUser(user);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(Map.of("userId", "user-id"), response.getBody().getData());
    }

    @Test
    void getUsersReturnsServiceResultsInSuccessEnvelope() {
        List<UserDAO> users = List.of(new UserDAO());
        when(userService.getUsers()).thenReturn(users);

        ResponseEntity<Object> response = controller.getUsers();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(Collections.singleton(users), ((SuccessResponse) response.getBody()).getData());
    }

    @Test
    void getRoleReturnsTheRequestedUsersRole() {
        UserDAO user = new UserDAO();
        user.setRole("ROLE_USER");
        when(userService.getUserByUserName("jane")).thenReturn(user);

        ResponseEntity<SuccessResponse> response = controller.getRole("jane");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("ROLE_USER", response.getBody().getData());
    }

    @Test
    void deleteUserReturnsServiceMessage() {
        when(userService.deleteUser("jane")).thenReturn("Deleted...");

        ResponseEntity<SuccessResponse> response = controller.deleteUser("jane");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(Map.of("message", "Deleted..."), response.getBody().getData());
    }
}

package com.ekartapplication.ekart.controllers;


import com.ekartapplication.ekart.dao.UserDAO;
import com.ekartapplication.ekart.model.SuccessResponse;
import com.ekartapplication.ekart.model.User;
import com.ekartapplication.ekart.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("user/v1")
public class UserController {

    @Autowired
    UserService service;

    @Autowired
    PasswordEncoder passwordEncoder;

    @PostMapping(value = "/post", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SuccessResponse> saveUser(@RequestBody User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        String userId = service.createUser(user);
        return new ResponseEntity<>(new SuccessResponse(HttpStatus.OK.value(), Collections.singletonMap("userId", userId)), HttpStatus.OK);
    }

    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAuthority('ROLE_USER') or hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<SuccessResponse> getUser(@RequestParam String userName) {
        UserDAO user = service.getUser(userName);
        return new ResponseEntity<>(new SuccessResponse(HttpStatus.OK.value(), Collections.singleton(user)), HttpStatus.OK);
    }

    @PutMapping(value = "", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAuthority('ROLE_USER') or hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<SuccessResponse> updateUser(@RequestBody User user) {
        String userId = service.updateUser(user);
        return new ResponseEntity<>(new SuccessResponse(HttpStatus.OK.value(), Collections.singletonMap("userId", userId)), HttpStatus.OK);
    }

    @GetMapping(value = "/all", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAuthority('ROLE_USER') or hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Object> getUsers() {
        List<UserDAO> users = service.getUsers();
        return new ResponseEntity<>(new SuccessResponse(HttpStatus.OK.value(), Collections.singleton(users)), HttpStatus.OK);
    }

    @GetMapping(value = "/getRole", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAuthority('ROLE_USER') or hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<SuccessResponse> getRole(String userName) {
        UserDAO userDAO = service.getUserByUserName(userName);
        return new ResponseEntity<>(new SuccessResponse(HttpStatus.OK.value(), userDAO.getRole()), HttpStatus.OK);
    }

    @DeleteMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAuthority('ROLE_USER') or hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<SuccessResponse> deleteUser(@RequestParam String userId) {
        String isDeleted = service.deleteUser(userId);
        return new ResponseEntity<>(new SuccessResponse(HttpStatus.OK.value(), Collections.singletonMap("message", isDeleted)), HttpStatus.OK);
    }

}

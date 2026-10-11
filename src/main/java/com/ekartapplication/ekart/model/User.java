package com.ekartapplication.ekart.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data

public class User {
    private String id;
    private String firstName;
    private String lastName;
    private String userName;
    private String password;
    private String emailId;
    private String mobileNum;
    private String houseNo;
    private String landmark;
    private String city;
    private String pincode;
    private String role;
}

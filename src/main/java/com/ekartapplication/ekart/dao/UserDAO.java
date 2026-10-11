package com.ekartapplication.ekart.dao;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserDAO {
    private String id;
    private String firstName;
    private String lastName;
    private String fullName;
    private String userName;
    private String password;
    private String emailId;
    private String mobileNum;
    private String houseNo;
    private String landmark;
    private String city;
    private String pincode;
    private String role;
    private Date createdDate;
    private Date updatedDate;
}

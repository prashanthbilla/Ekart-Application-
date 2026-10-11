package com.ekartapplication.ekart.utils.mapper;

import com.ekartapplication.ekart.dao.UserDAO;
import com.ekartapplication.ekart.model.User;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

@Component
public class UserModelToDao {
    public UserDAO mapperModelToDAO(User user) {
        UserDAO userDAO = new UserDAO();
        userDAO.setId(((user.getId() == null || user.getId().isEmpty()) ? UUID.randomUUID().toString() : user.getId()));
        userDAO.setFirstName(user.getFirstName());
        userDAO.setLastName(user.getLastName());
        userDAO.setFullName(user.getFirstName()+" "+user.getLastName());
        userDAO.setUserName(user.getUserName());
        userDAO.setPassword(user.getPassword());
        userDAO.setEmailId(user.getEmailId());
        userDAO.setMobileNum(user.getMobileNum());
        userDAO.setHouseNo(user.getHouseNo());
        userDAO.setLandmark(user.getLandmark());
        userDAO.setCity(user.getCity());
        userDAO.setPincode(user.getPincode());
        userDAO.setRole(user.getRole());
        userDAO.setCreatedDate(userDAO.getCreatedDate() == null ? new java.sql.Date(System.currentTimeMillis()) : userDAO.getCreatedDate());
        userDAO.setUpdatedDate(new Date(System.currentTimeMillis()));
        return userDAO;
    }
}

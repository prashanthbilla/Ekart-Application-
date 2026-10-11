package com.ekartapplication.ekart.service.user;

import com.ekartapplication.ekart.dao.UserDAO;
import com.ekartapplication.ekart.model.User;

import java.util.List;

public interface UserService {
    String createUser(User user);

    UserDAO getUser(String userId);

    String updateUser(User user);

    List<UserDAO> getUsers();

    String deleteUser(String userId);

    boolean isUserExistsByUserName(String userName);

    UserDAO getUserByUserName(String userName);

}

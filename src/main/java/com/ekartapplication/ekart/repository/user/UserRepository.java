package com.ekartapplication.ekart.repository.user;

import com.ekartapplication.ekart.dao.UserDAO;
import com.ekartapplication.ekart.model.User;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

public interface UserRepository {


    boolean saveUser(UserDAO user);

    UserDAO getUserById(String userId);

    int userCount(String userId);

    boolean updateUser(UserDAO user);

    List<UserDAO> getUsers();

    boolean deleteUser(String userId);

    int userNameCount(String userName);

    UserDAO getUserByUDANDPW(String username, String password);

    UserDAO getUserByUserName(String userName);

}

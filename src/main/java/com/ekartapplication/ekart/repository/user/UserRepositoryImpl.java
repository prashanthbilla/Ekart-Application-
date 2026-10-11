package com.ekartapplication.ekart.repository.user;

import com.ekartapplication.ekart.dao.UserDAO;
import com.ekartapplication.ekart.exception.repoexception.HandleRepoException;
import com.ekartapplication.ekart.utils.mapper.UserMapperRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class UserRepositoryImpl implements UserRepository {

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Override
    public boolean saveUser(UserDAO user) {
        try {
            String sql = "INSERT INTO user_info (id, first_name, last_name, full_name, user_name, " +
                    "paassword, email_id, mobile_num, house_num, landmark, city, pincode, user_role, created_at, updated_at) VALUES (\'"
                    + user.getId() + "\', \'" +
                    user.getFirstName() + "\', \'" +
                    user.getLastName() + "\', \'" +
                    user.getFullName() + "\', \'" +
                    user.getUserName() + "\', \'" +
                    user.getPassword() + "\', \'" +
                    user.getEmailId() + "\', \'" +
                    user.getMobileNum() + "\', \'" +
                    user.getHouseNo() + "\', \'" +
                    user.getLandmark() + "\', \'" +
                    user.getCity() + "\', \'" +
                    user.getPincode() + "\', \'" +
                    user.getRole() + "\', \'" +
                    user.getCreatedDate() + "\', \'" +
                    user.getUpdatedDate() + "\');";
            int response = jdbcTemplate.update(sql);
            return response == 1;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public UserDAO getUserById(String userId) {
        UserDAO user;
        try {
            String sql = "SELECT * FROM user_info WHERE user_name = \"" + userId
                    + "\";";
            user = jdbcTemplate.queryForObject(sql, new UserMapperRepo());
        } catch (Exception e) {
            return null;
        }
        return user;
    }

    @Override
    public int userCount(String userId) {
        try {
            String sql = "select count(full_name) from user_info where user_name = ?";
            return jdbcTemplate.queryForObject(
                    sql, Integer.class,
                    userId);
        } catch (Exception e) {
            return 0;
        }
    }

    @Override
    public boolean updateUser(UserDAO user) {
        try {
            String sql = "UPDATE user_info SET first_name = \'" + user.getFirstName() +
                    "\',last_name = \'" + user.getLastName() +
                    "\',full_name = \'" + user.getFullName() +
                    "\',user_name = \'" + user.getUserName() +
                    "\',paassword = \'" + user.getPassword() +
                    "\',email_id = \'" + user.getEmailId() +
                    "\',mobile_num = \'" + user.getMobileNum() +
                    "\',house_num = \'" + user.getHouseNo() +
                    "\',landmark = \'" + user.getLandmark() +
                    "\',city = \'" + user.getCity() +
                    "\',pincode = \'" + user.getPincode() +
                    "\',user_role = \'" + user.getRole() +
                    "\',created_at = \'" + user.getCreatedDate() +
                    "\',updated_at = \'" + user.getUpdatedDate() +
                    "\' where id = \'" + user.getId() + "\';";
            int response = jdbcTemplate.update(sql);
            return response == 1;
        } catch (Exception e) {
            return false;
        }
    }

    @HandleRepoException
    @Override
    public List<UserDAO> getUsers() {
        List<UserDAO> users;
            String sql = "SELECT * FROM user_info ";
            users = jdbcTemplate.query(sql, new UserMapperRepo());
            return users;
    }

    @Override
    public boolean deleteUser(String userId) {
        try {
            String sql = "DELETE from user_info where user_name = \"" + userId + "\"";
            int response = jdbcTemplate.update(sql);
            return response == 1;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public int userNameCount(String userName) {
        try {
            String sql = "select count(id) from user_info where user_name = ?";
            return jdbcTemplate.queryForObject(
                    sql, Integer.class,
                    userName);
        } catch (Exception e) {
            return 0;
        }
    }

    @Override
    public UserDAO getUserByUDANDPW(String username, String password) {
        UserDAO user;
        try {
            String sql = "SELECT * FROM user_info WHERE user_name = \"" + username
                    + "\" and paassword = \"" + password + "\"";
            user = jdbcTemplate.queryForObject(sql, new UserMapperRepo());
        } catch (Exception e) {
            return null;
        }
        return user;
    }

    @Override
    public UserDAO getUserByUserName(String userName) {
        UserDAO user;
        try {
            String sql = "SELECT * FROM user_info WHERE user_name = \"" + userName
                    + "\";";
            user = jdbcTemplate.queryForObject(sql, new UserMapperRepo());
        } catch (Exception e) {
            return null;
        }
        return user;
    }
}

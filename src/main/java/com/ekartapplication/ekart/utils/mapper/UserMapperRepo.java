package com.ekartapplication.ekart.utils.mapper;

import com.ekartapplication.ekart.dao.UserDAO;
import com.ekartapplication.ekart.model.User;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class UserMapperRepo implements RowMapper<UserDAO> {

    @Override
    public UserDAO mapRow(ResultSet rs, int rowNum) throws SQLException {
        UserDAO user = new UserDAO();
        user.setId(rs.getString("id"));
        user.setFirstName(rs.getString("first_name"));
        user.setLastName(rs.getString("last_name"));
        user.setFullName(rs.getString("full_name"));
        user.setUserName(rs.getString("user_name"));
        user.setPassword(rs.getString("paassword"));
        user.setEmailId(rs.getString("email_id"));
        user.setMobileNum(rs.getString("mobile_num"));
        user.setHouseNo(rs.getString("house_num"));
        user.setLandmark(rs.getString("landmark"));
        user.setCity(rs.getString("city"));
        user.setPincode(rs.getString("pincode"));
        user.setRole(rs.getString("user_role"));
        user.setCreatedDate(rs.getDate("created_at"));
        user.setUpdatedDate(rs.getDate("updated_at"));
        return user;
    }
}

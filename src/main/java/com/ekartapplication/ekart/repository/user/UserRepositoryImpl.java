package com.ekartapplication.ekart.repository.user;

import com.ekartapplication.ekart.dao.UserDAO;
import com.ekartapplication.ekart.exception.repoexception.HandleRepoException;
import com.ekartapplication.ekart.utils.mapper.UserMapperRepo;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class UserRepositoryImpl implements UserRepository {

    private static final UserMapperRepo USER_ROW_MAPPER = new UserMapperRepo();

    private final JdbcTemplate jdbcTemplate;

    public UserRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    @HandleRepoException
    public boolean saveUser(UserDAO user) {
        String sql = """
                INSERT INTO user_info (
                    id, first_name, last_name, full_name, user_name, paassword,
                    email_id, mobile_num, house_num, landmark, city, pincode,
                    user_role, created_at, updated_at
                ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;
        return jdbcTemplate.update(sql,
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getFullName(),
                user.getUserName(),
                user.getPassword(),
                user.getEmailId(),
                user.getMobileNum(),
                user.getHouseNo(),
                user.getLandmark(),
                user.getCity(),
                user.getPincode(),
                user.getRole(),
                user.getCreatedDate(),
                user.getUpdatedDate()) == 1;
    }

    @Override
    @HandleRepoException
    public UserDAO getUserById(String userId) {
        return findUser("SELECT * FROM user_info WHERE user_name = ?", userId);
    }

    @Override
    @HandleRepoException
    public int userCount(String userId) {
        String sql = "SELECT COUNT(*) FROM user_info WHERE user_name = ?";
        return jdbcTemplate.queryForObject(sql, Integer.class, userId);
    }

    @Override
    @HandleRepoException
    public boolean updateUser(UserDAO user) {
        String sql = """
                UPDATE user_info
                SET first_name = ?, last_name = ?, full_name = ?, user_name = ?,
                    paassword = ?, email_id = ?, mobile_num = ?, house_num = ?,
                    landmark = ?, city = ?, pincode = ?, user_role = ?,
                    created_at = ?, updated_at = ?
                WHERE id = ?
                """;
        return jdbcTemplate.update(sql,
                user.getFirstName(),
                user.getLastName(),
                user.getFullName(),
                user.getUserName(),
                user.getPassword(),
                user.getEmailId(),
                user.getMobileNum(),
                user.getHouseNo(),
                user.getLandmark(),
                user.getCity(),
                user.getPincode(),
                user.getRole(),
                user.getCreatedDate(),
                user.getUpdatedDate(),
                user.getId()) == 1;
    }

    @HandleRepoException
    @Override
    public List<UserDAO> getUsers() {
        return jdbcTemplate.query("SELECT * FROM user_info", USER_ROW_MAPPER);
    }

    @Override
    @HandleRepoException
    public boolean deleteUser(String userId) {
        String sql = "DELETE FROM user_info WHERE user_name = ?";
        return jdbcTemplate.update(sql, userId) == 1;
    }

    @Override
    @HandleRepoException
    public int userNameCount(String userName) {
        String sql = "SELECT COUNT(*) FROM user_info WHERE user_name = ?";
        return jdbcTemplate.queryForObject(sql, Integer.class, userName);
    }

    @Override
    @HandleRepoException
    public UserDAO getUserByUDANDPW(String username, String password) {
        String sql = "SELECT * FROM user_info WHERE user_name = ? AND paassword = ?";
        try {
            return jdbcTemplate.queryForObject(sql, USER_ROW_MAPPER, username, password);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    @HandleRepoException
    public UserDAO getUserByUserName(String userName) {
        return findUser("SELECT * FROM user_info WHERE user_name = ?", userName);
    }

    private UserDAO findUser(String sql, String userName) {
        try {
            return jdbcTemplate.queryForObject(sql, USER_ROW_MAPPER, userName);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }
}

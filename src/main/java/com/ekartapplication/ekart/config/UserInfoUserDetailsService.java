package com.ekartapplication.ekart.config;


import com.ekartapplication.ekart.dao.UserDAO;
import com.ekartapplication.ekart.repository.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
public class UserInfoUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository repository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        UserDAO userInfo = repository.getUserByUserName(username);

        if (userInfo == null) {
            throw new UsernameNotFoundException(username);
        }
        return new UserInfoUserDetails(userInfo);
    }
}
package com.ang.cafe.JWT;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.ang.cafe.dao.UserDao;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class CustomerUserDetailService implements UserDetailsService {

    @Autowired
    private UserDao userDao;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("Inside loadUserByUsername {}", username);
        com.ang.cafe.POJO.User userDetail = userDao.findByEmailId(username);  
        log.info("User is here: {}", userDetail);

        if (userDetail != null) {
            return new User(userDetail.getEmail(), userDetail.getPassword(), new ArrayList<>());
        } else {
            throw new UsernameNotFoundException("User not found!");
        }
    }

    public com.ang.cafe.POJO.User getUserDetail(String email) {
        log.info("Fetching user details for email: {}", email);
        return userDao.findByEmailId(email);  
    }
}

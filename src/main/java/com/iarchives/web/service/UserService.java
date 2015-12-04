package com.iarchives.web.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetailsService;

import com.iarchives.web.domain.User;

public interface UserService extends UserDetailsService {

    User registerVisit(User user);
    
    Page<User> getAllRegisteredUsers(final Pageable pageable);

    void save(User user);

    User loadUserById(Long id);
}

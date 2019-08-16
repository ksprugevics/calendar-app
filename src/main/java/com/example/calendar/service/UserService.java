package com.example.calendar.service;

import com.example.calendar.models.User;

public interface UserService {
    void save(User user);

    User findByUsername(String username);
}
package com.toki.repository;

import com.toki.model.User;

public interface UserRepositoryInterface {
    void save(User user);
    User findByUsername(String username);
}
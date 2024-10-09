package com.huiun.fizzybudget.userservice.service;

import com.huiun.fizzybudget.common.entity.User;

import java.util.Optional;

public interface UserService {

    Optional<User> findUserByUserId(Long userId);

    Optional<User> findUserByUsername(String username);

    Optional<User> findUserByEmail(String email);

    void addRoleToUser(Long userId, Long roleId);

    void removeRoleFromUser(Long userId, Long roleId);

    User createUser(User user);
}

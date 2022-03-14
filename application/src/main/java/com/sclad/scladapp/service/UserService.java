package com.sclad.scladapp.service;

import org.springframework.http.ResponseEntity;

import com.sclad.scladapp.entity.User;
import com.sclad.scladapp.model.UserModel;

public interface UserService {

    Long register(UserModel user);

    User getUserByUsername(String username);

    ResponseEntity<String> deleteUser(User user);

    User updateUser(User updatedUser);
}

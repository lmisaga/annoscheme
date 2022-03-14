package com.sclad.scladapp.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.sclad.scladapp.entity.Authority;
import com.sclad.scladapp.entity.User;
import com.sclad.scladapp.exceptions.UserNotFoundException;
import com.sclad.scladapp.model.UserModel;
import com.sclad.scladapp.repository.AuthorityRepository;
import com.sclad.scladapp.repository.UserRepository;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final AuthorityRepository authorityRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Autowired
    public UserServiceImpl(UserRepository userRepository, AuthorityRepository authorityRepository) {
        this.userRepository = userRepository;
        this.authorityRepository = authorityRepository;
    }

    @Override
    public Long register(UserModel userModel) {
        User user = new User();
        user.setUsername(userModel.getUsername());
        if (!userModel.getPassword().equals(userModel.getPasswordConfirm())) {
            throw new UsernameNotFoundException("Error in password confirm");
        } else {
            user.setPassword(passwordEncoder.encode(userModel.getPassword()));
        }
        user.setEmail(userModel.getEmail());
        //set "admin" role if username contains "admin," otherwise set user as "user" role
        Authority role = new Authority();
        role.setUsername(userModel.getUsername());
        role.setAuthority(userModel.getUsername().contains("admin") ? "ROLE_ADMIN" : "ROLE_USER");
        userRepository.save(user);
        authorityRepository.save(role);
        return user.getId();
    }

    @Override
    public User getUserByUsername(String username) {
        User user = userRepository.findByUsername(username).orElse(null);
        if (user != null) {
            return user;
        } else {
            throw new UsernameNotFoundException("Username not found.");
        }
    }

    @Override
    public ResponseEntity<String> deleteUser(User user) {
        if (user.getId() != null && userRepository.findById(user.getId()).isPresent()) {
            userRepository.delete(user);
            return new ResponseEntity<>("User removed.", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("User not found.", HttpStatus.NOT_FOUND);
        }
    }

    @Override
    public User updateUser(User updatedUser) {
        User user = userRepository.findById(updatedUser.getId()).orElse(null);
        if (user != null && updatedUser.getUsername().equals(user.getUsername())) {
            updatedUser.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
            return userRepository.save(updatedUser);
        } else {
            try {
                throw new UserNotFoundException("Could not update user - no user was found matching the provided model.");
            } catch (UserNotFoundException e) {
                return null;
            }
        }
    }
}

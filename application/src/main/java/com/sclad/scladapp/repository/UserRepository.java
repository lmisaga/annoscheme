package com.sclad.scladapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

import com.sclad.scladapp.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);
}

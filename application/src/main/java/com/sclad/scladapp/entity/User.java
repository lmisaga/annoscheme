package com.sclad.scladapp.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.Email;

@Table(name = "users")
@Entity
public class User extends AbstractEntity {

    @Column(unique = true)
    private String username;

    @Column
    private String password;

    @Transient
    private String passwordConfirm; //will not be serialized

    @Column
    @Email(message = "Email should be valid: <name>@sClad.sk")
    private String email;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasswordConfirm() {
        return passwordConfirm;
    }

    public void setPasswordConfirm(String passwordConfirm) {
        this.passwordConfirm = passwordConfirm;
    }
}

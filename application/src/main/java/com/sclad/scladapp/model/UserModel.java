package com.sclad.scladapp.model;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

public class UserModel extends AbstractModel {

    @Email(message = "Email should be valid: <name>@sClad.sk")
    private String email;

    @NotBlank(message = "Username is mandatory.")
    private String username;

    @NotBlank(message = "Please provide password.")
    private String password;

    @NotBlank(message = "Provide matching passwords.")
    private String passwordConfirm;


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

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

    public String getPasswordConfirm() {
        return passwordConfirm;
    }

    public void setPasswordConfirm(String passwordConfirm) {
        this.passwordConfirm = passwordConfirm;
    }
}

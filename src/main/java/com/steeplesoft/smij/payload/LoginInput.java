package com.steeplesoft.smij.payload;

import jakarta.validation.constraints.NotEmpty;

public class LoginInput {
    @NotEmpty
    public String userName;
    @NotEmpty
    public String password;

    public LoginInput() {
    }

    public LoginInput(String userName) {
        this.userName = userName;
    }

    public LoginInput(String userName, String password) {
        this.userName = userName;
        this.password = password;
    }
}

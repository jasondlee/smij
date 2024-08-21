package com.steeplesoft.simplesec.app.payload;

public class LoginInput {
    public String userName;
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

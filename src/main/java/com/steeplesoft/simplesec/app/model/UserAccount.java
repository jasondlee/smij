package com.steeplesoft.simplesec.app.model;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Entity;

import java.time.LocalDateTime;

@Entity
public class UserAccount extends PanacheEntity {
    public String userName;
    public String password;
    public String phoneNumber;
    public String address1;
    public String address2;
    public String city;
    public String state;
    public String zipCode;

    public LocalDateTime creationDate = LocalDateTime.now();

    public UserAccount() {
    }

    public UserAccount(String userName, String password) {
        this.userName = userName;
        this.password = password;
    }
}

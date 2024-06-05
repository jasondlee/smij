package com.steeplesoft.simplesec.app.model;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name="user_account")
public class User extends PanacheEntity {
    public String userName;
    public String password;
    public String phoneNumber;
    public String address1;
    public String address2;
    public String city;
    public String state;
    public String zipCode;
    public String roles; // Comma-delimited list

    public LocalDateTime creationDate = LocalDateTime.now();

    public User() {
    }

    public User(String userName, String password) {
        this.userName = userName;
        this.password = password;
    }
}

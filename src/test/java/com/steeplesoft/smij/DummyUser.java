package com.steeplesoft.smij;

import java.time.OffsetDateTime;

import com.steeplesoft.smij.user.UserFacade;

public class DummyUser implements UserFacade {
    private Long id;
    private Long tenantId = 1L;
    private String userName;
    private String password;
    private String roles;
    private int failAttempts;
    private OffsetDateTime lockedUntil;

    public DummyUser(String userName, String password, String roles) {
        this.userName = userName;
        this.password = password;
        this.roles = roles;
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public Long getTenantId() {
        return tenantId;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    @Override
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String getRoles() {
        return roles;
    }

    public void setRoles(String roles) {
        this.roles = roles;
    }

    @Override
    public int getFailAttempts() {
        return failAttempts;
    }

    public void setFailAttempts(int failAttempts) {
        this.failAttempts = failAttempts;
    }

    @Override
    public OffsetDateTime getLockedUntil() {
        return lockedUntil;
    }

    public void setLockedUntil(OffsetDateTime lockedUntil) {
        this.lockedUntil = lockedUntil;
    }
}

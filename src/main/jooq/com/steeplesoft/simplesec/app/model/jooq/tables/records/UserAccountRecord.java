/*
 * This file is generated by jOOQ.
 */
package com.steeplesoft.simplesec.app.model.jooq.tables.records;


import com.steeplesoft.simplesec.app.model.jooq.tables.UserAccount;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Size;

import java.time.OffsetDateTime;

import javax.annotation.processing.Generated;

import org.jooq.Record1;
import org.jooq.impl.UpdatableRecordImpl;


/**
 * This class is generated by jOOQ.
 */
@Generated(
    value = {
        "https://www.jooq.org",
        "jOOQ version:3.19.11"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes", "this-escape" })
public class UserAccountRecord extends UpdatableRecordImpl<UserAccountRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * Setter for <code>public.user_account.tenant_id</code>.
     */
    public UserAccountRecord setTenantId(@Nullable Long value) {
        set(0, value);
        return this;
    }

    /**
     * Getter for <code>public.user_account.tenant_id</code>.
     */
    @Nullable
    public Long getTenantId() {
        return (Long) get(0);
    }

    /**
     * Setter for <code>public.user_account.id</code>.
     */
    public UserAccountRecord setId(@Nullable Long value) {
        set(1, value);
        return this;
    }

    /**
     * Getter for <code>public.user_account.id</code>.
     */
    @Nullable
    public Long getId() {
        return (Long) get(1);
    }

    /**
     * Setter for <code>public.user_account.user_name</code>.
     */
    public UserAccountRecord setUserName(@Nullable String value) {
        set(2, value);
        return this;
    }

    /**
     * Getter for <code>public.user_account.user_name</code>.
     */
    @Size(max = 255)
    @Nullable
    public String getUserName() {
        return (String) get(2);
    }

    /**
     * Setter for <code>public.user_account.password</code>.
     */
    public UserAccountRecord setPassword(@Nullable String value) {
        set(3, value);
        return this;
    }

    /**
     * Getter for <code>public.user_account.password</code>.
     */
    @Size(max = 255)
    @Nullable
    public String getPassword() {
        return (String) get(3);
    }

    /**
     * Setter for <code>public.user_account.phone_number</code>.
     */
    public UserAccountRecord setPhoneNumber(@Nullable String value) {
        set(4, value);
        return this;
    }

    /**
     * Getter for <code>public.user_account.phone_number</code>.
     */
    @Size(max = 50)
    @Nullable
    public String getPhoneNumber() {
        return (String) get(4);
    }

    /**
     * Setter for <code>public.user_account.address1</code>.
     */
    public UserAccountRecord setAddress1(@Nullable String value) {
        set(5, value);
        return this;
    }

    /**
     * Getter for <code>public.user_account.address1</code>.
     */
    @Size(max = 100)
    @Nullable
    public String getAddress1() {
        return (String) get(5);
    }

    /**
     * Setter for <code>public.user_account.address2</code>.
     */
    public UserAccountRecord setAddress2(@Nullable String value) {
        set(6, value);
        return this;
    }

    /**
     * Getter for <code>public.user_account.address2</code>.
     */
    @Size(max = 100)
    @Nullable
    public String getAddress2() {
        return (String) get(6);
    }

    /**
     * Setter for <code>public.user_account.city</code>.
     */
    public UserAccountRecord setCity(@Nullable String value) {
        set(7, value);
        return this;
    }

    /**
     * Getter for <code>public.user_account.city</code>.
     */
    @Size(max = 100)
    @Nullable
    public String getCity() {
        return (String) get(7);
    }

    /**
     * Setter for <code>public.user_account.state</code>.
     */
    public UserAccountRecord setState(@Nullable String value) {
        set(8, value);
        return this;
    }

    /**
     * Getter for <code>public.user_account.state</code>.
     */
    @Size(max = 10)
    @Nullable
    public String getState() {
        return (String) get(8);
    }

    /**
     * Setter for <code>public.user_account.zip_code</code>.
     */
    public UserAccountRecord setZipCode(@Nullable String value) {
        set(9, value);
        return this;
    }

    /**
     * Getter for <code>public.user_account.zip_code</code>.
     */
    @Size(max = 10)
    @Nullable
    public String getZipCode() {
        return (String) get(9);
    }

    /**
     * Setter for <code>public.user_account.roles</code>.
     */
    public UserAccountRecord setRoles(@Nullable String value) {
        set(10, value);
        return this;
    }

    /**
     * Getter for <code>public.user_account.roles</code>.
     */
    @Size(max = 1000)
    @Nullable
    public String getRoles() {
        return (String) get(10);
    }

    /**
     * Setter for <code>public.user_account.creation_date</code>.
     */
    public UserAccountRecord setCreationDate(@Nullable OffsetDateTime value) {
        set(11, value);
        return this;
    }

    /**
     * Getter for <code>public.user_account.creation_date</code>.
     */
    @Nullable
    public OffsetDateTime getCreationDate() {
        return (OffsetDateTime) get(11);
    }

    /**
     * Setter for <code>public.user_account.fail_attempts</code>.
     */
    public UserAccountRecord setFailAttempts(@Nullable Integer value) {
        set(12, value);
        return this;
    }

    /**
     * Getter for <code>public.user_account.fail_attempts</code>.
     */
    @Nullable
    public Integer getFailAttempts() {
        return (Integer) get(12);
    }

    /**
     * Setter for <code>public.user_account.locked_until</code>.
     */
    public UserAccountRecord setLockedUntil(@Nullable Long value) {
        set(13, value);
        return this;
    }

    /**
     * Getter for <code>public.user_account.locked_until</code>.
     */
    @Nullable
    public Long getLockedUntil() {
        return (Long) get(13);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    @Override
    @Nonnull
    public Record1<Long> key() {
        return (Record1) super.key();
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached UserAccountRecord
     */
    public UserAccountRecord() {
        super(UserAccount.USER_ACCOUNT);
    }

    /**
     * Create a detached, initialised UserAccountRecord
     */
    public UserAccountRecord(@Nullable Long tenantId, @Nullable Long id, @Nullable String userName, @Nullable String password, @Nullable String phoneNumber, @Nullable String address1, @Nullable String address2, @Nullable String city, @Nullable String state, @Nullable String zipCode, @Nullable String roles, @Nullable OffsetDateTime creationDate, @Nullable Integer failAttempts, @Nullable Long lockedUntil) {
        super(UserAccount.USER_ACCOUNT);

        setTenantId(tenantId);
        setId(id);
        setUserName(userName);
        setPassword(password);
        setPhoneNumber(phoneNumber);
        setAddress1(address1);
        setAddress2(address2);
        setCity(city);
        setState(state);
        setZipCode(zipCode);
        setRoles(roles);
        setCreationDate(creationDate);
        setFailAttempts(failAttempts);
        setLockedUntil(lockedUntil);
        resetChangedOnNotNull();
    }

    /**
     * Create a detached, initialised UserAccountRecord
     */
    public UserAccountRecord(com.steeplesoft.simplesec.app.model.jooq.tables.pojos.UserAccount value) {
        super(UserAccount.USER_ACCOUNT);

        if (value != null) {
            setTenantId(value.getTenantId());
            setId(value.getId());
            setUserName(value.getUserName());
            setPassword(value.getPassword());
            setPhoneNumber(value.getPhoneNumber());
            setAddress1(value.getAddress1());
            setAddress2(value.getAddress2());
            setCity(value.getCity());
            setState(value.getState());
            setZipCode(value.getZipCode());
            setRoles(value.getRoles());
            setCreationDate(value.getCreationDate());
            setFailAttempts(value.getFailAttempts());
            setLockedUntil(value.getLockedUntil());
            resetChangedOnNotNull();
        }
    }
}

/*
 * This file is generated by jOOQ.
 */
package com.steeplesoft.simplesec.app.model.jooq.tables;


import com.steeplesoft.simplesec.app.model.jooq.Keys;
import com.steeplesoft.simplesec.app.model.jooq.Public;
import com.steeplesoft.simplesec.app.model.jooq.tables.records.UserAccountRecord;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import javax.annotation.processing.Generated;

import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Function13;
import org.jooq.Identity;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.Records;
import org.jooq.Row13;
import org.jooq.Schema;
import org.jooq.SelectField;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.TableOptions;
import org.jooq.UniqueKey;
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;
import org.jooq.impl.TableImpl;


/**
 * This class is generated by jOOQ.
 */
@Generated(
    value = {
        "https://www.jooq.org",
        "jOOQ version:3.18.6"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class UserAccount extends TableImpl<UserAccountRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>public.user_account</code>
     */
    public static final UserAccount USER_ACCOUNT = new UserAccount();

    /**
     * The class holding records for this type
     */
    @Override
    @Nonnull
    public Class<UserAccountRecord> getRecordType() {
        return UserAccountRecord.class;
    }

    /**
     * The column <code>public.user_account.id</code>.
     */
    public final TableField<UserAccountRecord, Long> ID = createField(DSL.name("id"), SQLDataType.BIGINT.nullable(false).identity(true), this, "");

    /**
     * The column <code>public.user_account.user_name</code>.
     */
    public final TableField<UserAccountRecord, String> USER_NAME = createField(DSL.name("user_name"), SQLDataType.VARCHAR(255), this, "");

    /**
     * The column <code>public.user_account.password</code>.
     */
    public final TableField<UserAccountRecord, String> PASSWORD = createField(DSL.name("password"), SQLDataType.VARCHAR(255), this, "");

    /**
     * The column <code>public.user_account.phone_number</code>.
     */
    public final TableField<UserAccountRecord, String> PHONE_NUMBER = createField(DSL.name("phone_number"), SQLDataType.VARCHAR(50), this, "");

    /**
     * The column <code>public.user_account.address1</code>.
     */
    public final TableField<UserAccountRecord, String> ADDRESS1 = createField(DSL.name("address1"), SQLDataType.VARCHAR(100), this, "");

    /**
     * The column <code>public.user_account.address2</code>.
     */
    public final TableField<UserAccountRecord, String> ADDRESS2 = createField(DSL.name("address2"), SQLDataType.VARCHAR(100), this, "");

    /**
     * The column <code>public.user_account.city</code>.
     */
    public final TableField<UserAccountRecord, String> CITY = createField(DSL.name("city"), SQLDataType.VARCHAR(100), this, "");

    /**
     * The column <code>public.user_account.state</code>.
     */
    public final TableField<UserAccountRecord, String> STATE = createField(DSL.name("state"), SQLDataType.VARCHAR(10), this, "");

    /**
     * The column <code>public.user_account.zip_code</code>.
     */
    public final TableField<UserAccountRecord, String> ZIP_CODE = createField(DSL.name("zip_code"), SQLDataType.VARCHAR(10), this, "");

    /**
     * The column <code>public.user_account.roles</code>.
     */
    public final TableField<UserAccountRecord, String> ROLES = createField(DSL.name("roles"), SQLDataType.VARCHAR(1000), this, "");

    /**
     * The column <code>public.user_account.creation_date</code>.
     */
    public final TableField<UserAccountRecord, OffsetDateTime> CREATION_DATE = createField(DSL.name("creation_date"), SQLDataType.TIMESTAMPWITHTIMEZONE(6).defaultValue(DSL.field(DSL.raw("now()"), SQLDataType.TIMESTAMPWITHTIMEZONE)), this, "");

    /**
     * The column <code>public.user_account.fail_attempts</code>.
     */
    public final TableField<UserAccountRecord, Integer> FAIL_ATTEMPTS = createField(DSL.name("fail_attempts"), SQLDataType.INTEGER.defaultValue(DSL.field(DSL.raw("0"), SQLDataType.INTEGER)), this, "");

    /**
     * The column <code>public.user_account.locked_until</code>.
     */
    public final TableField<UserAccountRecord, Long> LOCKED_UNTIL = createField(DSL.name("locked_until"), SQLDataType.BIGINT, this, "");

    private UserAccount(Name alias, Table<UserAccountRecord> aliased) {
        this(alias, aliased, null);
    }

    private UserAccount(Name alias, Table<UserAccountRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.table());
    }

    /**
     * Create an aliased <code>public.user_account</code> table reference
     */
    public UserAccount(String alias) {
        this(DSL.name(alias), USER_ACCOUNT);
    }

    /**
     * Create an aliased <code>public.user_account</code> table reference
     */
    public UserAccount(Name alias) {
        this(alias, USER_ACCOUNT);
    }

    /**
     * Create a <code>public.user_account</code> table reference
     */
    public UserAccount() {
        this(DSL.name("user_account"), null);
    }

    public <O extends Record> UserAccount(Table<O> child, ForeignKey<O, UserAccountRecord> key) {
        super(child, key, USER_ACCOUNT);
    }

    @Override
    @Nullable
    public Schema getSchema() {
        return aliased() ? null : Public.PUBLIC;
    }

    @Override
    @Nonnull
    public Identity<UserAccountRecord, Long> getIdentity() {
        return (Identity<UserAccountRecord, Long>) super.getIdentity();
    }

    @Override
    @Nonnull
    public UniqueKey<UserAccountRecord> getPrimaryKey() {
        return Keys.USER_ACCOUNT_PKEY;
    }

    @Override
    @Nonnull
    public List<UniqueKey<UserAccountRecord>> getUniqueKeys() {
        return Arrays.asList(Keys.USER_ACCOUNT_USER_NAME_KEY);
    }

    @Override
    @Nonnull
    public UserAccount as(String alias) {
        return new UserAccount(DSL.name(alias), this);
    }

    @Override
    @Nonnull
    public UserAccount as(Name alias) {
        return new UserAccount(alias, this);
    }

    @Override
    @Nonnull
    public UserAccount as(Table<?> alias) {
        return new UserAccount(alias.getQualifiedName(), this);
    }

    /**
     * Rename this table
     */
    @Override
    @Nonnull
    public UserAccount rename(String name) {
        return new UserAccount(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    @Nonnull
    public UserAccount rename(Name name) {
        return new UserAccount(name, null);
    }

    /**
     * Rename this table
     */
    @Override
    @Nonnull
    public UserAccount rename(Table<?> name) {
        return new UserAccount(name.getQualifiedName(), null);
    }

    // -------------------------------------------------------------------------
    // Row13 type methods
    // -------------------------------------------------------------------------

    @Override
    @Nonnull
    public Row13<Long, String, String, String, String, String, String, String, String, String, OffsetDateTime, Integer, Long> fieldsRow() {
        return (Row13) super.fieldsRow();
    }

    /**
     * Convenience mapping calling {@link SelectField#convertFrom(Function)}.
     */
    public <U> SelectField<U> mapping(Function13<? super Long, ? super String, ? super String, ? super String, ? super String, ? super String, ? super String, ? super String, ? super String, ? super String, ? super OffsetDateTime, ? super Integer, ? super Long, ? extends U> from) {
        return convertFrom(Records.mapping(from));
    }

    /**
     * Convenience mapping calling {@link SelectField#convertFrom(Class,
     * Function)}.
     */
    public <U> SelectField<U> mapping(Class<U> toType, Function13<? super Long, ? super String, ? super String, ? super String, ? super String, ? super String, ? super String, ? super String, ? super String, ? super String, ? super OffsetDateTime, ? super Integer, ? super Long, ? extends U> from) {
        return convertFrom(toType, Records.mapping(from));
    }
}
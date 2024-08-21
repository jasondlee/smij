/*
 * This file is generated by jOOQ.
 */
package com.steeplesoft.simplesec.app.model.jooq;


import com.steeplesoft.simplesec.app.model.jooq.tables.FlywaySchemaHistory;
import com.steeplesoft.simplesec.app.model.jooq.tables.JwtMetadata;
import com.steeplesoft.simplesec.app.model.jooq.tables.PasswordRecovery;
import com.steeplesoft.simplesec.app.model.jooq.tables.UserAccount;
import com.steeplesoft.simplesec.app.model.jooq.tables.records.FlywaySchemaHistoryRecord;
import com.steeplesoft.simplesec.app.model.jooq.tables.records.JwtMetadataRecord;
import com.steeplesoft.simplesec.app.model.jooq.tables.records.PasswordRecoveryRecord;
import com.steeplesoft.simplesec.app.model.jooq.tables.records.UserAccountRecord;

import javax.annotation.processing.Generated;

import org.jooq.TableField;
import org.jooq.UniqueKey;
import org.jooq.impl.DSL;
import org.jooq.impl.Internal;


/**
 * A class modelling foreign key relationships and constraints of tables in
 * public.
 */
@Generated(
    value = {
        "https://www.jooq.org",
        "jOOQ version:3.18.6"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Keys {

    // -------------------------------------------------------------------------
    // UNIQUE and PRIMARY KEY definitions
    // -------------------------------------------------------------------------

    public static final UniqueKey<FlywaySchemaHistoryRecord> FLYWAY_SCHEMA_HISTORY_PK = Internal.createUniqueKey(FlywaySchemaHistory.FLYWAY_SCHEMA_HISTORY, DSL.name("flyway_schema_history_pk"), new TableField[] { FlywaySchemaHistory.FLYWAY_SCHEMA_HISTORY.INSTALLED_RANK }, true);
    public static final UniqueKey<JwtMetadataRecord> JWT_METADATA_PKEY = Internal.createUniqueKey(JwtMetadata.JWT_METADATA, DSL.name("jwt_metadata_pkey"), new TableField[] { JwtMetadata.JWT_METADATA.ID }, true);
    public static final UniqueKey<PasswordRecoveryRecord> PASSWORD_RECOVERY_PKEY = Internal.createUniqueKey(PasswordRecovery.PASSWORD_RECOVERY, DSL.name("password_recovery_pkey"), new TableField[] { PasswordRecovery.PASSWORD_RECOVERY.ID }, true);
    public static final UniqueKey<UserAccountRecord> USER_ACCOUNT_PKEY = Internal.createUniqueKey(UserAccount.USER_ACCOUNT, DSL.name("user_account_pkey"), new TableField[] { UserAccount.USER_ACCOUNT.ID }, true);
    public static final UniqueKey<UserAccountRecord> USER_ACCOUNT_USER_NAME_KEY = Internal.createUniqueKey(UserAccount.USER_ACCOUNT, DSL.name("user_account_user_name_key"), new TableField[] { UserAccount.USER_ACCOUNT.USER_NAME }, true);
}

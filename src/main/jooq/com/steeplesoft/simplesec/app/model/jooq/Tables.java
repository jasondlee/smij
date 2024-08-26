/*
 * This file is generated by jOOQ.
 */
package com.steeplesoft.simplesec.app.model.jooq;


import com.steeplesoft.simplesec.app.model.jooq.tables.FlywaySchemaHistory;
import com.steeplesoft.simplesec.app.model.jooq.tables.JwtMetadata;
import com.steeplesoft.simplesec.app.model.jooq.tables.PasswordRecovery;

import javax.annotation.processing.Generated;


/**
 * Convenience access to all tables in public.
 */
@Generated(
    value = {
        "https://www.jooq.org",
        "jOOQ version:3.19.11"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes", "this-escape" })
public class Tables {

    /**
     * The table <code>public.flyway_schema_history</code>.
     */
    public static final FlywaySchemaHistory FLYWAY_SCHEMA_HISTORY = FlywaySchemaHistory.FLYWAY_SCHEMA_HISTORY;

    /**
     * The table <code>public.jwt_metadata</code>.
     */
    public static final JwtMetadata JWT_METADATA = JwtMetadata.JWT_METADATA;

    /**
     * The table <code>public.password_recovery</code>.
     */
    public static final PasswordRecovery PASSWORD_RECOVERY = PasswordRecovery.PASSWORD_RECOVERY;
}

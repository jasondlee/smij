package com.steeplesoft.simplesec.app;

import com.steeplesoft.simplesec.app.model.jooq.tables.daos.JwtMetadataDao;
import com.steeplesoft.simplesec.app.model.jooq.tables.daos.PasswordRecoveryDao;
import com.steeplesoft.simplesec.app.model.jooq.tables.daos.UserAccountDao;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import org.jooq.Configuration;

public class DaoProducer {

    @ApplicationScoped
    @Produces
    public UserAccountDao userAccountDao(Configuration configuration) {
        return new UserAccountDao(configuration);
    }

    @ApplicationScoped
    @Produces
    public PasswordRecoveryDao passwordRecoveryDao(Configuration configuration) {
        return new PasswordRecoveryDao(configuration);
    }

    @ApplicationScoped
    @Produces
    public JwtMetadataDao jwtMetadataDao(Configuration configuration) {
        return new JwtMetadataDao(configuration);
    }
}

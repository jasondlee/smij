package com.steeplesoft.simplesec.app.dao;

import static com.steeplesoft.simplesec.app.model.jooq.Tables.PASSWORD_RECOVERY;

import java.time.OffsetDateTime;
import java.util.Optional;

import com.steeplesoft.simplesec.app.model.jooq.tables.pojos.PasswordRecovery;
import com.steeplesoft.simplesec.app.model.jooq.tables.records.PasswordRecoveryRecord;
import jakarta.enterprise.context.RequestScoped;

@RequestScoped
public class PasswordRecoveryDao extends AbstractDao<PasswordRecoveryRecord, PasswordRecovery, Long> {


    public PasswordRecoveryDao() {
        super(PASSWORD_RECOVERY, PasswordRecovery.class);
    }

    @Override
    public Long getId(PasswordRecovery object) {
        return object.getId();
    }

    public void addRecoveryToken(String emailAddress, String recoveryCode, OffsetDateTime expirationDate) {
        deleteCodesByUserName(emailAddress);

        PasswordRecovery pr = new PasswordRecovery();
        pr.setUserName(emailAddress);
        pr.setRecoveryToken(recoveryCode);
        pr.setExpiryDate(expirationDate);

        insert(pr);
    }

    public void deleteCodesByUserName(String userName) {
        ctx().deleteFrom(PASSWORD_RECOVERY)
                .where(PASSWORD_RECOVERY.USER_NAME.eq(userName))
                .execute();
    }

    public Optional<PasswordRecovery> fetchByUserName(String emailAddress) {
        return fetchOptional(PASSWORD_RECOVERY.USER_NAME, emailAddress);
    }
}

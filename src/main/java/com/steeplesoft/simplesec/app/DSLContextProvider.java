package com.steeplesoft.simplesec.app;

import javax.sql.DataSource;

import jakarta.enterprise.inject.spi.CDI;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.RenderNameCase;
import org.jooq.conf.RenderQuotedNames;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;
import org.jooq.impl.DefaultConfiguration;

public class DSLContextProvider {
    private static DSLContext context;

    public static synchronized DSLContext getContext() {
        if (context == null) {
            DataSource dataSource = CDI.current().select(DataSource.class).get();
            context = DSL.using(new DefaultConfiguration()
                    .set(dataSource)
                    .set(SQLDialect.POSTGRES)
                    .set(
                            new Settings()
                                    .withExecuteLogging(true)
                                    .withRenderCatalog(false)
                                    .withRenderSchema(false)
                                    .withRenderQuotedNames(RenderQuotedNames.NEVER)
                                    .withRenderNameCase(RenderNameCase.LOWER_IF_UNQUOTED)
                    )
            );
        }

        return context;
    }
}

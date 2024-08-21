package com.steeplesoft.simplesec.app;

import javax.sql.DataSource;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.enterprise.inject.spi.CDI;
import org.jooq.Configuration;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.RenderNameCase;
import org.jooq.conf.RenderQuotedNames;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;
import org.jooq.impl.DefaultConfiguration;

public class DSLContextProvider {
    private static DSLContext context;

    @SuppressWarnings("CdiInjectionPointsInspection")
    @Produces
    @ApplicationScoped
    public Configuration getConfiguration(DataSource dataSource) {
        return new DefaultConfiguration()
                .set(dataSource)
                .set(SQLDialect.POSTGRES)
                .set(
                        new Settings()
                                .withExecuteLogging(true)
                                .withRenderCatalog(false)
                                .withRenderSchema(false)
                                .withRenderQuotedNames(RenderQuotedNames.NEVER)
                                .withRenderNameCase(RenderNameCase.LOWER_IF_UNQUOTED)
                                .withRenderFormatted(true)
                );
    }

    public static synchronized DSLContext getContext() {
        if (context == null) {
            context = DSL.using(CDI.current().select(Configuration.class).get());
        }

        return context;
    }
}

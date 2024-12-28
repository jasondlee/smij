package com.steeplesoft.smij.app;

import javax.sql.DataSource;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import org.jooq.Configuration;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.RenderNameCase;
import org.jooq.conf.RenderQuotedNames;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;
import org.jooq.impl.DefaultConfiguration;

public class DSLContextProvider {
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

    @Produces
    @ApplicationScoped
    public DSLContext getContext(Configuration configuration) {
        return DSL.using(configuration);
    }
}

package com.steeplesoft.simplesec.app.dao;

import jakarta.annotation.PostConstruct;
import jakarta.inject.Inject;
import org.jooq.Configuration;
import org.jooq.Table;
import org.jooq.UpdatableRecord;
import org.jooq.impl.DAOImpl;

public abstract class AbstractDao<R extends UpdatableRecord<R>, P, T>
        extends DAOImpl<R, P, T> {
    @Inject
    protected Configuration configuration;

    protected AbstractDao(Table<R> table, Class<P> type) {
        super(table, type);
    }

    @PostConstruct
    public void init() {
        setConfiguration(configuration);
    }

}

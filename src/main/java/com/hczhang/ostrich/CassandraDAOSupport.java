package com.hczhang.ostrich;

import com.datastax.driver.core.Session;

/**
 * Created by steven on 4/14/14.
 */
public abstract class CassandraDAOSupport {

    private CassandraSessionFactory sessionFactory;

    private CassandraTemplate template;

    public void setSessionFactory(CassandraSessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
        template = createCassandraTemplate(sessionFactory.getSession());
    }

    public final Session getSession() {
        return (this.sessionFactory != null ? this.sessionFactory.getSession() : null);
    }

    public CassandraSessionFactory getSessionFactory() {
        return sessionFactory;
    }

    protected CassandraTemplate createCassandraTemplate(Session session) {
        return new CassandraTemplate(session);
    }

    public CassandraTemplate getTemplate() {
        return template;
    }

}
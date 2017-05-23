package com.example.diplom.db;

import com.google.inject.Provider;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class HibernateSessionFactoryProvider implements Provider<SessionFactory> {
    @Override
    public SessionFactory get() {
        try {
            Configuration configuration = new Configuration();
            configuration.configure();

            return configuration.buildSessionFactory();
        } catch (Throwable ex) {
            throw new ExceptionInInitializerError(ex);
        }
    }
}

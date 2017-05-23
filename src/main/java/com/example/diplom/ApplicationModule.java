package com.example.diplom;

import com.example.diplom.db.HibernateSessionFactoryProvider;
import com.google.inject.AbstractModule;
import org.hibernate.SessionFactory;

public class ApplicationModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(SessionFactory.class).toProvider(HibernateSessionFactoryProvider.class).asEagerSingleton();
    }
}

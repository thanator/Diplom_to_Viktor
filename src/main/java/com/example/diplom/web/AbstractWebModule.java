package com.example.diplom.web;

import com.example.diplom.db.models.User;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import spark.ModelAndView;
import spark.Request;
import spark.Response;
import spark.template.handlebars.HandlebarsTemplateEngine;

import javax.inject.Inject;
import java.util.Map;

public abstract class AbstractWebModule {
    protected final SessionFactory dbSessionFactory;
    protected final HandlebarsTemplateEngine templateEngine = new HandlebarsTemplateEngine();

    @Inject
    public AbstractWebModule(SessionFactory dbSessionFactory) {
        this.dbSessionFactory = dbSessionFactory;
    }

    public abstract void start();

    protected String render(Map<String, Object> model, String templatePath) {
        return templateEngine.render(new ModelAndView(model, templatePath));
    }

    protected User checkLogin(Request request, Response response) {
        String login = request.session().attribute("login");
        if (login == null) {
            return null;
        }

        try (Session session = dbSessionFactory.openSession()) {
            return session.get(User.class, login);
        } catch (HibernateException e) {
            return null;
        }
    }

    protected User checkLoginAndRedirect(Request request, Response response) {
        User user = checkLogin(request, response);
        if (user == null) {
            response.redirect("/auth/login");
        }
        return user;
    }

    protected User checkAdminAndRedirect(Request request, Response response) {
        User user = checkLoginAndRedirect(request, response);
        if (user == null) {
            return null;
        }
        if (!user.isAdmin()) {
            response.redirect("/");
            return null;
        }
        return user;
    }

    protected User checkLoginAndRedirect(Request request, Response response, Session session) {
        String login = request.session().attribute("login");
        if (login == null) {
            response.redirect("/auth/login");
            return null;
        }

        User user = session.find(User.class, login);
        if (user == null) {
            response.redirect("/auth/login");
        }
        return user;
    }
}

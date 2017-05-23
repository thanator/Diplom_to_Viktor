package com.example.diplom.web;

import com.example.diplom.db.models.User;
import com.google.inject.Inject;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import spark.Request;
import spark.Response;
import spark.Spark;

import java.util.Date;
import java.util.HashMap;

public class AuthModule extends AbstractWebModule {
    @Inject
    public AuthModule(SessionFactory dbSessionFactory) {
        super(dbSessionFactory);
    }

    @Override
    public void start() {
        Spark.get("/auth/login", this::showLogin);
        Spark.post("/auth/login", this::performLogin);

        Spark.get("/auth/register", this::showRegister);
        Spark.post("/auth/register", this::performRegister);

        Spark.post("/auth/logout", this::performLogout);
    }

    private String showLogin(Request request, Response response) {
        HashMap<String, Object> data = new HashMap<>();
        data.put("page_title", "Главная страница");
        return render(data, "login.html.hbs");
    }

    private String performLogin(Request request, Response response) {
        String login;
        String password;
        try {
            login = request.queryParams("login");
            if (login == null) throw new IllegalArgumentException();

            password = request.queryParams("password");
            if (password == null) throw new IllegalArgumentException();
        } catch (IllegalArgumentException e) {
            response.status(400);
            return "Invalid arguments";
        }

        try (Session session = dbSessionFactory.openSession()) {
            User user = session.get(User.class, login);

            if (user.tryAuth(password)) {
                request.session(true).attribute("login", user.getLogin());
                response.redirect("/");
                return "";
            } else {
                return "Wrong user or password";
            }
        } catch (HibernateException e) {
            return "Wrong user or password";
        }
    }

    private String showRegister(Request request, Response response) {
        HashMap<String, Object> data = new HashMap<>();
        data.put("page_title", "Регистрация");
        return render(data, "register.html.hbs");
    }

    private String performRegister(Request request, Response response) {
        String login;
        String password;
        String name;
        boolean isWoman;
        Date birthday;
        try {
            login = request.queryParams("login");
            if (login == null) throw new IllegalArgumentException();

            password = request.queryParams("password");
            if (password == null) throw new IllegalArgumentException();

            name = request.queryParams("name");
            if (name == null) throw new IllegalArgumentException();

            String sexStr = request.queryParams("sex");
            if (sexStr == null) throw new IllegalArgumentException();
            switch (sexStr) {
                case "Мужской":
                    isWoman = false;
                    break;
                case "Женский":
                    isWoman = true;
                    break;
                default:
                    throw new IllegalArgumentException();
            }

            String birthdayStr = request.queryParams("birthday");
            if (birthdayStr == null) throw new IllegalArgumentException();
            String[] birthdayStrDateParts = birthdayStr.split("\\.");
            if (birthdayStrDateParts.length != 3) throw new IllegalArgumentException();
            int birthdayDay = Integer.parseInt(birthdayStrDateParts[0]);
            int birthdayMonth = Integer.parseInt(birthdayStrDateParts[1]);
            int birthdayYear = Integer.parseInt(birthdayStrDateParts[2]);
            birthday = new Date(birthdayYear, birthdayMonth, birthdayDay);
        } catch (IllegalArgumentException e) {
            response.status(400);
            return "Invalid arguments";
        }

        try (Session session = dbSessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();

            User user = new User();
            user.setLogin(login);
            user.setPassword(password);
            user.setName(name);
            user.setWoman(isWoman);
            user.setAdmin(false);
            user.setBirthday(birthday);

            session.save(user);
            transaction.commit();

            request.session(true).attribute("login", user.getLogin());
            response.redirect("/");
            return "";
        }
    }

    private String performLogout(Request request, Response response) {
        request.session().invalidate();
        response.redirect("/");
        return "";
    }
}

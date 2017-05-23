package com.example.diplom.web;

import com.example.diplom.db.models.User;
import com.example.diplom.utils.DateUtils;
import com.google.inject.Inject;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import spark.Request;
import spark.Response;
import spark.Spark;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class AdminUsersModule extends AbstractWebModule {
    @Inject
    public AdminUsersModule(SessionFactory dbSessionFactory) {
        super(dbSessionFactory);
    }

    @Override
    public void start() {
        Spark.get("/admin/users", this::showList);
        Spark.post("/admin/users", this::createUser);

        Spark.get("/admin/users/new", this::showNewUser);
    }

    private String showList(Request request, Response response) {
        User loginUser = checkAdminAndRedirect(request, response);
        if (loginUser == null) {
            return "";
        }

        List<HashMap<String, String>> usersTemplateData = new ArrayList<>();
        try (Session session = dbSessionFactory.openSession()) {
            List<User> users = session.createQuery("from User").list();

            int i = 1;
            for (User user : users) {
                HashMap<String, String> userTemplateData = new HashMap<>();
                userTemplateData.put("index", String.valueOf(i++));
                userTemplateData.put("name", user.getName());
                userTemplateData.put("login", user.getLogin());
                userTemplateData.put("birthday", DateUtils.formatDate(user.getBirthday()));
                userTemplateData.put("sex", user.isWoman() ? "Ж" : "М");
                userTemplateData.put("group", user.isAdmin() ? "Админ" : "Пользователь");
                userTemplateData.put("login", user.getLogin());
                usersTemplateData.add(userTemplateData);
            }
        }

        HashMap<String, Object> data = new HashMap<>();
        data.put("page_title", "Ученики");
        data.put("user_name", loginUser.getName());
        data.put("users", usersTemplateData);

        return render(data, "admin_users.html.hbs");
    }

    private String createUser(Request request, Response response) {
        User loginUser = checkAdminAndRedirect(request, response);
        if (loginUser == null) {
            return "";
        }

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

            response.redirect("/admin/users");
            return "";
        }
    }

    private String showNewUser(Request request, Response response) {
        User loginUser = checkAdminAndRedirect(request, response);
        if (loginUser == null) {
            return "";
        }

        HashMap<String, Object> data = new HashMap<>();
        data.put("page_title", "Новый пользователь");
        data.put("user_name", loginUser.getName());
        return render(data, "admin_users_new.html.hbs");
    }
}

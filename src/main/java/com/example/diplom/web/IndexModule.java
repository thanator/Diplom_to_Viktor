package com.example.diplom.web;

import com.example.diplom.db.models.User;
import com.google.inject.Inject;
import org.hibernate.SessionFactory;
import spark.Request;
import spark.Response;
import spark.Spark;

import java.util.HashMap;

public class IndexModule extends AbstractWebModule {
    @Inject
    public IndexModule(SessionFactory dbSessionFactory) {
        super(dbSessionFactory);
    }

    @Override
    public void start() {
        Spark.get("/", this::showIndex);
        Spark.get("/rules", this::showRules);
    }

    private String showIndex(Request request, Response response) {
        User loginUser = checkLogin(request, response);
        if (loginUser == null) {
            return showIndexNoLogin(request, response);
        } else {
            return showIndexLogin(request, response, loginUser);
        }
    }

    private String showIndexNoLogin(Request request, Response response) {
        HashMap<String, Object> data = new HashMap<>();
        return render(data, "home.html");
    }

    private String showIndexLogin(Request request, Response response, User loginUser) {
        HashMap<String, Object> data = new HashMap<>();
        data.put("page_title", "Главная страница");
        data.put("user_name", loginUser.getName());
        data.put("is_admin", loginUser.isAdmin());
        return render(data, "index.html.hbs");
    }

    private String showRules(Request request, Response response) {
        HashMap<String, Object> data = new HashMap<>();
        return render(data, "rules.html");
    }
}

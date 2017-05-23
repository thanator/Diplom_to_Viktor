package com.example.diplom.web;

import com.example.diplom.db.TestUtils;
import com.example.diplom.db.models.*;
import com.example.diplom.utils.DateUtils;
import com.google.inject.Inject;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import spark.Request;
import spark.Response;
import spark.Spark;

import java.util.*;

public class AdminTestsModule extends AbstractWebModule {
    @Inject
    public AdminTestsModule(SessionFactory dbSessionFactory) {
        super(dbSessionFactory);
    }

    @Override
    public void start() {
        Spark.get("/admin/tests", this::showList);
        Spark.post("/admin/tests", this::createTest);
        Spark.get("/admin/tests/:id/tasks", this::showTasksList);
        Spark.post("/admin/tests/:id/tasks", this::createTask);
        Spark.get("/admin/tests/:id/tasks/new", this::showTaskNew);
        Spark.get("/admin/tests/:id/users", this::showUsers);
        Spark.get("/admin/tests/:test_id/users/:user_id", this::showUserResults);
        Spark.get("/admin/tests/new", this::showNew);
        Spark.post("/admin/tasks/:id", this::updateTask);
    }

    private String showList(Request request, Response response) {
        User loginUser = checkAdminAndRedirect(request, response);
        if (loginUser == null) {
            return "";
        }

        List<HashMap<String, Object>> testsTemplateData = new ArrayList<>();
        try (Session session = dbSessionFactory.openSession()) {
            List<Test> tests = session.createQuery("from Test").list();

            for (Test test : tests) {
                HashMap<String, Object> testTemplateData = new HashMap<>();
                testTemplateData.put("id", test.getId());
                testTemplateData.put("name", test.getName());
                testTemplateData.put("task_count", test.getTasks().size());
                testTemplateData.put("user_count", TestUtils.findUserResults(test, session).size());
                testsTemplateData.add(testTemplateData);
            }
        }

        HashMap<String, Object> data = new HashMap<>();
        data.put("page_title", "Тестирования");
        data.put("user_name", loginUser.getName());
        data.put("tests", testsTemplateData);

        return render(data, "admin_tests.html.hbs");
    }

    private String createTest(Request request, Response response) {
        User loginUser = checkAdminAndRedirect(request, response);
        if (loginUser == null) {
            return "";
        }

        String name;
        try {
            name = request.queryParams("name");
            if (name == null) throw new IllegalArgumentException();
        } catch (IllegalArgumentException e) {
            response.status(400);
            return "Invalid arguments";
        }

        try (Session session = dbSessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();

            Test test = new Test();
            test.setName(name);


            session.save(test);
            transaction.commit();

            response.redirect("/admin/tests");
            return "";
        }
    }

    private String showTasksList(Request request, Response response) {
        User loginUser = checkAdminAndRedirect(request, response);
        if (loginUser == null) {
            return "";
        }

        long testId;
        try {
            testId = Long.parseLong(request.params("id"));
        } catch (IllegalArgumentException e) {
            response.status(400);
            return "Invalid args";
        }

        Test test;
        HashMap<String, Object> testTemplateData = new HashMap<>();
        List<HashMap<String, Object>> tasksTemplateData = new ArrayList<>();
        try (Session session = dbSessionFactory.openSession()) {
            test = session.find(Test.class, testId);
            if (test == null) {
                response.status(404);
                return "Not found";
            }
            testTemplateData.put("id", test.getId());

            List<Task> tasks = test.getTasks();

            for (Task task : tasks) {
                HashMap<String, Object> taskTemplateData = new HashMap<>();
                taskTemplateData.put("id", task.getId());
                taskTemplateData.put("text", task.getText());
                taskTemplateData.put("image", task.getImageUrl());

                List<HashMap<String, Object>> answersTemplateData = new ArrayList<>();
                for (TaskAnswer answer : task.getAnswers()) {
                    HashMap<String, Object> answerTemplateData = new HashMap<>();
                    answerTemplateData.put("id", answer.getId());
                    answerTemplateData.put("text", answer.getText());
                    answerTemplateData.put("is_right", answer.isRight());
                    answersTemplateData.add(answerTemplateData);
                }
                taskTemplateData.put("answers", answersTemplateData);

                tasksTemplateData.add(taskTemplateData);
            }
        }

        HashMap<String, Object> data = new HashMap<>();
        data.put("page_title", String.format("Билеты к \"%s\"", test.getName()));
        data.put("user_name", loginUser.getName());
        data.put("test", testTemplateData);
        data.put("tasks", tasksTemplateData);

        return render(data, "admin_tasks.html.hbs");
    }

    private String createTask(Request request, Response response) {
        User loginUser = checkAdminAndRedirect(request, response);
        if (loginUser == null) {
            return "";
        }

        long testId;
        String text;
        String imageUrl;
        List<TaskAnswer> answers;
        try {
            testId = Long.parseLong(request.params("id"));

            text = request.queryParams("text");
            if (text == null) throw new IllegalArgumentException();

            imageUrl = request.queryParams("image");

            answers = parseTaskAnswers(request);
        } catch (IllegalArgumentException e) {
            response.status(400);
            return "Invalid arguments";
        }

        Test test;
        try (Session session = dbSessionFactory.openSession()) {
            test = session.find(Test.class, testId);
            if (test == null) {
                response.status(404);
                return "Not found";
            }

            Transaction transaction = session.beginTransaction();

            Task task = new Task();
            task.setText(text);
            task.setAnswers(answers);
            task.setImageUrl(imageUrl);
            test.addTask(task);

            session.save(test);
            transaction.commit();

            response.redirect(String.format("/admin/tests/%d/tasks#task%d", test.getId(), task.getId()));
            return "";
        }
    }

    private String showTaskNew(Request request, Response response) {
        User loginUser = checkAdminAndRedirect(request, response);
        if (loginUser == null) {
            return "";
        }

        long testId;
        try {
            testId = Long.parseLong(request.params("id"));
        } catch (IllegalArgumentException e) {
            response.status(400);
            return "Invalid args";
        }

        Test test;
        HashMap<String, Object> testTemplateData = new HashMap<>();
        try (Session session = dbSessionFactory.openSession()) {
            test = session.get(Test.class, testId);
            testTemplateData.put("id", test.getId());
        } catch (HibernateException e) {
            response.status(404);
            return "Not found";
        }

        HashMap<String, Object> data = new HashMap<>();
        data.put("page_title", String.format("Новый билет к \"%s\"", test.getName()));
        data.put("user_name", loginUser.getName());
        data.put("test", testTemplateData);

        return render(data, "admin_new_task.html.hbs");
    }

    private String showUsers(Request request, Response response) {
        User loginUser = checkAdminAndRedirect(request, response);
        if (loginUser == null) {
            return "";
        }

        long testId;
        try {
            testId = Long.parseLong(request.params("id"));
        } catch (IllegalArgumentException e) {
            response.status(400);
            return "Invalid args";
        }

        Test test;
        List<HashMap<String, String>> usersTemplateData = new ArrayList<>();
        HashMap<String, Object> testTemplateData = new HashMap<>();
        try (Session session = dbSessionFactory.openSession()) {
            test = session.find(Test.class, testId);
            if (test == null) {
                response.status(404);
                return "Not found";
            }
            testTemplateData.put("id", test.getId());

            Map<User, Float> users = TestUtils.findUserResults(test, session);

            int i = 1;
            for (Map.Entry<User, Float> entry : users.entrySet()) {
                User user = entry.getKey();
                HashMap<String, String> userTemplateData = new HashMap<>();
                userTemplateData.put("index", String.valueOf(i++));
                userTemplateData.put("name", user.getName());
                userTemplateData.put("birthday", DateUtils.formatDate(user.getBirthday()));
                userTemplateData.put("sex", user.isWoman() ? "Ж" : "М");
                userTemplateData.put("group", user.isAdmin() ? "Админ" : "Пользователь");
                userTemplateData.put("login", user.getLogin());
                userTemplateData.put("result", Math.round((1f - entry.getValue()) * 100) + "%");
                usersTemplateData.add(userTemplateData);
            }
        }

        HashMap<String, Object> data = new HashMap<>();
        data.put("page_title", String.format("Ученики, прошедшие \"%s\"", test.getName()));
        data.put("user_name", loginUser.getName());
        data.put("with_results", true);
        data.put("test", testTemplateData);
        data.put("users", usersTemplateData);

        return render(data, "admin_users.html.hbs");
    }

    private String showUserResults(Request request, Response response) {
        User loginUser = checkAdminAndRedirect(request, response);
        if (loginUser == null) {
            return "";
        }

        long testId;
        String userLogin;
        try {
            testId = Long.parseLong(request.params("test_id"));
            userLogin = request.params("user_id");
            if (userLogin == null) throw new IllegalArgumentException();
        } catch (IllegalArgumentException e) {
            response.status(400);
            return "Invalid args";
        }

        Test test;
        User user;
        float result;
        HashMap<String, Object> testTemplateData = new HashMap<>();
        List<HashMap<String, Object>> tasksTemplateData = new ArrayList<>();
        try (Session session = dbSessionFactory.openSession()) {
            test = session.find(Test.class, testId);
            if (test == null) {
                response.status(404);
                return "Not found";
            }
            testTemplateData.put("id", test.getId());

            user = session.find(User.class, userLogin);
            if (user == null) {
                response.status(404);
                return "Not found";
            }

            int countPlus = 0;
            int count = 0;

            for (Task task : test.getTasks()) {
                HashMap<String, Object> taskTemplateData = new HashMap<>();

                boolean isPassed = true;
                List<HashMap<String, Object>> answersTemplateData = new ArrayList<>();
                for (TaskAnswer answer : task.getAnswers()) {
                    HashMap<String, Object> answerTemplateData = new HashMap<>();

                    TaskUserAnswer userAnswer =
                            (TaskUserAnswer) session.createQuery("from TaskUserAnswer where user = :user and answer = :answer")
                                    .setParameter("user", user)
                                    .setParameter("answer", answer)
                                    .getSingleResult();
                    if (answer.isRight() != userAnswer.isChecked()) {
                        isPassed = false;
                    }

                    answerTemplateData.put("id", answer.getId());
                    answerTemplateData.put("text", answer.getText());
                    answerTemplateData.put("is_right", answer.isRight());
                    answerTemplateData.put("is_checked", userAnswer.isChecked());
                    answersTemplateData.add(answerTemplateData);
                }

                if (isPassed) {
                    countPlus++;
                }
                count++;

                taskTemplateData.put("index", count);
                taskTemplateData.put("id", task.getId());
                taskTemplateData.put("text", task.getText());
                taskTemplateData.put("is_passed", isPassed);
                taskTemplateData.put("answers", answersTemplateData);
                tasksTemplateData.add(taskTemplateData);
            }

            result = countPlus / (float) count;
        }

        HashMap<String, Object> data = new HashMap<>();
        data.put("page_title", String.format("Результаты %s к \"%s\"", user.getName(), test.getName()));
        data.put("user_name", loginUser.getName());
        data.put("result", Math.round(result * 100) + "%");
        data.put("tasks", tasksTemplateData);

        return render(data, "admin_results.html.hbs");
    }

    private String showNew(Request request, Response response) {
        User loginUser = checkAdminAndRedirect(request, response);
        if (loginUser == null) {
            return "";
        }

        HashMap<String, Object> data = new HashMap<>();
        data.put("page_title", "Новое тестирование");
        data.put("user_name", loginUser.getName());

        return render(data, "admin_new_test.html.hbs");
    }

    private String updateTask(Request request, Response response) {
        User loginUser = checkAdminAndRedirect(request, response);
        if (loginUser == null) {
            return "";
        }

        long taskId;
        String text;
        String imageUrl;
        List<TaskAnswer> answers;
        try {
            taskId = Long.parseLong(request.params("id"));

            text = request.queryParams("text");
            if (text == null) throw new IllegalArgumentException();

            imageUrl = request.queryParams("image");

            answers = parseTaskAnswers(request);
        } catch (IllegalArgumentException e) {
            response.status(400);
            return "Invalid arguments";
        }

        Task task;
        try (Session session = dbSessionFactory.openSession()) {
            task = session.find(Task.class, taskId);
            if (task == null) {
                response.status(404);
                return "Not found";
            }

            Transaction transaction = session.beginTransaction();

            task.setText(text);
            task.setAnswers(answers);
            task.setImageUrl(imageUrl);

            session.merge(task);
            transaction.commit();

            response.redirect(String.format("/admin/tests/%d/tasks#task%d", task.getTest().getId(), task.getId()));
            return "";
        }
    }

    private List<TaskAnswer> parseTaskAnswers(Request request) {
        HashMap<String, TaskAnswer> answersMap = new HashMap<>();
        for (String param : request.queryParams()) {
            if (param.startsWith("answer_")) {
                TaskAnswer answer;

                String[] paramParts = param.split("_");
                if (paramParts.length != 3) {
                    throw new IllegalArgumentException();
                }
                String idStr = paramParts[2];
                if (idStr.startsWith("new")) {
                    answer = new TaskAnswer();
                    answersMap.put(idStr, answer);
                } else {
                    long id = Long.parseLong(idStr);
                    if (!answersMap.containsKey(idStr)) {
                        answer = new TaskAnswer();
                        answer.setId(id);
                        answersMap.put(idStr, answer);
                    } else {
                        answer = answersMap.get(idStr);
                    }
                }

                if (param.startsWith("answer_text_")) {
                    answer.setText(request.queryParams(param));
                } else if (param.startsWith("answer_check_")) {
                    answer.setRight(request.queryParams(param).equals("on"));
                }
            }
        }
        List<TaskAnswer> answers = new ArrayList<>(answersMap.values());
        return answers;
    }
}

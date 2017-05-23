package com.example.diplom.web;

import com.example.diplom.db.TestUtils;
import com.example.diplom.db.UserUtils;
import com.example.diplom.db.models.*;
import com.google.inject.Inject;
import com.sun.org.apache.xpath.internal.operations.Bool;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import spark.Request;
import spark.Response;
import spark.Spark;

import java.util.*;

public class TestsModule extends AbstractWebModule {
    @Inject
    public TestsModule(SessionFactory dbSessionFactory) {
        super(dbSessionFactory);
    }

    @Override
    public void start() {
        Spark.get("/tests", this::showList);

        Spark.get("/tests/:id", this::showTest);

        Spark.get("/tests/:test_id/tasks/:task_id", this::showTask);
        Spark.post("/tests/:test_id/tasks/:task_id", this::submitTask);

        Spark.get("/tests/:id/results", this::showResults);
    }

    private String showList(Request request, Response response) {
        User loginUser;
        List<HashMap<String, Object>> testsTemplateData = new ArrayList<>();
        try (Session session = dbSessionFactory.openSession()) {
            loginUser = checkLoginAndRedirect(request, response, session);
            if (loginUser == null) {
                return "";
            }

            Set<Test> passedTests = new HashSet<>(loginUser.getPassedTests());

            List<Test> tests = session.createQuery("from Test").list();
            for (Test test : tests) {
                if (!TestUtils.isValid(test, session)) {
                    continue;
                }
                HashMap<String, Object> testTemplateData = new HashMap<>();
                testTemplateData.put("id", test.getId());
                testTemplateData.put("name", test.getName());
                testTemplateData.put("is_passed", passedTests.contains(test));
                testsTemplateData.add(testTemplateData);
            }
        }

        HashMap<String, Object> data = new HashMap<>();
        data.put("page_title", "Список тестирований");
        data.put("user_name", loginUser.getName());
        data.put("tests", testsTemplateData);
        return render(data, "tests.html.hbs");
    }

    private String showTest(Request request, Response response) {
        long testId;
        try {
            testId = Long.parseLong(request.params("id"));
        } catch (IllegalArgumentException e) {
            response.status(400);
            return "Invalid args";
        }

        User loginUser;
        try (Session session = dbSessionFactory.openSession()) {
            loginUser = checkLoginAndRedirect(request, response, session);
            if (loginUser == null) {
                return "";
            }

            Set<Test> passedTests = new HashSet<>(loginUser.getPassedTests());

            Test test = session.find(Test.class, testId);
            if (passedTests.contains(test)) {
                response.redirect(String.format("/tests/%d/results", test.getId()));
                return "";
            } else {
                List<Task> notDoneTasks = UserUtils.findNotDoneTasks(loginUser, test, session);

                if (notDoneTasks.isEmpty()) {
                    Transaction transaction = session.beginTransaction();

                    test.addPassedBy(loginUser);
                    session.save(test);
                    session.save(loginUser);

                    transaction.commit();

                    response.redirect(String.format("/tests/%d/results", test.getId()));
                    return "";
                } else {
                    Random random = new Random();
                    Task task = notDoneTasks.get(random.nextInt(notDoneTasks.size()));
                    response.redirect(String.format("/tests/%d/tasks/%d", test.getId(), task.getId()));
                    return "";
                }
            }
        }
    }

    private String showTask(Request request, Response response) {
        long taskId;
        try {
            taskId = Long.parseLong(request.params("task_id"));
        } catch (IllegalArgumentException e) {
            response.status(400);
            return "Illegal args";
        }

        User loginUser;
        Task task;
        Test test;
        HashMap<String, Object> taskTemplateData = new HashMap<>();
        HashMap<String, Object> testTemplateData = new HashMap<>();
        try (Session session = dbSessionFactory.openSession()) {
            loginUser = checkLoginAndRedirect(request, response, session);
            if (loginUser == null) {
                return "";
            }

            task = session.find(Task.class, taskId);
            if (task == null) {
                response.status(400);
                return "Invalid args";
            }
            test = task.getTest();

            List<Task> allTasks = test.getTasks();
            List<Task> notDoneTasks = UserUtils.findNotDoneTasks(loginUser, test, session);

            List<HashMap<String, Object>> answersTemplateData = new ArrayList<>();
            for (TaskAnswer answer : task.getAnswers()) {
                HashMap<String, Object> answerTemplateData = new HashMap<>();
                answerTemplateData.put("id", answer.getId());
                answerTemplateData.put("text", answer.getText());
                answersTemplateData.add(answerTemplateData);
            }

            taskTemplateData.put("id", task.getId());
            taskTemplateData.put("index", allTasks.size() - notDoneTasks.size() + 1);
            taskTemplateData.put("text", task.getText());
            taskTemplateData.put("image", task.getImageUrl());
            taskTemplateData.put("answers", answersTemplateData);

            testTemplateData.put("id", test.getId());
            testTemplateData.put("name", test.getName());
            testTemplateData.put("task_count", allTasks.size());
        }

        HashMap<String, Object> data = new HashMap<>();
        data.put("page_title", test.getName());
        data.put("user_name", loginUser.getName());
        data.put("task", taskTemplateData);
        data.put("test", testTemplateData);
        return render(data, "task.html.hbs");
    }

    private String submitTask(Request request, Response response) {
        long taskId;
        try {
            taskId = Long.parseLong(request.params("task_id"));
        } catch (IllegalArgumentException e) {
            response.status(400);
            return "Invalid args";
        }

        User loginUser;
        Task task;
        Test test;
        try (Session session = dbSessionFactory.openSession()) {
            loginUser = checkLoginAndRedirect(request, response, session);
            if (loginUser == null) {
                return "";
            }

            task = session.find(Task.class, taskId);
            if (task == null) {
                response.status(400);
                return "Invalid args";
            }
            test = task.getTest();

            List<TaskUserAnswer> answers;
            try {
                answers = parseTaskAnswers(request, session, loginUser, task);
            } catch (IllegalArgumentException e) {
                response.status(400);
                return "Invalid args";
            }

            Transaction transaction = session.beginTransaction();

            for (TaskUserAnswer answer : answers) {
                session.save(answer);
            }

            transaction.commit();
        }

        response.redirect(String.format("/tests/%d", test.getId()));
        return "";
    }

    private String showResults(Request request, Response response) {
        long testId;
        try {
            testId = Long.parseLong(request.params("id"));
        } catch (IllegalArgumentException e) {
            response.status(400);
            return "Invalid args";
        }

        User loginUser;
        Test test;
        float result;
        HashMap<String, Object> testTemplateData = new HashMap<>();
        List<HashMap<String, Object>> tasksTemplateData = new ArrayList<>();
        try (Session session = dbSessionFactory.openSession()) {
            loginUser = checkLoginAndRedirect(request, response, session);
            if (loginUser == null) {
                return "";
            }

            test = session.find(Test.class, testId);
            if (test == null) {
                response.status(400);
                return "Invalid args";
            }
            testTemplateData.put("id", test.getId());

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
                                    .setParameter("user", loginUser)
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
        data.put("page_title", String.format("Результаты к \"%s\"", test.getName()));
        data.put("user_name", loginUser.getName());
        data.put("result", Math.round(result * 100) + "%");
        data.put("tasks", tasksTemplateData);

        return render(data, "results.html.hbs");
    }

    private List<TaskUserAnswer> parseTaskAnswers(Request request, Session session, User user, Task task) {
        List<TaskUserAnswer> userAnswers = new ArrayList<>();
        Set<TaskAnswer> answers = new HashSet<>();
        for (String param : request.queryParams()) {
            if (param.startsWith("answer_check_")) {
                String[] paramParts = param.split("_");
                if (paramParts.length != 3) {
                    throw new IllegalArgumentException();
                }
                long id = Long.parseLong(paramParts[2]);

                TaskAnswer answer = session.find(TaskAnswer.class, id);
                if (answer == null) {
                    throw new IllegalArgumentException();
                }

                TaskUserAnswer userAnswer = new TaskUserAnswer();
                userAnswer.setAnswer(answer);
                userAnswer.setUser(user);
                userAnswer.setChecked(request.queryParams(param).equals("on"));
                userAnswers.add(userAnswer);
                answers.add(answer);
            }
        }
        for (TaskAnswer answer : task.getAnswers()) {
            if (!answers.contains(answer)) {
                TaskUserAnswer userAnswer = new TaskUserAnswer();
                userAnswer.setAnswer(answer);
                userAnswer.setUser(user);
                userAnswer.setChecked(false);
                userAnswers.add(userAnswer);
            }
        }
        return userAnswers;
    }
}

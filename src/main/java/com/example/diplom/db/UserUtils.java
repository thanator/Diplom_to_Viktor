package com.example.diplom.db;

import com.example.diplom.db.models.*;
import org.hibernate.Session;

import javax.persistence.NoResultException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class UserUtils {
    public static List<Task> findNotDoneTasks(User user, Test test, Session session) {
        List<Task> result = new ArrayList<>();
        for (Task task : test.getTasks()) {
            boolean doneTask = false;
            for (TaskAnswer answer : task.getAnswers()) {
                TaskUserAnswer userAnswer;
                try {
                    userAnswer =
                            (TaskUserAnswer) session.createQuery("from TaskUserAnswer where answer = :answer and user = :user")
                                    .setParameter("answer", answer)
                                    .setParameter("user", user)
                                    .getSingleResult();
                } catch (NoResultException e) {
                    userAnswer = null;
                }

                if (userAnswer != null) {
                    doneTask = true;
                    break;
                }
            }
            if (!doneTask) {
                result.add(task);
            }
        }
        return result;
    }
}

package com.example.diplom.db;

import com.example.diplom.db.models.Task;
import com.example.diplom.db.models.TaskAnswer;
import com.example.diplom.db.models.TaskUserAnswer;
import com.example.diplom.db.models.User;
import org.hibernate.Session;

import java.util.*;

public class TaskUtils {
    public static Map<User, Boolean> findUserResults(Task task, Session session) {
        HashMap<User, List<Boolean>> userPasses = new HashMap<>();
        for (TaskAnswer answer : task.getAnswers()) {
            List<TaskUserAnswer> userAnswers =
                    session.createQuery("from TaskUserAnswer where answer = :answer")
                            .setParameter("answer", answer)
                            .list();

            for (TaskUserAnswer userAnswer : userAnswers) {
                boolean isPassed = userAnswer.isChecked() == answer.isRight();
                List<Boolean> passes = userPasses.getOrDefault(userAnswer.getUser(), new ArrayList<>());
                passes.add(isPassed);
                userPasses.put(userAnswer.getUser(), passes);
            }
        }

        HashMap<User, Boolean> userResults = new HashMap<>();
        for (Map.Entry<User, List<Boolean>> entry : userPasses.entrySet()) {
            User key = entry.getKey();
            List<Boolean> value = entry.getValue();

            userResults.put(key, value.contains(false));
        }
        return userResults;
    }
}

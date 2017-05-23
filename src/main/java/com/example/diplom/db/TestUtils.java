package com.example.diplom.db;

import com.example.diplom.db.models.Task;
import com.example.diplom.db.models.Test;
import com.example.diplom.db.models.User;
import org.hibernate.Session;

import java.util.*;

public class TestUtils {
    public static Map<User, Float> findUserResults(Test test, Session session) {
        HashMap<User, List<Boolean>> userBooleanResults = new HashMap<>();
        for (Task task : test.getTasks()) {
            Map<User, Boolean> results = TaskUtils.findUserResults(task, session);
            for (Map.Entry<User, Boolean> entry : results.entrySet()) {
                User key = entry.getKey();
                Boolean value = entry.getValue();

                List<Boolean> resultsOfUser = userBooleanResults.getOrDefault(key, new ArrayList<>());
                resultsOfUser.add(value);
                userBooleanResults.put(key, resultsOfUser);
            }
        }

        HashMap<User, Float> userPercentResults = new HashMap<>();
        for (Map.Entry<User, List<Boolean>> entry : userBooleanResults.entrySet()) {
            User key = entry.getKey();
            List<Boolean> value = entry.getValue();

            int countPlus = 0;
            for (Boolean b : value) {
                if (b) {
                    countPlus++;
                }
            }

            userPercentResults.put(key, countPlus / (float) value.size());
        }
        return userPercentResults;
    }

    public static boolean isValid(Test test, Session session) {
        if (test.getTasks().size() == 0) {
            return false;
        }
        for (Task task : test.getTasks()) {
            if (task.getAnswers().size() == 0) {
                return false;
            }
        }
        return true;
    }
}

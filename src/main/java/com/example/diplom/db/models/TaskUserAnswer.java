package com.example.diplom.db.models;

import javax.persistence.*;

@Entity
@Table(name = "taskUserAnswers")
public class TaskUserAnswer {
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    private User user;

    @ManyToOne
    private TaskAnswer answer;


    private boolean isChecked;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public TaskAnswer getAnswer() {
        return answer;
    }

    public void setAnswer(TaskAnswer answer) {
        this.answer = answer;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TaskUserAnswer)) return false;

        TaskUserAnswer that = (TaskUserAnswer) o;

        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}

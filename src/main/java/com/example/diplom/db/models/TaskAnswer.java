package com.example.diplom.db.models;

import javax.persistence.*;

@Entity
@Table(name = "taskAnswers")
public class TaskAnswer {
    @Id
    @GeneratedValue
    private Long id;


    private String text;


    private boolean isRight;

    @ManyToOne(optional = false)
    private Task task;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public boolean isRight() {
        return isRight;
    }

    public void setRight(boolean right) {
        isRight = right;
    }

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TaskAnswer)) return false;

        TaskAnswer answer = (TaskAnswer) o;

        return id.equals(answer.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}

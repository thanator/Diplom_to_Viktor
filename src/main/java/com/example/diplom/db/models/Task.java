package com.example.diplom.db.models;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="tasks")
public class Task {
    @Id
    @GeneratedValue
    private Long id;


    private String text;

    @ManyToOne(optional = false)
    private Test test;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "task")
    private List<TaskAnswer> answers = new ArrayList<>();

    private String imageUrl;

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

    public List<TaskAnswer> getAnswers() {
        return answers;
    }

    public void setAnswers(List<TaskAnswer> answers) {
        this.answers.clear();
        this.answers.addAll(answers);
        for (TaskAnswer answer : this.answers) {
            answer.setTask(this);
        }
    }

    public void addAnswer(TaskAnswer answer) {
        answers.add(answer);
        answer.setTask(this);
    }

    public void removeAnswer(TaskAnswer answer) {
        answers.remove(answer);
        answer.setTask(null);
    }

    public Test getTest() {
        return test;
    }

    public void setTest(Test test) {
        this.test = test;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Task)) return false;

        Task task = (Task) o;

        return id.equals(task.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}

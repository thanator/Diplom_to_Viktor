package com.example.diplom.db.models;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="tests")
public class Test {
    @Id
    @GeneratedValue
    private Long id;


    private String name;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "test")
    private List<Task> tasks = new ArrayList<>();

    @ManyToMany(mappedBy = "passedTests")
    private List<User> passedBy = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Task> getTasks() {
        return tasks;
    }

    public void setTasks(List<Task> tasks) {
        this.tasks.clear();
        this.tasks.addAll(tasks);
        for (Task task : this.tasks) {
            task.setTest(this);
        }
    }

    public void addTask(Task task) {
        tasks.add(task);
        task.setTest(this);
    }

    public void removeTask(Task task) {
        tasks.remove(task);
        task.setTest(null);
    }

    public List<User> getPassedBy() {
        return passedBy;
    }

    public void setPassedBy(List<User> passedBy) {
        this.passedBy.clear();
        this.passedBy.addAll(passedBy);
        for (User user : this.passedBy) {
            user.getPassedTests().add(this);
        }
    }

    public void addPassedBy(User passedBy) {
        this.passedBy.add(passedBy);
        passedBy.getPassedTests().add(this);
    }

    public void removePassedBy(User passedBy) {
        this.passedBy.remove(passedBy);
        passedBy.getPassedTests().remove(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Test)) return false;

        Test test = (Test) o;

        return id.equals(test.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}

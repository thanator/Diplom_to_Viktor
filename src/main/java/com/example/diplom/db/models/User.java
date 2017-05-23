package com.example.diplom.db.models;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name="users")
public class User {
    @Id
    private String login;


    private String password;


    private String name;


    private boolean isAdmin;


    private Date birthday;


    private boolean isWoman;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private List<Test> passedTests = new ArrayList<>();

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public boolean tryAuth(String password) {
        return Objects.equals(this.password, password);
    }

    public boolean isWoman() {
        return isWoman;
    }

    public void setWoman(boolean woman) {
        isWoman = woman;
    }

    public List<Test> getPassedTests() {
        return passedTests;
    }

    public void setPassedTests(List<Test> passedTests) {
        this.passedTests.clear();
        this.passedTests.addAll(passedTests);
        for (Test test : this.passedTests) {
            test.getPassedBy().add(this);
        }
    }

    public void addPassedTest(Test test) {
        passedTests.add(test);
        test.getPassedBy().add(this);
    }

    public void removePassedTest(Test test) {
        passedTests.remove(test);
        test.getPassedBy().remove(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;

        User user = (User) o;

        return login.equals(user.login);
    }

    @Override
    public int hashCode() {
        return login.hashCode();
    }
}

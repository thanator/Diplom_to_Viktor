package com.example.diplom.web;

import com.google.inject.Inject;
import spark.*;

public class WebServer {
    private final IndexModule index;
    private final AuthModule auth;
    private final TestsModule tests;
    private final AdminTestsModule adminTests;
    private final AdminUsersModule adminUsers;

    @Inject
    public WebServer(IndexModule index, AuthModule auth, TestsModule tests, AdminTestsModule adminTests, AdminUsersModule adminUsers) {
        this.index = index;
        this.auth = auth;
        this.tests = tests;
        this.adminTests = adminTests;
        this.adminUsers = adminUsers;
    }

    public void start() {
        Spark.staticFiles.location("/public");
        index.start();
        auth.start();
        tests.start();
        adminTests.start();
        adminUsers.start();
    }
}

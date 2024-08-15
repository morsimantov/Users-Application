package com.example.myusersapplication.models;

import java.io.Serializable;

public class UserRequest implements Serializable {

    String name;
    String job;

    public UserRequest(String name, String job) {
        this.name = name;
        this.job = job;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job;
    }
}

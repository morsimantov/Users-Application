package com.example.myusersapplication.models;

import java.io.Serializable;

public class UserApiResponse implements Serializable {

    User data;
    SupportItem support;

    public UserApiResponse(User data, SupportItem support) {
        this.data = data;
        this.support = support;
    }

    public User getData() {
        return data;
    }

    public void setData(User data) {
        this.data = data;
    }

    public SupportItem getSupport() {
        return support;
    }

    public void setSupport(SupportItem support) {
        this.support = support;
    }
}

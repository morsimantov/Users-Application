package com.example.myusersapplication.models;

import java.io.Serializable;

public class SupportItem implements Serializable {

    String url;
    String text;

    public SupportItem(String url, String text) {
        this.url = url;
        this.text = text;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}

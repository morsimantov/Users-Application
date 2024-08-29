package com.example.myusersapplication;

import com.example.myusersapplication.models.User;

public interface UserActionListener {

    void onEditUser(User user);

    void onDeleteUser(User user);
}

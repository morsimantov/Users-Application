package com.example.myusersapplication;

import com.example.myusersapplication.models.User;

// Interface to handle user actions
public interface UserActionListener {

    // Called when the user requests to edit an existing user
    void onEditUser(User user);

    // Called when the user requests to delete an existing user
    void onDeleteUser(User user);
}
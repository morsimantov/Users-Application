package com.example.myusersapplication.mvvm;

import android.content.Context;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.myusersapplication.db.AppDatabase;
import com.example.myusersapplication.db.UserDao;
import com.example.myusersapplication.models.User;

import java.util.List;
import java.util.concurrent.Executors;

public class UsersRepository {

    private UserDao userDao;
    private MutableLiveData<String> operationStatus;
    private LiveData<List<User>> allUsersLiveData;

    public UsersRepository(Context context) {
        AppDatabase db = AppDatabase.getDatabase(context);
        this.userDao = db.userDao();
        this.operationStatus = new MutableLiveData<>();
        this.allUsersLiveData = userDao.getAllUsersLiveData(); // Ensure this method exists in UserDao
    }

    // Method to get all users as LiveData
    public LiveData<List<User>> getAllUsersLiveData() {
        return allUsersLiveData;
    }

    // Method to get the operation status
    public LiveData<String> getOperationStatus() {
        return operationStatus;
    }

    // Method to insert a user
    public void insertUser(String email, String firstName, String lastName, String avatar) {
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                // Check if the email already exists
                User user = userDao.getUserByEmail(email);
                if (user != null) {
                    operationStatus.postValue("User with this email already exists");
                    return;
                }

                User newUser = new User(0, email, firstName, lastName, avatar);
                userDao.insertUser(newUser);
                operationStatus.postValue("User created successfully");
            } catch (Exception e) {
                operationStatus.postValue("Error inserting user: " + e.getMessage());
            }
        });
    }

    // Method to delete a user
    public void deleteUser(int userId) {
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                userDao.deleteUser(userId);
                operationStatus.postValue("User deleted successfully");
            } catch (Exception e) {
                operationStatus.postValue("Error deleting user: " + e.getMessage());
            }
        });
    }

    // Method to update a user
    public void updateUser(int id, String email, String firstName, String lastName, String avatar) {
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                // Check if the email already exists
                User user = userDao.getUserByEmail(email);
                if (user != null && user.getId() != id) {
                    operationStatus.postValue("User with this email already exists");
                    return;
                }
                userDao.updateUserById(id, email, firstName, lastName, avatar);
                operationStatus.postValue("User updated successfully");
            } catch (Exception e) {
                operationStatus.postValue("Error updating user: " + e.getMessage());
            }
        });
    }
}
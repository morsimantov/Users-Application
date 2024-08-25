package com.example.myusersapplication.mvvm;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.myusersapplication.models.User;

import java.util.ArrayList;
import java.util.List;

public class UsersViewModel extends ViewModel {

    private UsersRepository usersRepository;
    private LiveData<List<User>> usersLiveData;
    private LiveData<String> operationStatus;

    public UsersViewModel(@NonNull Application application) {
        super();
        usersRepository = new UsersRepository(application.getApplicationContext());

        // Initialize usersLiveData
        usersLiveData = usersRepository.getAllUsersLiveData();
        // Initialize operationStatus
        operationStatus = usersRepository.getOperationStatus();
    }

    // Getter for usersLiveData
    public LiveData<List<User>> getUsersLiveData() {
        return usersLiveData;
    }

    // Getter for operationStatus
    public LiveData<String> getOperationStatus() {
        return operationStatus;
    }

    public void insertUser(String email, String firstName, String lastName, String avatar) {
        usersRepository.insertUser(email, firstName, lastName, avatar);
    }

    public void deleteUser(int userId) {
        usersRepository.deleteUser(userId);
    }

    public void updateUser(int id, String email, String firstName, String lastName, String avatar) {
        usersRepository.updateUser(id, email, firstName, lastName, avatar);
    }
}
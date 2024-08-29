package com.example.myusersapplication.mvvm;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.myusersapplication.models.User;

import java.util.ArrayList;
import java.util.List;

public class UsersViewModel extends ViewModel {

    public static final int PAGE_SIZE = 6;
    private UsersRepository usersRepository;
    private MutableLiveData<List<User>> usersLiveData = new MutableLiveData<>();
    private LiveData<String> operationStatus;

    public UsersViewModel(@NonNull Application application) {
        super();
        usersRepository = new UsersRepository(application.getApplicationContext());

        // Initialize operationStatus
        operationStatus = usersRepository.getOperationStatus();
        loadNextPage(0);
    }

    // Getter for usersLiveData
    public LiveData<List<User>> getUsersLiveData() {
        return usersLiveData;
    }

    // Getter for operationStatus
    public LiveData<String> getOperationStatus() {
        return operationStatus;
    }

    public void loadNextPage(int offset) {
        usersRepository.getUsersWithPaging(PAGE_SIZE, offset)
                // Observe the data returned by the repository indefinitely
                .observeForever(newUsers -> {
                    if (newUsers != null) {
                        List<User> currentUsers = usersLiveData.getValue();
                        // If there are already users loaded, add the new users to the existing list
                        if (currentUsers != null) {
                            currentUsers.addAll(newUsers);
                        } else {
                            currentUsers = new ArrayList<>(newUsers);
                        }
                        // Update the LiveData with the new list of users
                        // This will trigger UI updates to display the combined list of old and new users
                        usersLiveData.postValue(currentUsers);
                    }
                });
    }

    public void insertUser(String email, String firstName, String lastName, String avatar) {
        usersRepository.insertUser(email, firstName, lastName, avatar);
    }

    public void deleteUser(int userId) {
        // Delete the user from the repository
        usersRepository.deleteUser(userId);
    }

    public void updateUser(int id, String email, String firstName, String lastName, String avatar) {
        usersRepository.updateUser(id, email, firstName, lastName, avatar);
    }
}
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

    // Number of users to fetch per page
    public static final int PAGE_SIZE = 6;

    private UsersRepository usersRepository;

    // LiveData for observing the list of users
    private MutableLiveData<List<User>> usersLiveData = new MutableLiveData<>();

    // LiveData for observing operation status (e.g., success or error messages)
    private LiveData<String> operationStatus;

    public UsersViewModel(@NonNull Application application) {
        super();
        usersRepository = new UsersRepository(application.getApplicationContext());

        // Initialize operationStatus
        operationStatus = usersRepository.getOperationStatus();
    }

    // Getter for usersLiveData to observe the list of users
    public LiveData<List<User>> getUsersLiveData() {
        return usersLiveData;
    }

    // Getter for operationStatus to observe the status of operations
    public LiveData<String> getOperationStatus() {
        return operationStatus;
    }

    // Method to load the next page of users from the repository
    public void loadNextPage(int offset) {
        // Fetch users with pagination from the repository
        usersRepository.getUsersWithPaging(PAGE_SIZE, offset)
                // Observe the data returned by the repository indefinitely
                .observeForever(newUsers -> {
                    if (newUsers != null) {
                        // Get the current list of users
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

    // Method to refresh the list of users from the repository
    public void refreshUsers() {
        usersRepository.getAllUsers().observeForever(users -> usersLiveData.postValue(users));
    }

    // Method to insert a new user into the repository
    public void insertUser(String email, String firstName, String lastName, String avatar) {
        usersRepository.insertUser(email, firstName, lastName, avatar);
    }

    // Method to delete a user by ID from the repository
    public void deleteUser(int userId) {
        usersRepository.deleteUser(userId);
    }

    // Method to update an existing user in the repository
    public void updateUser(int id, String email, String firstName, String lastName, String avatar) {
        usersRepository.updateUser(id, email, firstName, lastName, avatar);
    }
}
package com.example.myusersapplication.mvvm;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.myusersapplication.models.CreateUserResponse;
import com.example.myusersapplication.models.UpdateUserResponse;
import com.example.myusersapplication.models.User;

import java.util.ArrayList;
import java.util.List;

public class UsersViewModel extends AndroidViewModel {
    private UsersRepository repo;
    private MutableLiveData<List<User>> usersLiveData;
    private MutableLiveData<Boolean> isLoading;
    private MutableLiveData<String> resultMessage;
    // Tracks the current page for pagination'
    private int currentPage;
    // Track if there are more pages to load
    private boolean hasMorePages;
    // Set the page size (6 users at a time)
    public static final int pageSize = 6;

    public UsersViewModel(@NonNull Application application) {
        super(application);
        repo = new UsersRepository(application.getApplicationContext());
        // LiveData for observing the list of users
        usersLiveData = new MutableLiveData<>(new ArrayList<>());
        // LiveData for observing the loading state
        isLoading = new MutableLiveData<>(false);
        resultMessage = new MutableLiveData<>();
        // Start from the first page
        currentPage = 1;
        // Initialize with true until proven otherwise
        hasMorePages = true;
        // Load the initial page of users
        loadMoreUsers();
    }

    // Expose the LiveData to observe user data
    public MutableLiveData<List<User>> getUsers() {
        return usersLiveData;
    }

    // Expose the LiveData to observe loading state
    public MutableLiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public MutableLiveData<String> getResultMessage() {
        return resultMessage;
    }

    // Method to load the pages of users
    public void loadMoreUsers() {
        if (isLoading.getValue() == Boolean.FALSE && hasMorePages) {
            isLoading.setValue(true);

            repo.getAllUsers(currentPage, pageSize, new UsersRepository.DataCallback() {
                @Override
                public void onSuccess(List<User> newUsers) {
                    if (newUsers.isEmpty()) {
                        hasMorePages = false;
                    } else {
                        // Get the current list of users
                        List<User> currentUsers = new ArrayList<>(usersLiveData.getValue());
                        // Add the new users
                        currentUsers.addAll(newUsers);
                        // Update LiveData with the new list
                        usersLiveData.postValue(currentUsers);
                        // Increment the page number for the next request
                        currentPage++;
                    }
                    isLoading.postValue(false);
                }

                @Override
                public void onFailure(Throwable t) {
                    isLoading.setValue(false);
                    // Handle the error (e.g., show a message to the user)
                }
            });
        }
    }


    // Create a new user
    public void addUser(String email, String firstName, String lastName, String avatar) {
        repo.addUser(email, firstName, lastName, avatar).observeForever(result -> {
            resultMessage.postValue(result);
            if (result.equals("User created successfully")) {
                loadMoreUsers();
            }
        });
    }


    private void refreshUsersList() {
        hasMorePages = true;
        currentPage = 1;
        usersLiveData.setValue(new ArrayList<>()); // Clear current list
        loadMoreUsers();
    }

    // Update an existing user by ID
    public MutableLiveData<String> updateUser(int id, String email, String firstName, String lastName, String avatar) {
        return repo.updateUser(id, email, firstName, lastName, avatar);
    }

    // Fetch a single user by ID
    public User getUser(int id) {
        return repo.getUser(id);
    }

    // Delete a user by ID
//    public Boolean deleteUser(int id) {
//        return repo.deleteUser(id);
//    }
}

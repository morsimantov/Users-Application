package com.example.myusersapplication.mvvm;

import android.app.Application;

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
    // Tracks the current page for pagination'
    private int currentPage;
    // Track if there are more pages to load
    private boolean hasMorePages;

    public UsersViewModel(@NonNull Application application) {
        super(application);
        repo = new UsersRepository(getApplication());
        // LiveData for observing the list of users
        usersLiveData = new MutableLiveData<>(new ArrayList<>());
        // LiveData for observing the loading state
        isLoading = new MutableLiveData<>(false);
        // Start from the first page
        currentPage = 1;
        // Initialize with true until proven otherwise
        hasMorePages = true;
        // Load the initial page of users
        loadMoreUsers();
    }

    // Expose the LiveData to observe user data
    public LiveData<List<User>> getUsers() {
        return usersLiveData;
    }

    // Expose the LiveData to observe loading state
    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    // Method to load the pages of users
    public void loadMoreUsers() {
        if (isLoading.getValue() == Boolean.FALSE && hasMorePages) {
            isLoading.setValue(true);

            // Set the page size (6 users at a time)
            int pageSize = 6;

            repo.getAllUsers(currentPage, pageSize, new UsersRepository.DataCallback() {
                @Override
                public void onSuccess(List<User> newUsers) {
                    List<User> currentUsers = usersLiveData.getValue();
                    if (currentUsers != null) {
                        if (newUsers.isEmpty()) {
                            hasMorePages = false;
                        } else {
                            currentUsers.addAll(newUsers);
                            usersLiveData.postValue(currentUsers);
                            currentPage++;
                            hasMorePages = true;
                        }
                    }
                    isLoading.postValue(false);
                }

                @Override
                public void onFailure(Throwable t) {
                    isLoading.setValue(false);
                    // Handle the error
                }
            });
        }
    }
    // Fetch a single user by ID
    public User getUser(int id) {
        return repo.getUser(id);
    }

    // Create a new user
    public MutableLiveData<String> createUser(String email, String firstName, String lastName, String avatar) {
        return repo.createUser(email, firstName, lastName, avatar);
    }

    // Update an existing user by ID
    public MutableLiveData<String> updateUser(int id, String email, String firstName, String lastName, String avatar) {
        return repo.updateUser(id, email, firstName, lastName, avatar);
    }

    // Delete a user by ID
//    public Boolean deleteUser(int id) {
//        return repo.deleteUser(id);
//    }
}

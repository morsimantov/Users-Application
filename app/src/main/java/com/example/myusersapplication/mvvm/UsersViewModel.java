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
        // If the data is not currently loading already and if there are more pages
        if (isLoading.getValue() == Boolean.FALSE && hasMorePages) {
            // Set loading to true
            isLoading.setValue(true);

            // Fetch users from the repository
            repo.getAllUsers(currentPage, new UsersRepository.DataCallback() {
                @Override
                public void onSuccess(List<User> newUsers) {
                    // Get current list of users
                    List<User> currentUsers = usersLiveData.getValue();
                    if (currentUsers != null) {
                        if (newUsers.isEmpty()) {
                            // If the new data is empty, mark that there are no more pages
                            hasMorePages = false;
                        } else {
                            // Add new users to the list
                            currentUsers.addAll(newUsers);
                            // Update LiveData with the new list
                            usersLiveData.setValue(currentUsers);
                            // Increment the page number for the next request
                            currentPage++;
                            hasMorePages = true;
                        }
                    }
                    isLoading.setValue(false);
                }

                @Override
                public void onFailure(Throwable t) {
                    isLoading.setValue(false);
                    // Handle the error (e.g., show a message to the user)
                }
            });
        }
    }

    // Fetch a single user by ID
    public MutableLiveData<User> getUser(int id) {
        return repo.getUser(id);
    }

    // Create a new user
    public MutableLiveData<CreateUserResponse> createUser(String name, String job) {
        return repo.createUser(name, job);
    }

    // Update an existing user by ID
    public MutableLiveData<UpdateUserResponse> updateUser(int id, String name, String job) {
        return repo.updateUser(id, name, job);
    }

    // Delete a user by ID
    public MutableLiveData<Boolean> deleteUser(int id) {
        return repo.deleteUser(id);
    }
}

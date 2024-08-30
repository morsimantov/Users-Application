package com.example.myusersapplication.mvvm;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.myusersapplication.api.RetrofitClient;
import com.example.myusersapplication.db.AppDatabase;
import com.example.myusersapplication.db.UserDao;
import com.example.myusersapplication.models.GetUsersResponse;
import com.example.myusersapplication.models.User;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UsersRepository {

    private static final String PREFS_NAME = "MyAppPrefs";
    private static final String KEY_INITIALIZED = "initialized";

    private UserDao userDao;
    private MutableLiveData<String> operationStatus = new MutableLiveData<>();
    private SharedPreferences sharedPreferences;
    private boolean isFetching = false;  // Add this at the class level
    private boolean isInitialized;

    public UsersRepository(Context context) {
        AppDatabase db = AppDatabase.getDatabase(context);
        this.userDao = db.userDao();
        this.sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        this.isInitialized = sharedPreferences.getBoolean(KEY_INITIALIZED, false);
    }

    // Method to get the operation status
    public LiveData<String> getOperationStatus() {
        return operationStatus;
    }

    // Method to get all users on a background thread
    public LiveData<List<User>> getAllUsers() {
        MutableLiveData<List<User>> data = new MutableLiveData<>();
        Executors.newSingleThreadExecutor().execute(() -> {
            List<User> users = userDao.getAllUsers();
            data.postValue(users);
        });
        return data;
    }

    // Method to get users with paging
    public LiveData<List<User>> getUsersWithPaging(int limit, int offset) {
        MutableLiveData<List<User>> data = new MutableLiveData<>();
        // Check if a fetch operation is already in progress
        if (isFetching) {
            Log.d(null, "Fetching already in progress, skipping...");
            return data;
        }
        isFetching = true;
        Executors.newSingleThreadExecutor().execute(() -> {
            Log.d(null,  "init: " + isInitialized);
            if (!isInitialized) {
                int page_number = offset / limit + 1;
                fetchUsersFromApi(page_number, data); // Pass MutableLiveData to update it with fetched data
                Log.d(null, "fetching from API page number: " + page_number);
            } else {
                List<User> usersFromDb = userDao.getUsersWithPagingSync(limit, offset);
                data.postValue(usersFromDb);
                isFetching = false;  // Reset the flag after fetching from the database
            }
        });
        return data;
    }

    private void fetchUsersFromApi(int page, MutableLiveData<List<User>> data) {
        Call<GetUsersResponse> call = RetrofitClient.getInstance().getApi().getAllUsers(page);
        call.enqueue(new Callback<GetUsersResponse>() {
            @Override
            public void onResponse(Call<GetUsersResponse> call, Response<GetUsersResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<User> newUsers = response.body().getData();
                    if (!newUsers.isEmpty()) {
                        Executors.newSingleThreadExecutor().execute(() -> {
                            List<User> usersToInsert = new ArrayList<>();

                            for (User user : newUsers) {
                                // Check if the user already exists in the database
                                if (userDao.getUserById(user.getId()) == null) {
                                    usersToInsert.add(user); // Add to the list if the user does not exist
                                }
                            }

                            if (!usersToInsert.isEmpty()) {
                                userDao.insertUsers(usersToInsert); // Insert the filtered list
                                data.postValue(usersToInsert); // Update LiveData with the new users
                            }
                        });
                    } else {
                        sharedPreferences.edit().putBoolean(KEY_INITIALIZED, true).apply();
                        isInitialized = true; // Cache the value
                        Log.d(null, "Done initialization");
                    }
                } else {
                    operationStatus.postValue("Failed to load users from API");
                }
                isFetching = false;
            }

            @Override
            public void onFailure(Call<GetUsersResponse> call, Throwable t) {
                operationStatus.postValue("Error fetching users from API: " + t.getMessage());
                isFetching = false;  // Reset the flag in case of failure
            }
        });
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

    // method for deleting a user with a callback
    public void deleteUser(int userId) {
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                userDao.deleteUser(userId);
                operationStatus.postValue("User deleted");
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
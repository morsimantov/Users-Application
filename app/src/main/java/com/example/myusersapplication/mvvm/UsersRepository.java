package com.example.myusersapplication.mvvm;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.myusersapplication.api.RetrofitClient;
import com.example.myusersapplication.database.AppDatabase;
import com.example.myusersapplication.database.UserDao;
import com.example.myusersapplication.models.GetUsersResponse;
import com.example.myusersapplication.models.User;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
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

    // Single ExecutorService instance for managing background tasks
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    // Flag to track if a fetch operation is in progress
    private boolean isFetching = false;
    // Flag to check if the database has been initialized
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

    // Method to fetch all users from the database on a background thread
    public LiveData<List<User>> getAllUsers() {
        MutableLiveData<List<User>> data = new MutableLiveData<>();
        executor.execute(() -> {
            List<User> users = userDao.getAllUsers();
            data.postValue(users);
        });
        return data;
    }

    // Method to fetch users with pagination support
    public LiveData<List<User>> getUsersWithPaging(int limit, int offset) {
        MutableLiveData<List<User>> data = new MutableLiveData<>();
        // Check if a fetch operation is already in progress
        if (isFetching) {
            return data;
        }
        // Set flag to true indicating a fetch is in progress
        isFetching = true;
        executor.execute(() -> {
            if (!isInitialized) {
                // Calculate the page number based on offset and limit
                int page_number = offset / limit + 1;
                // Fetch users from API
                fetchUsersFromApi(page_number, data);
            } else {
                // Fetch users from database if already initialized
                List<User> usersFromDb = userDao.getUsersWithPagingSync(limit, offset);
                data.postValue(usersFromDb);
                // Reset the flag after fetching from the database
                isFetching = false;
            }
        });
        return data;
    }

    // Method to fetch users from API and update the database
    private void fetchUsersFromApi(int page, MutableLiveData<List<User>> data) {
        Call<GetUsersResponse> call = RetrofitClient.getInstance().getApi().getAllUsers(page);
        call.enqueue(new Callback<GetUsersResponse>() {
            @Override
            public void onResponse(@NonNull Call<GetUsersResponse> call, @NonNull Response<GetUsersResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<User> newUsers = response.body().getData();
                    if (!newUsers.isEmpty()) {
                        executor.execute(() -> {
                            List<User> usersToInsert = new ArrayList<>();

                            for (User user : newUsers) {
                                // Check if the user already exists in the database
                                if (userDao.getUserById(user.getId()) == null) {
                                    // Add to the list if the user does not exist
                                    usersToInsert.add(user);
                                }
                            }

                            if (!usersToInsert.isEmpty()) {
                                // Insert the filtered list
                                userDao.insertUsers(usersToInsert);
                                // Update LiveData with the new users
                                data.postValue(usersToInsert);
                            }
                        });
                    } else {
                        sharedPreferences.edit().putBoolean(KEY_INITIALIZED, true).apply();
                        isInitialized = true; // Cache the value
                    }
                } else {
                    operationStatus.postValue("Failed to load users from API");
                }
                isFetching = false;
            }

            @Override
            public void onFailure(@NonNull Call<GetUsersResponse> call, @NonNull Throwable t) {
                operationStatus.postValue("Error fetching users from API: " + t.getMessage());
                // Reset the flag in case of failure
                isFetching = false;
            }
        });
    }

    // Method to insert a user into the database
    public void insertUser(String email, String firstName, String lastName, String avatar) {
        executor.execute(() -> {
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

    // Method to delete a user from the database
    public void deleteUser(int userId) {
        executor.execute(() -> {
            try {
                userDao.deleteUser(userId);
                operationStatus.postValue("User deleted");
            } catch (Exception e) {
                operationStatus.postValue("Error deleting user: " + e.getMessage());
            }
        });
    }

    // Method to update a user in the database
    public void updateUser(int id, String email, String firstName, String lastName, String avatar) {
        executor.execute(() -> {
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
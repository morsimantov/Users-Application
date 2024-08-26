package com.example.myusersapplication.mvvm;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.recyclerview.widget.AsyncListUtil;

import com.example.myusersapplication.api.RetrofitClient;
import com.example.myusersapplication.db.AppDatabase;
import com.example.myusersapplication.db.UserDao;
import com.example.myusersapplication.models.GetUsersResponse;
import com.example.myusersapplication.models.User;

import java.util.List;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UsersRepository {

//    private static final String PREFS_NAME = "MyAppPrefs";
//    private static final String KEY_INITIALIZED = "initialized";

    private UserDao userDao;
    private MutableLiveData<String> operationStatus;
    private LiveData<List<User>> allUsersLiveData;
    private SharedPreferences sharedPreferences;

    private int currentPage = 1;

    public UsersRepository(Context context) {
        AppDatabase db = AppDatabase.getDatabase(context);
        this.userDao = db.userDao();
        this.operationStatus = new MutableLiveData<>();
//        this.sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        this.allUsersLiveData = userDao.getAllUsersLiveData();
        initializeData();
    }

    private void initializeData() {
        Executors.newSingleThreadExecutor().execute(() -> {
//            boolean initialized = sharedPreferences.getBoolean(KEY_INITIALIZED, false);
//            if (!initialized) {
                List<User> users = userDao.getAllUsers();
                if (users == null || users.isEmpty()) {
                    fetchUsersFromApi(currentPage); // Fetch users from the first page
                }
//                sharedPreferences.edit().putBoolean(KEY_INITIALIZED, true).apply();
//            }
        });
    }

    private void fetchUsersFromApi(int page) {
        Call<GetUsersResponse> call = RetrofitClient.getInstance().getApi().getAllUsers(page);
        call.enqueue(new Callback<GetUsersResponse>() {
            @Override
            public void onResponse(Call<GetUsersResponse> call, Response<GetUsersResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<User> newUsers = response.body().getData();
                    if (!newUsers.isEmpty()) {
                        // Insert new users into the Room database
                        Executors.newSingleThreadExecutor().execute(() -> userDao.insertUsers(newUsers));
                        operationStatus.postValue("Users loaded from API");
                        // Increment the page for next request
                        currentPage++;
                    }
                } else {
                    operationStatus.postValue("Failed to load users from API");
                }
            }

            @Override
            public void onFailure(Call<GetUsersResponse> call, Throwable t) {
                operationStatus.postValue("Error fetching users from API: " + t.getMessage());
            }
        });
    }

    // Method to get users with paging
    public LiveData<List<User>> getUsersWithPaging(int limit, int offset) {
        return userDao.getUsersWithPaging(limit, offset);
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
package com.example.myusersapplication.mvvm;

import android.content.Context;
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

import java.util.List;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UsersRepository {

    private UserDao userDao;
    private MutableLiveData<String> operationStatus = new MutableLiveData<>();

    public UsersRepository(Context context) {
        AppDatabase db = AppDatabase.getDatabase(context);
        this.userDao = db.userDao();
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
        Executors.newSingleThreadExecutor().execute(() -> {
            List<User> usersFromDb = userDao.getUsersWithPagingSync(limit, offset);
            if (usersFromDb != null && !usersFromDb.isEmpty()) {
                data.postValue(usersFromDb);
            } else {
                int page_number = offset / limit + 1;
                fetchUsersFromApi(page_number, data); // Pass MutableLiveData to update it with fetched data
                Log.d(null, "fetching from API page number: " + page_number);
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
                            userDao.insertUsers(newUsers);
                            data.postValue(newUsers); // Update data LiveData
                        });
                        operationStatus.postValue("Users loaded from API");
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

    // New method for deleting a user with a callback
    public void deleteUser(int userId, Runnable onSuccess) {
        Executors.newSingleThreadExecutor().execute(() -> {
            userDao.deleteUser(userId);
            ((MutableLiveData<String>) operationStatus).postValue("User deleted");

            // Run the callback on the main thread
            new Handler(Looper.getMainLooper()).post(onSuccess);
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
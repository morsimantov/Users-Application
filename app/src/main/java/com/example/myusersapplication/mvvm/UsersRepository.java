package com.example.myusersapplication.mvvm;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;

import com.example.myusersapplication.api.RetrofitClient;
import com.example.myusersapplication.db.AppDatabase;
import com.example.myusersapplication.db.UserDao;
import com.example.myusersapplication.models.GetUsersResponse;
import com.example.myusersapplication.models.User;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UsersRepository {

    private UserDao userDao;
    private List<User> users;
    private Context context;

    public UsersRepository(Context context) {
        this.context = context;
        AppDatabase db = AppDatabase.getDatabase(context);
        this.userDao = db.userDao();
        users = new ArrayList<>(); // Initialize the cache
        initializeData();
    }

    private void initializeData() {
        // Load initial data if database is empty
        new Thread(() -> {
            if (userDao.getUserCount() == 0) {
                fetchUsersFromApi(1, new DataCallback() {
                    @Override
                    public void onSuccess(List<User> users) {
                        System.out.println("Initial data fetch successful, number of users fetched: " + users.size());
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        System.err.println("Initial data fetch failed: " + t.getMessage());
                    }
                });
            }
        }).start();
    }

    public void getAllUsers(int page, int pageSize, DataCallback callback) {
        new Thread(() -> {
            // Calculate the offset (number of users to skip)
            int offset = (page - 1) * pageSize;

            // Fetch users with paging from the Room database
            List<User> localUsers = userDao.getUsersWithPaging(pageSize, offset);
            if (localUsers != null && !localUsers.isEmpty()) {
                // If users are found locally, return them
                callback.onSuccess(localUsers);
            } else if (userDao.getUserCount() == 0) {
                // If the local database is empty, fetch from the API
                fetchUsersFromApi(page, callback);
            } else {
                // If no users are found for this page, return an empty list
                callback.onSuccess(new ArrayList<>());
            }
        }).start();
    }

    private void fetchUsersFromApi(int page, DataCallback callback) {
        Call<GetUsersResponse> call = RetrofitClient.getInstance().getApi().getAllUsers(page);
        call.enqueue(new Callback<GetUsersResponse>() {
            @Override
            public void onResponse(Call<GetUsersResponse> call, Response<GetUsersResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<User> newUsers = response.body().getData();
                    if (!newUsers.isEmpty()) {
                        // Insert new users into the Room database
                        new Thread(() -> userDao.insertUsers(newUsers)).start();
                    }
                    callback.onSuccess(newUsers);
                } else {
                    callback.onSuccess(new ArrayList<>());  // Return an empty list if no data
                }
            }

            @Override
            public void onFailure(Call<GetUsersResponse> call, Throwable t) {
                callback.onFailure(t);
            }
        });
    }

    public User getUser(int id) {
        return userDao.getUserById(id);
    }

    public MutableLiveData<String> addUser(String email, String firstName, String lastName, String avatar) {
        MutableLiveData<String> resultMessage = new MutableLiveData<>();

        User newUser = new User(
                0,  // Set the id to 0 or any placeholder (Room will auto-generate it)
                email,
                firstName,
                lastName,
                avatar
        );

        new Thread(() -> {
            try {
                User existingUser = userDao.getUserByEmail(email);
                if (existingUser != null) {
                    resultMessage.postValue("User with this email already exists");
                } else {
                    userDao.insertUser(newUser);
                    resultMessage.postValue("User created successfully");
                }
            } catch (Exception e) {
                resultMessage.postValue("Failed to create user: " + e.getMessage());
            }
        }).start();

        return resultMessage;
    }

    public MutableLiveData<String> updateUser(int id, String email, String firstName, String lastName, String avatar) {
        MutableLiveData<String> updateStatus = new MutableLiveData<>();

        new Thread(() -> {
            User userWithSameEmail = userDao.getUserByEmail(email);
            if (userWithSameEmail != null && userWithSameEmail.getId() != id) {
                updateStatus.postValue("User with this email already exists");
            } else {
                try {
                    userDao.updateUserById(id, email, firstName, lastName, avatar);
                    updateStatus.postValue("User updated successfully");
                } catch (Exception e) {
                    updateStatus.postValue("Failed to update user");
                }
            }
        }).start();

        return updateStatus;
    }

    public interface DataCallback {
        void onSuccess(List<User> users);
        void onFailure(Throwable t);
    }
}

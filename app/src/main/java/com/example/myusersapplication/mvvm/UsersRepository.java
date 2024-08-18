package com.example.myusersapplication.mvvm;

import android.app.Application;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.myusersapplication.api.RetrofitClient;
import com.example.myusersapplication.db.AppDatabase;
import com.example.myusersapplication.db.UserDao;
import com.example.myusersapplication.models.CreateUserResponse;
import com.example.myusersapplication.models.GetUserResponse;
import com.example.myusersapplication.models.GetUsersResponse;
import com.example.myusersapplication.models.UpdateUserResponse;
import com.example.myusersapplication.models.User;
import com.example.myusersapplication.models.UserRequest;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UsersRepository {

    private UserDao userDao;
    private List<User> users;

    public UsersRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
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
                        // Handle success
                        System.out.println("Initial data fetch successful, number of users fetched: " + users.size());
                        // You can update UI or notify other parts of your app here if needed
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        // Handle failure
                        System.err.println("Initial data fetch failed: " + t.getMessage());
                        // Notify user or log the error
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
                callback.onSuccess(localUsers);
            } else {
                // If no users are in the Room database, fetch from the API
                fetchUsersFromApi(page, callback);
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
                        // Update cache
                        users.addAll(newUsers);

                        // Insert new users into the Room database
                        new Thread(() -> {
                            userDao.insertUsers(newUsers);
                        }).start();
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


    // Fetch a single user from the Room database
    public User getUser(int id) {
        return userDao.getUserById(id);
    }


    public MutableLiveData<String> createUser(String email, String first_name, String last_name, String avatar) {
        MutableLiveData<String> resultMessage = new MutableLiveData<>();

        // Create a new User object with the given details
        User newUser = new User(
                0,  // Set the id to 0 or any placeholder (Room will auto-generate it)
                email,
                first_name,
                last_name,
                avatar
        );

        new Thread(() -> {
            try {
                // Check if a user with the same email already exists in the database
                User existingUser = userDao.getUserByEmail(email);  // Assuming userDao has a method for this

                if (existingUser != null) {
                    // If the user exists, post an error message
                    resultMessage.postValue("User with this email already exists");
                    System.out.println("User with email " + email + " already exists in the Room database");
                } else {
                    // Insert the new user into the Room database
                    userDao.insertUser(newUser);
                    resultMessage.postValue("User created successfully");
                    System.out.println("User created locally and saved in the Room database");
                }
            } catch (Exception e) {
                resultMessage.postValue("Failed to create user: " + e.getMessage());
                System.out.println("Failed to create user locally: " + e.getMessage());
            }
        }).start();

        return resultMessage;
    }

    public MutableLiveData<String> updateUser(int id, String email, String first_name, String last_name, String avatar) {
        MutableLiveData<String> updateStatus = new MutableLiveData<>();

        new Thread(() -> {
            // Check if a user with the same email already exists (except for the user being updated)
            User userWithSameEmail = userDao.getUserByEmail(email);
            if (userWithSameEmail != null && userWithSameEmail.getId() != id) {
                updateStatus.postValue("User with this email already exists");
            } else {
                // Update the user locally
                try {
                    userDao.updateUserById(id, email, first_name, last_name, avatar);
                    updateStatus.postValue("User updated successfully");
                } catch (Exception e) {
                    updateStatus.postValue("Failed to update user");
                }
            }
        }).start();

        return updateStatus;
    }


//    public MutableLiveData<String> deleteUser(int id) {
//        MutableLiveData<String> deleteUserResponse = new MutableLiveData<>();
//
//        // Delete the user from the Room database in a background thread
//        new Thread(() -> {
//            try {
//                User userToDelete = userDao.getUserById(id);
//                if userToDelete != null {
//
//                }
//                int rowsDeleted = userDao.deleteUser(id);
//                if (rowsDeleted > 0) {
//                    deleteUserResponse.postValue("User deleted successfully");
//                } else {
//                    deleteUserResponse.postValue("User not found");
//                }
//            } catch (Exception e) {
//                deleteUserResponse.postValue("Failed to delete user: " + e.getMessage());
//            }
//        }).start();
//
//        return deleteUserResponse;
//    }

    public interface DataCallback {
        void onSuccess(List<User> users);
        void onFailure(Throwable t);
    }
}

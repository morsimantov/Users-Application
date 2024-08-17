package com.example.myusersapplication.mvvm;

import android.app.Application;

import androidx.lifecycle.MutableLiveData;

import com.example.myusersapplication.api.RetrofitClient;
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
    MutableLiveData<List<User>> liveDataUsers;
    List<User> users;

    public UsersRepository(Application application) {
        this.liveDataUsers = new MutableLiveData<>();
        this.users = new ArrayList<User>();
    }

    public MutableLiveData<List<User>> getAllUsers(int page, DataCallback callback) {
        Call<GetUsersResponse> call = RetrofitClient.getInstance().getApi().getAllUsers(page);
        call.enqueue(new Callback<GetUsersResponse>() {
            @Override
            public void onResponse(Call<GetUsersResponse> call, Response<GetUsersResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body().getData());
                    users.clear();
                    users.addAll(response.body().getData());
                    liveDataUsers.postValue(users);
                }
            }

            @Override
            public void onFailure(Call<GetUsersResponse> call, Throwable t) {
                liveDataUsers.postValue(null);
                callback.onFailure(t);
            }
        });
        return liveDataUsers;
    }

    public MutableLiveData<User> getUser(int id) {
        MutableLiveData<User> user = new MutableLiveData<>();
        Call<GetUserResponse> call = RetrofitClient.getInstance().getApi().getUser(id);
        call.enqueue(new Callback<GetUserResponse>() {
            @Override
            public void onResponse(Call<GetUserResponse> call, Response<GetUserResponse> response) {
                if (response.isSuccessful()) {
                    System.out.println("success");
                    user.postValue(response.body().getData());
                }
            }

            @Override
            public void onFailure(Call<GetUserResponse> call, Throwable t) {
                user.postValue(null);
                System.out.println("t.getMessage() = " + t.getMessage());
            }
        });
        return user;
    }


    public MutableLiveData<CreateUserResponse> createUser(String name, String job) {
        UserRequest request = new UserRequest(name, job);
        MutableLiveData<CreateUserResponse> createUserResponse = new MutableLiveData<>();
        Call<CreateUserResponse> call = RetrofitClient.getInstance().getApi().createUser(request);
        call.enqueue(new Callback<CreateUserResponse>() {
            @Override
            public void onResponse(Call<CreateUserResponse> call, Response<CreateUserResponse> response) {
                if (response.isSuccessful()) {
                    createUserResponse.postValue(response.body());
                    System.out.println("success");
                }
            }

            @Override
            public void onFailure(Call<CreateUserResponse> call, Throwable t) {
                createUserResponse.postValue(null);
                System.out.println("t.getMessage() = " + t.getMessage());
            }
        });
        return createUserResponse;
    }

    public MutableLiveData<UpdateUserResponse> updateUser(int id, String name, String job) {
        UserRequest request = new UserRequest(name, job);
        MutableLiveData<UpdateUserResponse> updateUserResponse = new MutableLiveData<>();
        Call<UpdateUserResponse> call = RetrofitClient.getInstance().getApi().updateUser(id, request);
        call.enqueue(new Callback<UpdateUserResponse>() {
            @Override
            public void onResponse(Call<UpdateUserResponse> call, Response<UpdateUserResponse> response) {
                if (response.isSuccessful()) {
                    updateUserResponse.postValue(response.body());
                    System.out.println("success");
                }
            }

            @Override
            public void onFailure(Call<UpdateUserResponse> call, Throwable t) {
                updateUserResponse.postValue(null);
                System.out.println("t.getMessage() = " + t.getMessage());
            }
        });
        return updateUserResponse;
    }


    public MutableLiveData<Boolean> deleteUser(int id) {
        MutableLiveData<Boolean> deleteUserResponse = new MutableLiveData<>();
        Call<Void> call = RetrofitClient.getInstance().getApi().deleteUser(id);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    // Notify that the deletion was successful
                    deleteUserResponse.postValue(true);
                    System.out.println("User deleted successfully");
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                // Notify that the deletion failed due to an error
                deleteUserResponse.postValue(false);
                System.out.println("t.getMessage() = " + t.getMessage());
            }
        });
        return deleteUserResponse;
    }

    public interface DataCallback {
        void onSuccess(List<User> users);
        void onFailure(Throwable t);
    }
}

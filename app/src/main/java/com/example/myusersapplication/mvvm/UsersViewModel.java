package com.example.myusersapplication.mvvm;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.example.myusersapplication.models.CreateUserResponse;
import com.example.myusersapplication.models.UpdateUserResponse;
import com.example.myusersapplication.models.User;

import java.util.List;

public class UsersViewModel extends AndroidViewModel {
    private UsersRepository repo;

    public UsersViewModel(@NonNull Application application) {
        super(application);
        repo = new UsersRepository(getApplication());
    }

    public MutableLiveData<List<User>> getAll() {
        return repo.getAllUsers();
    }

    public MutableLiveData<User> getUser(int id) {
        return repo.getUser(id);
    }

    public MutableLiveData<CreateUserResponse> createUser(String name, String job) {
        return repo.createUser(name, job);
    }

    public MutableLiveData<UpdateUserResponse> updateUser(int id, String name, String job) {
        return repo.updateUser(id, name, job);
    }

    public MutableLiveData<Boolean> deleteUser(int id) {
        return repo.deleteUser(id);
    }
}

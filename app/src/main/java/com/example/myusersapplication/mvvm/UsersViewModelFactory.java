package com.example.myusersapplication.mvvm;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class UsersViewModelFactory extends ViewModelProvider.NewInstanceFactory {

    private final Application application;

    public UsersViewModelFactory(Application application) {
        this.application = application;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(UsersViewModel.class)) {
            return (T) new UsersViewModel(application);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}

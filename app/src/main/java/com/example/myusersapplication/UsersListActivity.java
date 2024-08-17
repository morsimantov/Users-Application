package com.example.myusersapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myusersapplication.models.User;
import com.example.myusersapplication.mvvm.UsersViewModel;

import java.util.List;

public class UsersListActivity extends AppCompatActivity {

    private UsersViewModel usersViewModel;
    private UsersListAdapter adapter;
    private List<User> usersList;
    private boolean isLoading = false; // Track loading state

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users_list);

        usersViewModel = new ViewModelProvider(this).get(UsersViewModel.class);

        RecyclerView recycler = findViewById(R.id.users_recycler);

//        recycler.setHasFixedSize(true);
        recycler.setLayoutManager(new GridLayoutManager(this, 1));
        adapter = new UsersListAdapter(usersList, this);
        recycler.setAdapter(adapter);

        // Observe user data from the ViewModel
        usersViewModel.getUsers().observe(this, new Observer<List<User>>() {
            @Override
            public void onChanged(List<User> users) {
                if (users != null) {
                    usersList = users;
                    adapter.updateList(usersList);
                    // Reset loading state when data is loaded
                    isLoading = false;
                }
            }
        });

        // Observe loading state
        usersViewModel.getIsLoading().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean loading) {
                isLoading = loading;
            }
        });

        // Scroll listener to load more users when reaching the end of the list
        recycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                GridLayoutManager layoutManager = (GridLayoutManager) recyclerView.getLayoutManager();
                int visibleItemCount = layoutManager.getChildCount();
                int totalItemCount = layoutManager.getItemCount();
                int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();
                // If we are not currently loading and if we've reached the end of the list
                if (!isLoading && (visibleItemCount + firstVisibleItemPosition) >= totalItemCount) {
                    // Trigger the method to load more users
                    usersViewModel.loadMoreUsers();
                }
            }
        });
    }

}
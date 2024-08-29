package com.example.myusersapplication;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myusersapplication.models.User;
import com.example.myusersapplication.mvvm.UsersViewModel;
import com.example.myusersapplication.mvvm.UsersViewModelFactory;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

public class UsersListActivity extends AppCompatActivity {

    private UsersViewModel usersViewModel;
    private UsersListAdapter adapter;
    private List<User> usersList;
    private FloatingActionButton addUserButton;
    private ProgressBar progressBar;
    private RecyclerView recycler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users_list);

        recycler = findViewById(R.id.users_recycler);
        addUserButton = findViewById(R.id.add_user_button);

        recycler.setLayoutManager(new GridLayoutManager(this, 1));
        // Initialize the adapter
        adapter = new UsersListAdapter(new ArrayList<>(), this, getApplication(), new UserActionListener() {
            @Override
            public void onEditUser(User user) {
                EditUserFragment editUserFragment = EditUserFragment.newInstance(user, adapter);
                editUserFragment.show(getSupportFragmentManager(), "editUserFragment");
            }

            @Override
            public void onDeleteUser(User user) {
                usersViewModel.deleteUser(user.getId());
            }
        });

        recycler.setAdapter(adapter);

        progressBar = findViewById(R.id.progress_circular);

        addUserButton.setOnClickListener(v -> {
            Intent intent = new Intent(UsersListActivity.this, AddUserActivity.class);
            // Set up the transition for shared elements
            ActivityOptions options = ActivityOptions
                    .makeSceneTransitionAnimation(this, addUserButton, "transition_fab");
            // Start the new activity
            startActivity(intent, options.toBundle());
        });

        // Initialize ViewModel with factory
        UsersViewModelFactory factory = new UsersViewModelFactory(getApplication());
        usersViewModel = new ViewModelProvider(this, factory).get(UsersViewModel.class);

        // Observe user data from the ViewModel
        usersViewModel.getUsersLiveData().observe(this, new Observer<List<User>>() {
            @Override
            public void onChanged(List<User> users) {
                if (users != null) {
                    usersList = users;
                    adapter.updateList(usersList);  // Update the adapter's list
                    adapter.notifyDataSetChanged(); // Notify the adapter that the data has changed

                    // Hide ProgressBar and show RecyclerView
                    progressBar.setVisibility(View.GONE);
                    recycler.setVisibility(View.VISIBLE);
                }
            }
        });

        // Load initial data
        usersViewModel.loadNextPage(0);

        // Observe operationStatus
        usersViewModel.getOperationStatus().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String statusMessage) {
                if (statusMessage != null && !statusMessage.isEmpty()) {
                    adapter.notifyDataSetChanged();
                    // Show a message to the user
                    Snackbar.make(findViewById(android.R.id.content), statusMessage, Snackbar.LENGTH_SHORT).show();
                }
            }
        });

        // Scroll listener to load more users when reaching the end of the list
        recycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                // Check if we have reached the end of the list and load more data
                if (!recyclerView.canScrollVertically(1)) {
                    // Load the next page
                    int offset = adapter.getItemCount();
                    Log.d(null, "offset is: " + offset);
                    usersViewModel.loadNextPage(offset);
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        adapter.notifyDataSetChanged();
        Log.d(null, "On resume");
    }
}
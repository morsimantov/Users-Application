package com.example.myusersapplication.activities;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentResultListener;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myusersapplication.R;
import com.example.myusersapplication.UserActionListener;
import com.example.myusersapplication.UsersListAdapter;
import com.example.myusersapplication.fragments.EditUserFragment;
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
    private List<User> usersList = new ArrayList<>();
    private FloatingActionButton addUserButton;
    private ProgressBar progressBar;
    private RecyclerView recycler;
    // ImageView and TextView to show when no data is available
    private ImageView noDataImage;
    private TextView noDataText;
    // Flag to rack if data has been loaded
    private boolean dataLoaded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users_list);

        recycler = findViewById(R.id.users_recycler);
        addUserButton = findViewById(R.id.add_user_button);
        noDataImage = findViewById(R.id.empty_imageview);
        noDataText = findViewById(R.id.no_data);
        progressBar = findViewById(R.id.progress_circular);

        // Initialize ViewModel with factory
        UsersViewModelFactory factory = new UsersViewModelFactory(getApplication());
        usersViewModel = new ViewModelProvider(this, factory).get(UsersViewModel.class);

        recycler.setLayoutManager(new GridLayoutManager(this, 1));

        // Initialize the adapter
        adapter = new UsersListAdapter(new ArrayList<>(), this, new UserActionListener() {
            @Override
            public void onEditUser(User user) {
                EditUserFragment editUserFragment = EditUserFragment.newInstance(user);
                editUserFragment.show(getSupportFragmentManager(), "editUserFragment");
            }

            @Override
            public void onDeleteUser(User user) {
                usersViewModel.deleteUser(user.getId());
            }
        });

        recycler.setAdapter(adapter);

        // Observe user data from the ViewModel
        usersViewModel.getUsersLiveData().observe(this, users -> {
            if (users != null) {
                if (!users.isEmpty()) {
                    usersList = users;
                    adapter.updateList(usersList);
                    adapter.notifyDataSetChanged();

                    // Update UI based on data availability
                    progressBar.setVisibility(View.GONE);
                    recycler.setVisibility(View.VISIBLE);
                    noDataImage.setVisibility(View.GONE);
                    noDataText.setVisibility(View.GONE);
                    // When data is successfully loaded
                    dataLoaded = true;
                } else if (dataLoaded) {
                    // No users found after loading is complete - show "No Data" view
                    recycler.setVisibility(View.GONE);
                    noDataImage.setVisibility(View.VISIBLE);
                    noDataText.setVisibility(View.VISIBLE);
                }
            }
        });

        // Load initial data
        usersViewModel.loadNextPage(0);

        // Observe operation status for showing status messages
        usersViewModel.getOperationStatus().observe(this, statusMessage -> {
            if (statusMessage != null && !statusMessage.isEmpty()) {
                usersViewModel.refreshUsers();
                adapter.notifyDataSetChanged();
                // Show a message to the user
                Snackbar.make(findViewById(android.R.id.content), statusMessage, Snackbar.LENGTH_SHORT).show();
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
                    usersViewModel.loadNextPage(offset);
                }
            }
        });

        // Set up click listener for the add user button with shared element transition
        addUserButton.setOnClickListener(v -> {
            Intent intent = new Intent(UsersListActivity.this, AddUserActivity.class);
            // Set up the transition for shared elements
            ActivityOptions options = ActivityOptions
                    .makeSceneTransitionAnimation(this, addUserButton, "transition_fab");
            // Start the add user activity
            startActivity(intent, options.toBundle());
        });

        // Handle results from EditUserFragment - update the user in list if changed
        getSupportFragmentManager().setFragmentResultListener("edit_user_request", this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
                if (result.containsKey("updated_user")) {
                    User updatedUser = (User) result.getSerializable("updated_user");
                    if (updatedUser != null) {
                        // Find the position of the updated user in the current users list
                        int position = findUserPositionById(updatedUser.getId());
                        if (position != -1) {
                            // Update the user data at the found position with the new user data
                            usersList.set(position, updatedUser);
                            // Notify the adapter that the item at this position has changed
                            adapter.notifyItemChanged(position);
                        }
                    }
                }
            }
        });
    }

    // Helper method to find the position of the updated user
    private int findUserPositionById(int userId) {
        for (int i = 0; i < usersList.size(); i++) {
            if (usersList.get(i).getId() == userId) {
                return i;
            }
        }
        return -1;
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh users when the activity resumes
        usersViewModel.refreshUsers();
        adapter.notifyDataSetChanged();
    }
}
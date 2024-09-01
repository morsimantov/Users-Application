package com.example.myusersapplication.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.lifecycle.ViewModelProvider;

import com.example.myusersapplication.R;
import com.example.myusersapplication.UserActionListener;
import com.example.myusersapplication.fragments.EditUserFragment;
import com.example.myusersapplication.models.User;
import com.example.myusersapplication.mvvm.UsersViewModel;
import com.example.myusersapplication.mvvm.UsersViewModelFactory;
import com.example.myusersapplication.utils.ImageUtils;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class UserDetailsActivity extends AppCompatActivity implements UserActionListener {

    private UsersViewModel usersViewModel;
    private User user;
    TextView nameTitle;
    TextView firstName;
    TextView lastName;
    TextView email;
    ImageView avatarImg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_details);

        nameTitle = findViewById(R.id.name_title);
        firstName = findViewById(R.id.first_name);
        lastName = findViewById(R.id.last_name);
        email = findViewById(R.id.email);
        avatarImg = findViewById(R.id.avatar_img);
        ViewCompat.setTransitionName(avatarImg, "transition_img");
        FloatingActionButton editButton = findViewById(R.id.edit_button);

        // Retrieve the User object passed via the Intent
        user = (User) getIntent().getSerializableExtra("user");

        // Initialize ViewModel with factory
        UsersViewModelFactory factory = new UsersViewModelFactory(getApplication());
        usersViewModel = new ViewModelProvider(this, factory).get(UsersViewModel.class);

        // Update UI with user details
        updateUI(user);

        // Set up action bar with back button
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        // Set up the edit button with the interface method
        editButton.setOnClickListener(view -> onEditUser(user));

        // Handle fragment results for updated user
        getSupportFragmentManager().setFragmentResultListener("edit_user_request", this, (requestKey, result) -> {
            if (result.containsKey("updated_user")) {
                User updatedUser = (User) result.getSerializable("updated_user");
                updateUI(updatedUser);

                // Notify the users list activity about the change
                Intent returnIntent = new Intent();
                returnIntent.putExtra("updated_user", updatedUser);
                setResult(Activity.RESULT_OK, returnIntent);
            }
        });
    }

    @Override
    public void onEditUser(User user) {
        EditUserFragment editUserFragment = EditUserFragment.newInstance(user);
        editUserFragment.show(getSupportFragmentManager(), "edit_user_fragment");
    }

    @Override
    public void onDeleteUser(User user) {
        new MaterialAlertDialogBuilder(this)
                .setMessage("Are you sure you want to delete?")
                .setPositiveButton("DELETE", (dialogInterface, i) -> {
                    usersViewModel.deleteUser(user.getId());

                    // Notify the activity that the user was deleted
                    Intent returnIntent = new Intent();
                    returnIntent.putExtra("isDeleted", true);
                    setResult(Activity.RESULT_OK, returnIntent);
                    finish();
                })
                .setNegativeButton("CANCEL", (dialogInterface, i) -> dialogInterface.dismiss())
                .show();
    }

    private void updateUI(User user) {
        if (user != null) {
            // Update the UI elements with user details
            nameTitle.setText(user.getFirst_name() + " " + user.getLast_name());
            firstName.setText(user.getFirst_name());
            lastName.setText(user.getLast_name());
            email.setText(user.getEmail());

            // Load avatar image
            ImageUtils.loadImage(avatarImg, user.getAvatar());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.more_vertical, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.delete_user:
                onDeleteUser(user);
                return true;
            case android.R.id.home:
                // Finish activity with transition when back button is pressed
                finishAfterTransition();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

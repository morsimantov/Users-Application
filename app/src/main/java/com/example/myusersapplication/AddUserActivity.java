package com.example.myusersapplication;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.myusersapplication.mvvm.UsersViewModel;
import com.example.myusersapplication.mvvm.UsersViewModelFactory;
import com.example.myusersapplication.utils.ImageUtils;
import com.google.android.material.snackbar.Snackbar;

public class AddUserActivity extends AppCompatActivity {

    private ImageView avatarImageView;
    private EditText firstNameInput;
    private EditText lastNameInput;
    private EditText emailInput;
    private UsersViewModel usersViewModel;
    private String avatarFilePath;  // Path to the saved avatar image

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_user);

        // Initialize views
        firstNameInput = findViewById(R.id.first_name);
        lastNameInput = findViewById(R.id.last_name);
        emailInput = findViewById(R.id.email);
        Button uploadButton = findViewById(R.id.upload_button);

        Button saveButton = findViewById(R.id.saveButton);
        Button cancelButton = findViewById(R.id.cancelButton);
        avatarImageView = findViewById(R.id.input_avatar);

        UsersViewModelFactory factory = new UsersViewModelFactory(getApplication());
        usersViewModel = new ViewModelProvider(this, factory).get(UsersViewModel.class);

        // Define the click listener to open the image chooser
        View.OnClickListener openImageChooserListener = v -> ImageUtils.openImageChooser(AddUserActivity.this);

        addClickAnimation(avatarImageView);

        // Trigger image picker
        // Set the same click listener for both the button and the image view
        uploadButton.setOnClickListener(openImageChooserListener);

        // calling the action bar
        ActionBar actionBar = getSupportActionBar();

        // showing the back button in action bar
        actionBar.setDisplayHomeAsUpEnabled(true);

        // Observe operationStatus
        usersViewModel.getOperationStatus().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String statusMessage) {
                if (statusMessage != null && !statusMessage.isEmpty()) {
                    // Show a message to the user
                    // Log result to verify
                    Log.d("AddUserActivity", "Operation Status: " + statusMessage);
                    Snackbar.make(findViewById(android.R.id.content), statusMessage, Snackbar.LENGTH_SHORT).show();
                    // Finish the activity on success
                    if (statusMessage.equals("User created successfully")) {
                        finish();
                    }
                }
            }
        });

        // Save button logic
        saveButton.setOnClickListener(v -> {
            if (validateInputs()) {
                String email = emailInput.getText().toString();
                String firstName = firstNameInput.getText().toString();
                String lastName = lastNameInput.getText().toString();
                // Add the user through the ViewModel
                usersViewModel.insertUser(email, firstName, lastName, avatarFilePath);
            }
        });

        // Cancel button logic
        cancelButton.setOnClickListener(v -> finishAfterTransition());
    }

    private boolean validateInputs() {
        if (firstNameInput.getText().toString().trim().isEmpty()) {
            firstNameInput.setError("First name is required");
            return false;
        }
        if (lastNameInput.getText().toString().trim().isEmpty()) {
            lastNameInput.setError("Last name is required");
            return false;
        }
        if (emailInput.getText().toString().trim().isEmpty()) {
            emailInput.setError("Email is required");
            return false;
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        avatarFilePath = ImageUtils.handleImageChooserResult(requestCode, resultCode, data, avatarImageView, this);
    }

    // this event will enable the back function to the button on press
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void addClickAnimation(View view) {
        view.setOnClickListener(v -> {
            // Simple click animation
            view.animate().scaleX(0.9f).scaleY(0.9f).setDuration(200).withEndAction(() -> {
                view.animate().scaleX(1.0f).scaleY(1.0f).setDuration(200).start();
                ImageUtils.openImageChooser(AddUserActivity.this);
            }).start();
        });
    }
}
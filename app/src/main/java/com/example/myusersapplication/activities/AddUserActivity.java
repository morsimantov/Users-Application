package com.example.myusersapplication.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.myusersapplication.R;
import com.example.myusersapplication.mvvm.UsersViewModel;
import com.example.myusersapplication.mvvm.UsersViewModelFactory;
import com.example.myusersapplication.utils.AnimationUtils;
import com.example.myusersapplication.utils.ImageUtils;
import com.google.android.material.snackbar.Snackbar;

public class AddUserActivity extends AppCompatActivity {

    private ImageView avatarImageView;
    private EditText firstNameInput;
    private EditText lastNameInput;
    private EditText emailInput;
    private UsersViewModel usersViewModel;
    // Path to the saved avatar image
    private String avatarFilePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_user);

        // Initialize views
        firstNameInput = findViewById(R.id.first_name);
        lastNameInput = findViewById(R.id.last_name);
        emailInput = findViewById(R.id.email);
        avatarImageView = findViewById(R.id.input_avatar);

        Button uploadButton = findViewById(R.id.upload_button);
        Button saveButton = findViewById(R.id.save_button);
        Button cancelButton = findViewById(R.id.cancel_button);

        // Initialize ViewModel with factory
        UsersViewModelFactory factory = new UsersViewModelFactory(getApplication());
        usersViewModel = new ViewModelProvider(this, factory).get(UsersViewModel.class);

        // Trigger image picker
        uploadButton.setOnClickListener(v -> ImageUtils.openImageChooser(this));
        // Add click animation to the avatar ImageView
        AnimationUtils.addClickAnimation(avatarImageView, () -> ImageUtils.openImageChooser(AddUserActivity.this));

        // Set up the action bar and enable the back button
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        // Observe operationStatus
        usersViewModel.getOperationStatus().observe(this, statusMessage -> {
            if (statusMessage != null && !statusMessage.isEmpty()) {
                // Show a message to the user
                Snackbar.make(findViewById(android.R.id.content), statusMessage, Snackbar.LENGTH_SHORT).show();
                // Finish the activity if user creation is successful
                if (statusMessage.equals("User created successfully")) {
                    finish();
                }
            }
        });

        // Save button logic: Validate inputs and insert user if valid
        saveButton.setOnClickListener(v -> {
            // If the inputs are valid
            if (validateInputs()) {
                String email = emailInput.getText().toString();
                String firstName = firstNameInput.getText().toString();
                String lastName = lastNameInput.getText().toString();
                // Add the user through the ViewModel
                usersViewModel.insertUser(email, firstName, lastName, avatarFilePath);
            }
        });

        // Cancel button logic: Finish the activity with transition
        cancelButton.setOnClickListener(v -> finishAfterTransition());
    }

    // Validate user inputs
    private boolean validateInputs() {
        boolean isValid = true;

        if (firstNameInput.getText().toString().trim().isEmpty()) {
            firstNameInput.setError("First name is required");
            isValid = false;
        }
        if (lastNameInput.getText().toString().trim().isEmpty()) {
            lastNameInput.setError("Last name is required");
            isValid = false;
        }
        if (emailInput.getText().toString().trim().isEmpty()) {
            emailInput.setError("Email is required");
            isValid = false;
        }
        return isValid;
    }

    // Handle results from image picker
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Process the result of the image picker
        avatarFilePath = ImageUtils.handleImageChooserResult(requestCode, resultCode, data, avatarImageView, this);
    }

    // Handle back button in the action bar
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // Finish activity with transition when back button is pressed
                this.finishAfterTransition();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
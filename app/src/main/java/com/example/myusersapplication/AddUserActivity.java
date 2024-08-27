package com.example.myusersapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.Nullable;
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
        Button avatarButton = findViewById(R.id.uploadButton);
        Button saveButton = findViewById(R.id.saveButton);
        Button cancelButton = findViewById(R.id.cancelButton);
        avatarImageView = findViewById(R.id.input_avatar);

        UsersViewModelFactory factory = new UsersViewModelFactory(getApplication());
        usersViewModel = new ViewModelProvider(this, factory).get(UsersViewModel.class);

        // Trigger image picker
        avatarButton.setOnClickListener(v -> ImageUtils.openImageChooser(this));

        // Observe operationStatus
        usersViewModel.getOperationStatus().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String statusMessage) {
                if (statusMessage != null && !statusMessage.isEmpty()) {
                    // Show a message to the user
                    // Log result to verify
                    Log.d("AddUserActivity", "Operation Status: " + statusMessage);

                    // Show Snackbar message
                    Snackbar.make(findViewById(android.R.id.content), statusMessage, Snackbar.LENGTH_SHORT).show(); // Changed to a specific view ID

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
}
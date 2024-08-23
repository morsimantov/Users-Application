package com.example.myusersapplication;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.myusersapplication.mvvm.UsersViewModel;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class AddUserActivity extends AppCompatActivity {

    private static final int REQUEST_IMAGE_PICK = 1;
    private static final int REQUEST_IMAGE_CAPTURE = 2;
    private static final int REQUEST_PERMISSION_CAMERA = 3;
    private static final int REQUEST_PERMISSION_STORAGE = 4;

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
        firstNameInput = findViewById(R.id.firstName);
        lastNameInput = findViewById(R.id.lastName);
        emailInput = findViewById(R.id.email);
        Button avatarButton = findViewById(R.id.uploadButton);
        Button saveButton = findViewById(R.id.saveButton);
        Button cancelButton = findViewById(R.id.cancelButton);
        avatarImageView = findViewById(R.id.input_avatar);

        usersViewModel = new ViewModelProvider(this).get(UsersViewModel.class);

        // Implement the avatar upload logic
        avatarButton.setOnClickListener(v -> {
            // Check for camera and storage permissions
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                // Request permissions if not granted
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE},
                        REQUEST_PERMISSION_CAMERA);
            } else {
                // Open the image picker
                Intent pickPhoto = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(pickPhoto, REQUEST_IMAGE_PICK);
            }
        });

        // Save button logic
        saveButton.setOnClickListener(v -> {
            if (validateInputs()) {
                String email = emailInput.getText().toString();
                String firstName = firstNameInput.getText().toString();
                String lastName = lastNameInput.getText().toString();

                // Add the user through the ViewModel
                usersViewModel.addUser(email, firstName, lastName, avatarFilePath);

                // Observe the result and provide feedback
                usersViewModel.getResultMessage().observe(this, result -> {
                    Toast.makeText(AddUserActivity.this, result, Toast.LENGTH_SHORT).show();
                    if (result.equals("User created successfully")) {
                        finish(); // Close the activity on success
                    }
                });
            }
        });

        // Cancel button logic
        cancelButton.setOnClickListener(v -> finish());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            switch (requestCode) {
                case REQUEST_IMAGE_PICK:
                    Uri selectedImage = data.getData();
                    avatarImageView.setImageURI(selectedImage);
                    // Save the image to the app's directory and get the file path
                    avatarFilePath = saveImageToInternalStorage(selectedImage);
                    break;
            }
        }
    }

    private boolean validateInputs() {
        if (emailInput.getText().toString().trim().isEmpty()) {
            emailInput.setError("Email is required");
            return false;
        }
        if (firstNameInput.getText().toString().trim().isEmpty()) {
            firstNameInput.setError("First name is required");
            return false;
        }
        if (lastNameInput.getText().toString().trim().isEmpty()) {
            lastNameInput.setError("Last name is required");
            return false;
        }
        return true;
    }

    private String saveImageToInternalStorage(Uri imageUri) {
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
            File filesDir = getFilesDir();
            File imageFile = new File(filesDir, "avatar_" + System.currentTimeMillis() + ".png");

            try (FileOutputStream fos = new FileOutputStream(imageFile)) {
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            }

            return imageFile.getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}

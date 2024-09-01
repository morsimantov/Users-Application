package com.example.myusersapplication.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.myusersapplication.R;
import com.example.myusersapplication.models.User;
import com.example.myusersapplication.mvvm.UsersViewModel;
import com.example.myusersapplication.mvvm.UsersViewModelFactory;
import com.example.myusersapplication.utils.AnimationUtils;
import com.example.myusersapplication.utils.ImageUtils;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class EditUserFragment extends DialogFragment {

    private static final String ARG_USER = "user";
    private UsersViewModel usersViewModel;
    private TextInputEditText firstNameInput;
    private TextInputEditText lastNameInput;
    private TextInputEditText emailInput;
    private ImageView avatarImageView;
    // Path to the selected avatar image
    private String avatarFilePath;

    // Creates a new instance of the fragment with the user data
    public static EditUserFragment newInstance(User user) {
        EditUserFragment fragment = new EditUserFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_USER, user);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_edit_user, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextInputLayout firstNameLayout = view.findViewById(R.id.outlined_first_name);
        TextInputLayout lastNameLayout = view.findViewById(R.id.outlined_last_name);
        TextInputLayout emailLayout = view.findViewById(R.id.outlined_email);
        avatarImageView = view.findViewById(R.id.avatar_img);

        firstNameInput = (TextInputEditText) firstNameLayout.getEditText();
        lastNameInput = (TextInputEditText) lastNameLayout.getEditText();
        emailInput = (TextInputEditText) emailLayout.getEditText();

        // Initialize ViewModel with factory
        UsersViewModelFactory factory = new UsersViewModelFactory(requireActivity().getApplication());
        usersViewModel = new ViewModelProvider(this, factory).get(UsersViewModel.class);

        Button cancelButton = view.findViewById(R.id.cancel_button);
        ImageButton closeButton = view.findViewById(R.id.close_button);
        Button avatarUploadButton = view.findViewById(R.id.button_upload_avatar);
        Button saveButton = view.findViewById(R.id.save_button);

        // Get the User object from the arguments
        User user = (User) getArguments().getSerializable(ARG_USER);

        // Set click animations for views
        AnimationUtils.addClickAnimation(avatarImageView, () -> ImageUtils.openImageChooser(EditUserFragment.this));
        AnimationUtils.addClickAnimation(avatarUploadButton, () -> ImageUtils.openImageChooser(EditUserFragment.this));

        if (user != null) {
            // Populate the fields with user data
            firstNameInput.setText(user.getFirst_name());
            lastNameInput.setText(user.getLast_name());
            emailInput.setText(user.getEmail());

            // Load avatar image
            ImageUtils.loadImage(avatarImageView, user.getAvatar());
        }

        saveButton.setOnClickListener(v -> {
            // Safely retrieve and trim text from input fields
            String firstName = firstNameInput != null && firstNameInput.getText() != null
                    ? firstNameInput.getText().toString().trim()
                    : "";
            String lastName = lastNameInput != null && lastNameInput.getText() != null
                    ? lastNameInput.getText().toString().trim()
                    : "";
            String email = emailInput != null && emailInput.getText() != null
                    ? emailInput.getText().toString().trim()
                    : "";
            String avatarImage = avatarFilePath != null ? avatarFilePath : (user != null ? user.getAvatar() : "");

            if (user != null && validateInputs()) {
                // Update user details
                user.setFirst_name(firstName);
                user.setLast_name(lastName);
                user.setEmail(email);
                user.setAvatar(avatarImage);
                usersViewModel.updateUser(user.getId(), email, firstName, lastName, avatarImage);

                // Observe the operation status and handle it appropriately
                usersViewModel.getOperationStatus().observe(getViewLifecycleOwner(), result -> {
                    if ("User updated successfully".equals(result)) {
                        Bundle resultBundle = new Bundle();
                        resultBundle.putSerializable("updated_user", user);
                        getParentFragmentManager().setFragmentResult("edit_user_request", resultBundle);
                        dismiss();
                    } else {
                        Snackbar.make(view, result, Snackbar.LENGTH_SHORT).show();
                    }
                });
            } else {
                Log.e("EditUserFragment", "User is null or validation failed");
            }
        });

        // Cancel button logic
        cancelButton.setOnClickListener(v -> dismiss());
        closeButton.setOnClickListener(v -> dismiss());
    }

    // Validate user inputs
    private boolean validateInputs() {
        String firstName = firstNameInput.getText() != null ? firstNameInput.getText().toString().trim() : "";
        String lastName = lastNameInput.getText() != null ? lastNameInput.getText().toString().trim() : "";
        String email = emailInput.getText() != null ? emailInput.getText().toString().trim() : "";

        boolean isValid = true;

        if (firstName.isEmpty()) {
            firstNameInput.setError("First name is required");
            isValid = false;
        }
        if (lastName.isEmpty()) {
            lastNameInput.setError("Last name is required");
            isValid = false;
        }
        if (email.isEmpty()) {
            emailInput.setError("Email is required");
            isValid = false;
        }

        return isValid;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Handle image chooser result
        avatarFilePath = ImageUtils.handleImageChooserResult(requestCode, resultCode, data, avatarImageView, requireActivity());
    }
}
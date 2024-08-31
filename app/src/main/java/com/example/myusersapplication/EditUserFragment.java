package com.example.myusersapplication;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
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
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.myusersapplication.models.User;
import com.example.myusersapplication.mvvm.UsersViewModel;
import com.example.myusersapplication.mvvm.UsersViewModelFactory;
import com.example.myusersapplication.utils.ImageUtils;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.squareup.picasso.Picasso;

public class EditUserFragment extends DialogFragment {

    private static final String ARG_USER = "user";
    private UsersViewModel usersViewModel;
    private TextInputEditText firstNameInput;
    private TextInputEditText lastNameInput;
    private TextInputEditText emailInput;
    private ImageView avatarImageView;

    private String avatarFilePath;

    public static EditUserFragment newInstance(User user) {
        EditUserFragment fragment = new EditUserFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_USER, user); // Pass the full user object
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
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

        Button cancelButton = view.findViewById(R.id.cancelButton);
        ImageButton closeButton = view.findViewById(R.id.close_button);
        Button avatarUploadButton = view.findViewById(R.id.button_upload_avatar);

        // Get the User object from the arguments
        User user = (User) getArguments().getSerializable(ARG_USER);
        // Trigger image picker
        addClickAnimation(avatarImageView);
        addClickAnimation(avatarUploadButton);

        if (user != null) {
            // Populate the fields with user data
            firstNameInput.setText(user.getFirst_name());
            lastNameInput.setText(user.getLast_name());
            emailInput.setText(user.getEmail());

            String urlImg = user.getAvatar();
            // Check if urlImg is null or empty
            if (urlImg != null && !urlImg.isEmpty()) {
                if (urlImg.startsWith("https")) {
                    Picasso.get()
                            .load(urlImg)
                            .placeholder(R.drawable.not_available) // Default drawable resource
                            .into(avatarImageView);
                } else {
                    avatarImageView.setImageURI(Uri.parse(urlImg));
                }
            } else {
                // Set a default image if urlImg is null or empty
                avatarImageView.setImageResource(R.drawable.not_available);
            }

        }

        usersViewModel.getOperationStatus().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String result) {
                if (result != null) {
                    // Log result to verify
                    Log.d("EditUserFragment", "Operation Status: " + result);
                    if (result.equals("User updated successfully")) {
                        dismiss(); // Close the dialog on success
                    }
                }
            }
        });

        Button saveButton = view.findViewById(R.id.saveButton);
        // Handle save button
        saveButton.setOnClickListener(v -> {
            String firstName = firstNameInput != null ? firstNameInput.getText().toString().trim() : "";
            String lastName = lastNameInput != null ? lastNameInput.getText().toString().trim() : "";
            String email = emailInput != null ? emailInput.getText().toString().trim() : "";

            if (validateInputs()) {
                user.setFirst_name(firstName);
                user.setLast_name(lastName);
                user.setEmail(email);
                if (avatarFilePath != null) {
                    user.setAvatar(avatarFilePath);
                }
                usersViewModel.updateUser(user.getId(), email, firstName, lastName, user.getAvatar());

                // Collect the updated user data and create a result bundle
                Bundle result = new Bundle();
                result.putSerializable("updated_user", user);

                // Send the result to the activity
                getParentFragmentManager().setFragmentResult("edit_user_request", result);
                dismiss(); // Close the dialog on success
            }
        });

        // Cancel button logic
        cancelButton.setOnClickListener(v -> dismiss());
        closeButton.setOnClickListener(v -> dismiss());
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
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        avatarFilePath = ImageUtils.handleImageChooserResult(requestCode, resultCode, data, avatarImageView, requireActivity());
    }

    private void addClickAnimation(View view) {
        view.setOnClickListener(v -> {
            // Simple click animation
            view.animate().scaleX(0.9f).scaleY(0.9f).setDuration(200).withEndAction(() -> {
                view.animate().scaleX(1.0f).scaleY(1.0f).setDuration(200).start();
                ImageUtils.openImageChooser(EditUserFragment.this);
            }).start();
        });
    }
}
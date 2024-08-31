package com.example.myusersapplication;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.ActivityOptions;
import android.app.Application;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myusersapplication.models.User;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.squareup.picasso.Picasso;

import java.util.List;

public class UsersListAdapter extends RecyclerView.Adapter<UsersViewHolder> {

    private List<User> usersList;
    private final FragmentActivity activity;  // Use FragmentActivity for access to FragmentManager
    private final UserActionListener actionListener;

    public UsersListAdapter(List<User> usersList, FragmentActivity activity, Application application, UserActionListener actionListener) {
        this.usersList = usersList;
        this.activity = activity;
        this.actionListener = actionListener;
    }

    @NonNull
    @Override
    public UsersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_list_item, parent, false);
        return new UsersViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UsersViewHolder holder, int position) {
        User user = usersList.get(position);
        holder.userName.setText(usersList.get(position).getFirst_name() + " " + usersList.get(position).getLast_name());
        holder.userEmail.setText(usersList.get(position).getEmail());
        String urlImg = usersList.get(position).getAvatar();
        // Check if urlImg is null or empty
        if (urlImg != null && !urlImg.isEmpty()) {
            if (urlImg.startsWith("https")) {
                Picasso.get()
                        .load(urlImg)
                        .placeholder(R.drawable.not_available) // Default drawable resource
                        .into(holder.avatarImg);
            } else {
                holder.avatarImg.setImageURI(Uri.parse(urlImg));
            }
        } else {
            // Set a default image if urlImg is null or empty
            holder.avatarImg.setImageResource(R.drawable.not_available);
        }

        // Handle edit button click
        holder.editButton.setOnClickListener(v -> {
            if (actionListener != null) {
                actionListener.onEditUser(user);
            }
        });

        holder.deleteButton.setOnClickListener(v -> {
            new MaterialAlertDialogBuilder(activity)
                    .setMessage("Are you sure you want to delete?")
                    .setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            // Start the fade-out animation
                            ViewPropertyAnimator animator = holder.itemView.animate();
                            animator.alpha(0f).setDuration(200).setListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    // Remove the user from the list and notify the adapter
                                    int position = holder.getAdapterPosition();
                                    usersList.remove(position);
                                    notifyItemRemoved(position);
                                    if (actionListener != null) {
                                        actionListener.onDeleteUser(user);
                                    }
                                }
                            }).start();
                        }
                    })
                    .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            // Close the dialog without doing anything
                            dialogInterface.dismiss();
                        }
                    })
                    .show();
        });


        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int adapterPosition = holder.getAdapterPosition();
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    // Get the selected user
                    User selectedUser = usersList.get(adapterPosition);

                    // Create an Intent to start UserDetailsActivity
                    Intent intent = new Intent(activity, UserDetailsActivity.class);

                    // Pass the selected user's details to the UserDetailsActivity
                    intent.putExtra("user", selectedUser);

                    // Set up the transition for shared elements
                    // Set up the transition for shared elements
                    ActivityOptions options = ActivityOptions
                            .makeSceneTransitionAnimation(activity, holder.avatarImg, "transition_img");

                    // Start the UserDetailsActivity
                    activity.startActivity(intent, options.toBundle());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        if (usersList == null) {
            return 0;
        }
        return usersList.size();
    }

    public void updateList(List<User> users) {
        this.usersList = users;
        notifyDataSetChanged();
    }
}
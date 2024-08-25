package com.example.myusersapplication;

import android.app.Application;
import android.content.DialogInterface;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myusersapplication.models.User;
import com.example.myusersapplication.mvvm.UsersViewModel;
import com.example.myusersapplication.mvvm.UsersViewModelFactory;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.squareup.picasso.Picasso;

import java.util.List;

public class UsersListAdapter extends RecyclerView.Adapter<UsersViewHolder> {

    private List<User> usersList;
    //    private SelectUserListener listener;
    private final FragmentActivity activity;  // Use FragmentActivity for access to FragmentManager
    private UsersViewModel usersViewModel;


    public UsersListAdapter(List<User> usersList, FragmentActivity activity, Application application) {
        this.usersList = usersList;
        this.activity = activity;
        // Use the Application context to create the UsersViewModelFactory
        UsersViewModelFactory factory = new UsersViewModelFactory(application);
        usersViewModel = new ViewModelProvider(activity, factory).get(UsersViewModel.class);
//        this.listener = listener;
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


        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int adapterPosition = holder.getAdapterPosition();
                if (adapterPosition != RecyclerView.NO_POSITION) {
//                    listener.onSelectUser(usersList.get(adapterPosition));
                }
            }
        });

        // Handle edit button click
        holder.editButton.setOnClickListener(v -> {
            EditUserFragment editUserFragment = EditUserFragment.newInstance(user);
            editUserFragment.show(activity.getSupportFragmentManager(), "editUserFragment");
        });

        holder.deleteButton.setOnClickListener(v -> {
            new MaterialAlertDialogBuilder(activity)
                    .setMessage("Are you sure you want to delete?")
                    .setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            // Call the deleteUser method from the ViewModel
                            usersViewModel.deleteUser(user.getId());
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
    }

    @Override
    public int getItemCount() {
        if (usersList == null) {
            return 0;
        }
        return usersList.size();
    }

    public void updateList(List<User> users) {
        usersList = users;
        notifyDataSetChanged();
    }
}
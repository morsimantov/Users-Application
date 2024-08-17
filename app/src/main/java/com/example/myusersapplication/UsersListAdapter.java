package com.example.myusersapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myusersapplication.models.User;
import com.squareup.picasso.Picasso;

import java.util.List;

public class UsersListAdapter extends RecyclerView.Adapter<UsersViewHolder> {

    private List<User> usersList;
    private Context context;
//    private SelectUserListener listener;

    public UsersListAdapter(List<User> usersList, Context context) {
        this.usersList = usersList;
        this.context = context;
//        this.listener = listener;
    }

    @NonNull
    @Override
    public UsersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new UsersViewHolder(LayoutInflater.from(context).inflate(R.layout.user_list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull UsersViewHolder holder, int position) {
        holder.userName.setText(usersList.get(position).getFirst_name() + " " + usersList.get(position).getLast_name());
        holder.userEmail.setText(usersList.get(position).getEmail());
        String urlImg = usersList.get(position).getAvatar();
        if (urlImg != null) {
            Picasso.get().load(urlImg).into(holder.avatarImg);
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
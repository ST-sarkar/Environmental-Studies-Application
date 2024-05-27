package com.example.environmentalstudies.User.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.environmentalstudies.Admin.Model.UserProfile;
import com.example.environmentalstudies.R;

import java.util.ArrayList;
import java.util.List;

public class SeeUserAdapter extends RecyclerView.Adapter<SeeUserAdapter.UserViewHolder> {
    List<UserProfile> userList=new ArrayList<>();

    public SeeUserAdapter(List<UserProfile> userList) {
        this.userList = userList;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.see_user_item,parent,false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {

        UserProfile userProfile=userList.get(position);
        holder.email.setText(userProfile.getUsername());
        holder.name.setText(userProfile.getName());
        holder.phno.setText(userProfile.getPhone());
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView name,phno,email;
        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            name=itemView.findViewById(R.id.tx_name);
            phno=itemView.findViewById(R.id.tx_phon_no);
            email=itemView.findViewById(R.id.tx_username);
        }
    }
}

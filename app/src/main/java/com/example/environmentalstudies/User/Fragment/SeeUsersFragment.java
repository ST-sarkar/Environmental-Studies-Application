package com.example.environmentalstudies.User.Fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.environmentalstudies.Admin.Model.UserProfile;
import com.example.environmentalstudies.R;
import com.example.environmentalstudies.User.Adapter.SeeUserAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class SeeUsersFragment extends Fragment {
    RecyclerView recyclerView;
    List<UserProfile> userList=new ArrayList<>();
    SeeUserAdapter adapter;

    public SeeUsersFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_see_users, container, false);

        recyclerView=view.findViewById(R.id.rec_see_users);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        fetchData();
        return view;
    }

    private void fetchData() {
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference();
        reference.child("UserProfile").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    for (DataSnapshot dataSnapshot:snapshot.getChildren()){
                        userList.add(dataSnapshot.getValue(UserProfile.class));
                    }
                }
                setAdapter();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void setAdapter() {

        adapter=new SeeUserAdapter(userList);
        recyclerView.setAdapter(adapter);

    }
}
package com.example.environmentalstudies.User.Fragment;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.environmentalstudies.Admin.Model.TaskUpload;
import com.example.environmentalstudies.R;
import com.example.environmentalstudies.User.Adapter.TaskAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.util.ArrayList;
import java.util.List;


public class ClassWorkFrag extends Fragment {


    ProgressDialog progressDialog;
    RecyclerView recyclerView;

    List<String> taskId = new ArrayList<>();
    List<TaskUpload> taskList = new ArrayList<>();
    List<String> TaskId = new ArrayList<>();
    List<TaskUpload> TaskList = new ArrayList<>();
    TaskAdapter adapter;
    FragmentManager fragmentManager;
    String user="student";


    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_class_work, container, false);

        recyclerView = view.findViewById(R.id.task_recview);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Loading tasks...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        fragmentManager=getParentFragmentManager();

        fetchTask();


        return view;
    }

    private void fetchTask() {
        taskId.clear();
        taskList.clear();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child("Tasks").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                progressDialog.dismiss();
                if (snapshot.exists()) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {


                        taskId.add(dataSnapshot.getKey());
                        //Toast.makeText(getContext(), "keyin="+id, Toast.LENGTH_LONG).show();
                        TaskUpload upload = dataSnapshot.getValue(TaskUpload.class);
                        taskList.add(upload);

                    }
                   // Toast.makeText(getContext(), "keyin="+taskId.get(0), Toast.LENGTH_LONG).show();
                    setupAdapter();


                } else {
                    Toast.makeText(getContext(), "No tasks found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressDialog.dismiss();
                Toast.makeText(getContext(), "Failed to fetch tasks", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupAdapter() {
       if (fragmentManager!=null && taskId.size()>0) {
            adapter = new TaskAdapter(taskList, taskId, fragmentManager,user);
            recyclerView.setAdapter(adapter);
        }else {
            Toast.makeText(getContext(), "Failed to fetch tasks", Toast.LENGTH_SHORT).show();
        }
    }
}

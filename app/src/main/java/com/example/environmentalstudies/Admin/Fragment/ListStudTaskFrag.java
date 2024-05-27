package com.example.environmentalstudies.Admin.Fragment;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.environmentalstudies.Admin.Adapter.TaskListStudentAdapter;
import com.example.environmentalstudies.Admin.Model.TaskUpload;
import com.example.environmentalstudies.Admin.Model.UserProfile;
import com.example.environmentalstudies.R;
import com.example.environmentalstudies.User.Adapter.TaskAdapter;
import com.example.environmentalstudies.User.Model.TaskSubModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class ListStudTaskFrag extends Fragment {
    RecyclerView recyclerView;
    ProgressDialog progressDialog;
    List<String> taskId = new ArrayList<>();
    List<String> studList = new ArrayList<>();
    List<TaskSubModel> taskList = new ArrayList<>();
    TaskListStudentAdapter adapter;
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    FragmentManager fragmentManager;

    public ListStudTaskFrag() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list_stud_task, container, false);

        progressDialog = new ProgressDialog(getContext());
        progressDialog.setTitle("Loading..");
        progressDialog.show();
        fragmentManager = getParentFragmentManager();

        recyclerView = view.findViewById(R.id.rec_stud);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        setupAdapter();

        fetchStudentData();


        return view;
    }

    private void fetchStudentData() {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        //List<String> stList=new ArrayList<>();
        taskId.clear();
        taskList.clear();
        databaseReference.child("submissions").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        String studUid = dataSnapshot.getKey();
                        // Add student UID to taskId list

                        //fetchStudentName(studUid);

                        taskId.add(studUid);
                        TaskSubModel task = dataSnapshot.getValue(TaskSubModel.class);
                        taskList.add(task);
                        //adapter.notifyDataSetChanged();

                    }
                    // Check if studList size matches taskId size before setting up adapter
                   /* if (studList.size() == taskId.size()) {
                        setupAdapter();
                    }else{
                        Toast.makeText(getContext(), "list size="+studList.size()+" "+taskList.size(), Toast.LENGTH_SHORT).show();
                    }

                    */

                    fetchStudentName();
                } else {
                    progressDialog.dismiss();
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

    private void fetchStudentName() {

        // Fetch student name and add it to studList
        studList.clear();
        for(String uid:taskId) {
            databaseReference.child("UserProfile").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        UserProfile userProfile = dataSnapshot.getValue(UserProfile.class);
                        String name = userProfile.getName();
                        studList.add(name); // Add student name to studList

                        //Toast.makeText(getContext(), "name="+name, Toast.LENGTH_SHORT).show();
                        if (studList.size() == taskId.size()) {
                            //setupAdapter();
                            adapter.notifyDataSetChanged();
                        }

                    } else {
                        Toast.makeText(getContext(), "datasnapshot not exist", Toast.LENGTH_SHORT).show();
                    }
                    //studList.addAll(stList);
                    progressDialog.dismiss();

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    progressDialog.dismiss();
                    Toast.makeText(getContext(), "Failed to fetch student name", Toast.LENGTH_SHORT).show();
                }
            });

        }

    }

    private void setupAdapter() {
        // if (!taskId.isEmpty() && taskId.size() == studList.size()) {
        //Toast.makeText(getContext(), "set adapter data list empty", Toast.LENGTH_SHORT).show();
        adapter = new TaskListStudentAdapter(taskList, taskId, studList, fragmentManager);
        recyclerView.setAdapter(adapter);
        //  } else {
        //    Toast.makeText(getContext(), "Error: Mismatch in data", Toast.LENGTH_SHORT).show();
        //}
    }
}

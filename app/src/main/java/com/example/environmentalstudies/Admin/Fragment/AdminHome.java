package com.example.environmentalstudies.Admin.Fragment;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.environmentalstudies.Admin.Model.TaskUpload;
import com.example.environmentalstudies.R;
import com.example.environmentalstudies.User.Adapter.TaskAdapter;
import com.example.environmentalstudies.User.Fragment.SeeUsersFragment;
import com.example.environmentalstudies.databinding.FragmentAdminHomeBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.imaginativeworld.whynotimagecarousel.ImageCarousel;
import org.imaginativeworld.whynotimagecarousel.model.CarouselItem;

import java.util.ArrayList;
import java.util.List;


public class AdminHome extends Fragment {

    RecyclerView recyclerView;

    ProgressDialog progressDialog;

    List<String> taskId = new ArrayList<>();
    List<TaskUpload> taskList = new ArrayList<>();
    TaskAdapter adapter;
    FragmentManager fragmentManager;
    String user="admin";
    CardView seeUser;

    FragmentAdminHomeBinding binding;
    //ImageCarousel carousel;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentAdminHomeBinding.inflate(inflater,container,false);

        View view = binding.getRoot();
        /*carousel=view.findViewById(R.id.carousel);*/

        seeUser=view.findViewById(R.id.cd_seeuser);


        progressDialog=new ProgressDialog(getContext());
        progressDialog.setTitle("Loding...");
        progressDialog.show();

        recyclerView=view.findViewById(R.id.rec_task);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        fragmentManager=getParentFragmentManager();

        seeUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SeeUsersFragment fragment=new SeeUsersFragment();
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,fragment).addToBackStack(null).commit();

            }
        });

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

    private void setupAdapter() {
        // Toast.makeText(getContext(), "keysetadapter="+id, Toast.LENGTH_SHORT).show();
        //Toast.makeText(getContext(), "keyout="+taskId.get(0), Toast.LENGTH_LONG).show();

        if (fragmentManager!=null && taskId.size()>0) {
            adapter = new TaskAdapter(taskList, taskId, fragmentManager,user);
            //RecyclerView recyclerView = findViewById(R.id.task_recview);
            recyclerView.setAdapter(adapter);
        }else {
            Toast.makeText(getContext(), "list is empty", Toast.LENGTH_SHORT).show();
        }
        progressDialog.dismiss();
    }

}
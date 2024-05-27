package com.example.environmentalstudies.User.Fragment;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.airbnb.lottie.LottieAnimationView;
import com.example.environmentalstudies.Admin.Model.TaskUpload;
import com.example.environmentalstudies.User.*;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.environmentalstudies.R;

import org.imaginativeworld.whynotimagecarousel.ImageCarousel;
import org.imaginativeworld.whynotimagecarousel.model.CarouselItem;

import java.util.ArrayList;
import java.util.List;
import androidx.appcompat.widget.SearchView;
import android.os.Bundle;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import com.example.environmentalstudies.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.imaginativeworld.whynotimagecarousel.ImageCarousel;
import org.imaginativeworld.whynotimagecarousel.model.CarouselItem;

import java.util.ArrayList;
import java.util.List;


public class UserHomeFrag extends Fragment {

    View view;
    CardView cdldboared,cdTask;
    TextView title,desc,stdate,eddate;
    ProgressDialog progressDialog;
    LottieAnimationView llt1;
    TextView tx;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.fragment_user_home, container, false);
        cdldboared=view.findViewById(R.id.cd_ldboard);
        title=view.findViewById(R.id.tx_title);
        desc=view.findViewById(R.id.tx_desc);
        stdate=view.findViewById(R.id.tx_startdate);
        eddate=view.findViewById(R.id.tx_enddate);
        llt1=view.findViewById(R.id.animationView);
        cdTask=view.findViewById(R.id.cd_task);
        tx=view.findViewById(R.id.text);
        tx.setText("No task presents");

        progressDialog=new ProgressDialog(getContext());
        progressDialog.setTitle("Loding...");

        cdldboared.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LeaderBoardFrag frag=new LeaderBoardFrag();
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,frag).addToBackStack(null).commit();
            }
        });


        fetchTasks();

        slider();

       // setupSearch();

        progressDialog.show();


        return view;
    }

    private void fetchTasks() {

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child("Tasks").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                progressDialog.dismiss();
                if (snapshot.exists()) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {

                        TaskUpload upload = dataSnapshot.getValue(TaskUpload.class);
                        llt1.setVisibility(View.GONE);
                        cdTask.setVisibility(View.VISIBLE);
                        tx.setText("Current Task");
                        title.setText(upload.getTitle());
                        desc.setText(upload.getDesc());
                        stdate.setText(upload.getPublishdate());
                        eddate.setText(upload.getDeadline());
                    }


                } else {
                    //Toast.makeText(getContext(), "No tasks found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressDialog.dismiss();
                Toast.makeText(getContext(), "Failed to fetch tasks", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void slider() {
        ImageCarousel carousel = view.findViewById(R.id.carousel);
        List<CarouselItem> list = new ArrayList<>();
        list.add(
                new CarouselItem(
                        "http://www.testproject.life/Projects/EnvStudies/image/e1.jpg",
                        ""
                )
        );
        list.add(
                new CarouselItem(
                        "http://www.testproject.life/Projects/EnvStudies/image/e2.jpg",
                        ""
                )
        );
        list.add(
                new CarouselItem(
                        "http://www.testproject.life/Projects/EnvStudies/image/e3.jpg",
                        ""
                )
        );
        list.add(
                new CarouselItem(
                        "http://www.testproject.life/Projects/EnvStudies/image/e4.jpg",
                        ""
                )
        );
        list.add(
                new CarouselItem(
                        "http://www.testproject.life/Projects/EnvStudies/image/e5.jpg",
                        ""
                )
        );
        list.add(
                new CarouselItem(
                        "http://www.testproject.life/Projects/EnvStudies/image/e6.jpg",
                        ""
                )
        );
        carousel.setData(list);
    }



}
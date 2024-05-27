package com.example.environmentalstudies.User.Fragment;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;

import com.example.environmentalstudies.Admin.Model.UserProfile;
import com.example.environmentalstudies.User.*;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Looper;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.environmentalstudies.R;
import com.example.environmentalstudies.User.Model.StudentData;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.security.cert.CertificateRevokedException;
import java.util.ArrayList;




public class Profile extends Fragment {

    ProgressDialog progressDialog;
    String uname,pword;
    TextView Name,Mobile,Address,edit_profile;
    TextView totalQ,totalCorrect,totalQuizz,progress;
    ProgressBar progressIndicator;
    CardView card_download,card_bookmark;
    DatabaseReference reference;
    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_profile, container, false);

        //TextView editProfileTextView = view.findViewById(R.id.edit_profile);
        progressDialog=new ProgressDialog(getContext());

        Name=view.findViewById(R.id.tx_profile_name);
        Mobile=view.findViewById(R.id.tx_profile_mobile);
        Address=view.findViewById(R.id.tx_profile_address);

        edit_profile=view.findViewById(R.id.edit_profile);
        totalCorrect=view.findViewById(R.id.tx_total_correct);
        totalQ=view.findViewById(R.id.tx_totalQ);
        totalQuizz=view.findViewById(R.id.tx_total_quizz);
        progress=view.findViewById(R.id.tx_progress);
        progressIndicator=view.findViewById(R.id.circular_progress);

        card_download=view.findViewById(R.id.card_download);

        card_bookmark=view.findViewById(R.id.card_bookmarks);

        try {
            StudentData studentData1 = new StudentData();
            studentData1.setLoginSharedPreferences(getContext());

            uname = studentData1.getUid();
            pword = studentData1.getPassword();

            StudentData studentData = new StudentData();
            studentData.setStudentSharedPreferences(getContext());
            totalQuizz.setText(String.valueOf(studentData.getTotalCompletedQuizz()));
            int q = studentData.getTotalQuestions();
            int cq = studentData.getCorrectQuestions();
            totalQ.setText(String.valueOf(q));
            totalCorrect.setText(String.valueOf(cq));
            int progress_percent=0;
            if(q!=0) {
                progress_percent = ((cq * 100) / q);
            }

            //Toast.makeText(getContext(), "percent :"+prog_percent+" p"+progress_percent, Toast.LENGTH_SHORT).show();
            progress.setText(getString(R.string.progress_text, progress_percent));
            progressIndicator.setProgress(progress_percent);
        }catch (Exception e){
            Toast.makeText(getContext(), ""+e, Toast.LENGTH_SHORT).show();
        }


       // Toast.makeText(getContext(), "Puname= "+uname+"Ppw= "+pword, Toast.LENGTH_LONG).show();

        //System.out.println("UserName: "+uname);
        //System.out.println("Password: "+pword);


        card_download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DownloadFrag frag=new DownloadFrag();
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,frag).addToBackStack(null).commit();
            }
        });

        card_bookmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BookFrag frag=new BookFrag();
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,frag).addToBackStack(null).commit();
            }
        });


        edit_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), EditProfileActivity.class);
                startActivity(intent);
            }
        });

       // new LoadDataTask().execute();

        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                getProfileData();
            }
        });


        progressDialog.show();

        return view;
    }



    public void getProfileData()
    {
        progressDialog.setTitle("Loading...");
        if(uname!=null) {
            reference = FirebaseDatabase.getInstance().getReference();
            reference.child("UserProfile").child(uname).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {
                    if (task.getResult().exists()) {
                        UserProfile profile = task.getResult().getValue(UserProfile.class);
                        if(profile!=null) {
                            Name.setText(profile.getName());
                            Mobile.setText(profile.getPhone());
                            Address.setText(profile.getAddress());
                            progressDialog.dismiss();
                        }
                    }else {
                        progressDialog.dismiss();
                        Toast.makeText(getContext(), "data not exist", Toast.LENGTH_SHORT).show();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                    Toast.makeText(getContext(), "error :" + e, Toast.LENGTH_SHORT).show();
                }
            });
        }else {
            progressDialog.dismiss();
            Toast.makeText(getContext(), "uname is empty", Toast.LENGTH_SHORT).show();
        }
    }


    public class LoadDataTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.setMessage("Loading...");
            progressDialog.show();
        }
        @Override
        protected Void doInBackground(Void... voids) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    getProfileData();
                }
            });
            return null;
        }
        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);
            progressDialog.dismiss();
        }
    }


}
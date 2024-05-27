package com.example.environmentalstudies.User.Fragment;

import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.environmentalstudies.R;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.apache.poi.ss.formula.functions.T;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;


public class UploadTaskFrag extends Fragment {
    private Button buttonChoose;
    private ImageView img_pdf;
    private TextView task_title;
    private TextView task_desc,remark;
    CardView remark_card;


    Uri pdfUri= Uri.parse("");
    String taskId;
    private static final int PERMISSION_REQUEST_STORAGE = 1000;
    private static final int PICK_PDF_FILE = 123;

    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    openPdfPicker();
                } else {
                    Toast.makeText(getContext(), "Permission Denied", Toast.LENGTH_SHORT).show();
                }
            });

    private final ActivityResultLauncher<String> pickPdfLauncher =
            registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
                if (uri != null) {
                    pdfUri = uri;
                    Toast.makeText(getContext(), "PDF Selected: " + pdfUri.getPath(), Toast.LENGTH_SHORT).show();
                }
            });


    public UploadTaskFrag() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_upload_task2, container, false);

        buttonChoose =view.findViewById(R.id.buttonChoose);
        Button buttonUpload = view.findViewById(R.id.buttonUpload);

        task_title= view.findViewById(R.id.tx_title);
        task_desc =view.findViewById(R.id.tx_desc);
        remark=view.findViewById(R.id.tx_remark);
        remark_card=view.findViewById(R.id.card_text_remark);
        img_pdf=view.findViewById(R.id.img_pdf);


        String deadDate="11";
        // Retrieve data passed from RecyclerView adapter
        Bundle bundle = getArguments();
        if (bundle != null) {
            String title = bundle.getString("title");
            task_title.setText(title);
            String desc=bundle.getString("desc");
            task_desc.setText(desc);
            taskId=bundle.getString("taskId");
            deadDate=bundle.getString("deadline");

        }

        // Get current date
        LocalDate currentDate = LocalDate.now();

// Format the date as a string
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d/M/yyyy");
        String curtDate = currentDate.format(formatter);


        String[] part1 = curtDate.split("/");
        long currl=Long.valueOf(part1[0]+part1[1]+part1[2]);

        String[] part2 = deadDate.split("/");
        long deadl=Long.valueOf(part2[0]+part2[1]+part2[2]);

        if(currl>=deadl){
            buttonChoose.setVisibility(View.GONE);
            img_pdf.setImageResource(R.drawable.baseline_file_upload_off);
        }



        //if(!remark.getText().toString().equals("remark not yet")) {
        remark.setText("remark not yet");
        fetchRemark();
        // }


        buttonChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //pickPdfFromStorage();
                requestPermissionAndPickPdf();

            }
        });

        buttonUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(buttonChoose.getVisibility()==View.VISIBLE) {
                    uploadPdfToFirebase(pdfUri);
                }else if(pdfUri.equals(Uri.parse(""))){
                    Toast.makeText(getContext(), "select file", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(getContext(), "Task Submitted!!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return view;
    }

    private void fetchRemark() {
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference();
        String uid=FirebaseAuth.getInstance().getCurrentUser().getUid();

        reference.child("submissions").child(taskId).child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    TaskSubModel model=snapshot.getValue(TaskSubModel.class);
                    assert model != null;
                    String rk=model.getRemark();
                    Log.e("uptask","remark="+rk);
                    if(!rk.isEmpty()){
                        remark_card.setVisibility(View.VISIBLE);
                        buttonChoose.setVisibility(View.GONE);
                        img_pdf.setImageResource(R.drawable.baseline_file_upload_off);
                        remark.setText(rk);
                    }else {
                        Toast.makeText(getContext(), "remark is empty", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        /*
        reference.child("submissions").child(taskId).child(uid).child("remark").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                Log.e("uptask","remark= on complete");
                if(task.getResult().exists()){
                    String rk=task.getResult().getValue(String.class);

                    Log.e("uptask","remark="+rk);

                    if(!rk.isEmpty()){
                        remark_card.setVisibility(View.VISIBLE);
                        remark.setText(rk);
                    }else {
                        Toast.makeText(getContext(), "remark is empty", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(), "error "+e, Toast.LENGTH_SHORT).show();
            }
        });

         */
    }

    private void requestPermissionAndPickPdf() {
        Log.d("UploadTaskFrag", "Requesting storage permission");
        requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
    }

    private void openPdfPicker() {
        pickPdfLauncher.launch("application/pdf");
    }


    private void uploadPdfToFirebase(Uri pdfUri) {
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        StorageReference pdfRef = storageRef.child("submissions/" + System.currentTimeMillis() + "_homework.pdf");

        pdfRef.putFile(pdfUri).addOnSuccessListener(taskSnapshot -> {
            pdfRef.getDownloadUrl().addOnSuccessListener(uri -> {
                String fileUrl = uri.toString();
                // Save submission details in Firestore/Realtime Database
                String uid= FirebaseAuth.getInstance().getCurrentUser().getUid();
                String title=task_title.getText().toString();
                if (!title.isEmpty()) {

                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

                    LocalDate today = LocalDate.now();
                    // Format the date as a String (e.g., "2024-03-16")
                    String formattedDate = today.format(DateTimeFormatter.ISO_LOCAL_DATE);

                    TaskSubModel subModel=new TaskSubModel(formattedDate,fileUrl,"");
                    reference.child("submissions").child(taskId).child(uid).setValue(subModel).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(getContext(), "Task uploaded successfully", Toast.LENGTH_SHORT).show();
                            buttonChoose.setVisibility(View.GONE);
                            img_pdf.setImageResource(R.drawable.baseline_file_upload_off);
                            remark_card.setVisibility(View.VISIBLE);
                            remark.setText("remark not yet");

                            //buttonUpload.setVisibility(View.GONE);

                        }
                    });
                }
            });
        }).addOnFailureListener(e -> {
            // Handle unsuccessful uploads
            Toast.makeText(getContext(), "fail to upload", Toast.LENGTH_SHORT).show();
        });
    }

    private void choosePdf() {
        Toast.makeText(getContext(), "choose pdf", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("application/pdf");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(Intent.createChooser(intent, "Select PDF"), PICK_PDF_FILE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_PDF_FILE && resultCode == RESULT_OK && data != null) {

            // Get the Uri of the selected file
            pdfUri = data.getData();
            img_pdf.setImageResource(R.drawable.baseline_description);

        }
    }

    private void pickPdfFromStorage() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted, request it
            Toast.makeText(getContext(), "in pich from storage", Toast.LENGTH_SHORT).show();
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_STORAGE);
        } else {
            // Permission is already granted, open file picker
            choosePdf();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_STORAGE) {
            Toast.makeText(getContext(), "in on req per result", Toast.LENGTH_LONG).show();
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission was granted
                choosePdf();
            } else {
                // Permission denied, show a message to the user
                Toast.makeText(getContext(), "Permission denied to read your External storage", Toast.LENGTH_SHORT).show();
            }
        }else {
            Toast.makeText(getContext(), "request code not match", Toast.LENGTH_SHORT).show();
        }

    }

}
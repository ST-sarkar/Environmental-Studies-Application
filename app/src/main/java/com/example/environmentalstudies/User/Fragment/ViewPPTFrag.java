package com.example.environmentalstudies.User.Fragment;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.environmentalstudies.Admin.Model.PPTUpModel;
import com.example.environmentalstudies.R;
import com.example.environmentalstudies.User.Adapter.PPTViewAdapter;
import com.example.environmentalstudies.User.Adapter.StudyAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class ViewPPTFrag extends Fragment {
    RecyclerView recyclerView;
    List<PPTUpModel> pptList = new ArrayList<>();
    PPTViewAdapter adapter;
    private long downloadId;
    String newFileName,topic,description,type;


    public ViewPPTFrag() {
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
        View view=inflater.inflate(R.layout.fragment_view_p_p_t, container, false);

        recyclerView = view.findViewById(R.id.rec_ppt);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(),2));

        fetchFiles();

        return view;
    }

    private void fetchFiles() {
        pptList.clear();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("PPT_Uploads");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        PPTUpModel model = dataSnapshot.getValue(PPTUpModel.class);
                        pptList.add(model);
                    }
                    setAdapter();
                } else {
                    Toast.makeText(getContext(), "Data not exist", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Error" + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setAdapter() {
        adapter = new PPTViewAdapter(pptList, getContext(), new PPTViewAdapter.ItemClickListener() {
            @Override
            public void onDownloadClick(View view, int position) {
                //downloadId=downloadPdfFile(position);
            }

            @Override
            public void onViewClick(View view, int position) {
                viewPptFile(position);
            }
        });
        recyclerView.setAdapter(adapter);
    }

    private void viewPptFile(int position) {
        PPTUpModel model = pptList.get(position);
        String filename = model.getFilename();
        String fileExtension = getFileExtension(filename);
        String mimeType = getPptMimeType(fileExtension);

        // Assume "storageRef" is a reference to the file in Firebase Storage
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();

        // Create a directory for storing the downloaded file
        File storageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "PPTFiles");
        if (!storageDir.exists()) {
            storageDir.mkdirs();
        }

        // Create a local file in the storage directory
        File localFile = new File(storageDir, filename);

        ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Wait ...");
        progressDialog.show();

        storageRef.child("ppt_uploads/" + filename).getFile(localFile)
                .addOnSuccessListener(taskSnapshot -> {
                    // File downloaded successfully
                    progressDialog.dismiss();
                    // Open the downloaded file using an intent
                    openFile(localFile, mimeType);
                })
                .addOnFailureListener(exception -> {
                    // Handle any errors
                    progressDialog.dismiss();
                    Toast.makeText(getContext(), "Failed to download file: " + exception.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void openFile(File file, String mimeType) {
        if (file.exists()) {
            // Create a content URI for the downloaded file using FileProvider
            Uri contentUri = FileProvider.getUriForFile(getContext(), "com.example.environmentalstudies.fileprovider", file);

            // Create an intent to view the file
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(contentUri, mimeType);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            // Verify that there are apps available to open the file
            PackageManager pm = getContext().getPackageManager();
            if (intent.resolveActivity(pm) != null) {
                startActivity(intent);
            } else {
                Toast.makeText(getContext(), "No app found to handle this file type", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getContext(), "File not found", Toast.LENGTH_SHORT).show();
        }
    }



    private String getFileExtension(String filename) {
        //String uriString = uri.toString();
        Log.e("ppt","filename="+filename);
        return filename.substring(filename.lastIndexOf(".") + 1);
    }

    private String getPptMimeType(String fileExtension) {
        Log.e("ppt","fileExtension="+fileExtension);
        if (fileExtension.equalsIgnoreCase("ppt")) {
            return "application/vnd.ms-powerpoint";
        } else if (fileExtension.equalsIgnoreCase("pptx")) {
            return "application/vnd.openxmlformats-officedocument.presentationml.presentation";
        }
        return null; // Unsupported file extension
    }


}
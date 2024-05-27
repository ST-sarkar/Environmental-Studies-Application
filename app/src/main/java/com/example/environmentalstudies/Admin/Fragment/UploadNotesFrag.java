package com.example.environmentalstudies.Admin.Fragment;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.environmentalstudies.Admin.Model.PPTUpModel;
import com.example.environmentalstudies.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;


public class UploadNotesFrag extends Fragment {

    ImageView pdf;
    EditText topic,desc;
    Button submit;
    Uri pdfUri;
    String from;
    ProgressDialog progressDialog;
    private static final int REQUEST_PERMISSION_READ_EXTERNAL_STORAGE = 1;
    private static final int PICK_PDF_FILE = 101;
   // private static final int PERMISSION_REQUEST_STORAGE = 100;
    //private static final int PICK_PDF_FILE = 101;

    //private ActivityResultLauncher<String> requestPermissionLauncher;
    //private ActivityResultLauncher<String> pickPdfLauncher;

    private final ActivityResultLauncher<String> requestPermissionLauncherpdf =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    pickPdfFile();
                } else {
                    Toast.makeText(getContext(), "Permission denied", Toast.LENGTH_SHORT).show();
                }
            });
/*
    private final ActivityResultLauncher<String> pickPdfFileLauncher =
            registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
                if (uri != null) {
                    pdfUri=uri;
                    pdf.setImageResource(R.drawable.pdf);
                }
            });



 */


    public UploadNotesFrag() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        assert getArguments() != null;
        from=getArguments().getString("from","NOTES");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_upload_notes, container, false);


        pdf=view.findViewById(R.id.img_pdf);
        topic=view.findViewById(R.id.ed_topic);
        desc=view.findViewById(R.id.ed_desc);
        submit=view.findViewById(R.id.btn_submit);

        progressDialog=new ProgressDialog(getContext());
        progressDialog.setTitle("Uploading wait...");
        progressDialog.setIndeterminate(true);

        pdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestStoragePermissionAndPickFile();

            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String topicc=topic.getText().toString();
                String ds=desc.getText().toString();
                if(pdfUri!=null && !topicc.isEmpty() && !ds.isEmpty()) {
                    progressDialog.show();
                    uploadPdfFile(topicc,ds);
                }else {
                    Toast.makeText(getContext(), "select pdf", Toast.LENGTH_SHORT).show();
                }
            }
        });


        return view;
    }
/*
    private void requestStoragePermissionAndPickFile() {
        if (ContextCompat.checkSelfPermission(
                requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE) ==
                PackageManager.PERMISSION_GRANTED) {
            // Permission is granted, proceed to pick a file
            pickPdfFile();
        } else {
            // Request permission
            requestPermissionLauncherpdf.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
    }

    private void pickPdfFile() {
        pickPdfFileLauncher.launch("application/pdf");
    }

 */
private void requestStoragePermissionAndPickFile() {
    if (ContextCompat.checkSelfPermission(
            requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE) ==
            PackageManager.PERMISSION_GRANTED) {
        // Permission is granted, proceed to pick a file
        pickPdfFile();
    } else {
        // Request permission
        requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                REQUEST_PERMISSION_READ_EXTERNAL_STORAGE);
    }
}

    private void pickPdfFile() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("application/pdf");
        startActivityForResult(intent, PICK_PDF_FILE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION_READ_EXTERNAL_STORAGE) {
            Toast.makeText(getContext(), "in request", Toast.LENGTH_SHORT).show();
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, proceed to pick a file
                pickPdfFile();
            } else {
                Toast.makeText(getContext(), "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_PDF_FILE && resultCode == Activity.RESULT_OK && data != null) {
            pdfUri = data.getData();
            pdf.setImageResource(R.drawable.pdf);
        }
    }

    private void uploadPdfFile(String topicc,String description) {

        String location;
        if(from.equals("NOTE")){
            location="Note_Uploads";
        }else if(from.equals("PROJECT")){
            location="Project_Uploads";
        }else if (from.equals("QBANK")) {
            location="QBank_Uploads";
        }else {
            location="Note_Uploads";
        }
        String finalLocation = location;
        String filename=System.currentTimeMillis()+"";
        StorageReference storageReference = FirebaseStorage.getInstance().getReference(finalLocation);
        StorageReference fileRef = storageReference.child(filename);


        fileRef.putFile(pdfUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        DatabaseReference reference= FirebaseDatabase.getInstance().getReference(finalLocation);
                        String key=reference.push().getKey();
                        String pdfur= String.valueOf(uri);
                        LocalDate today = LocalDate.now();
                        // Format the date as a String (e.g., "2024-03-16")
                        String formattedDate = today.format(DateTimeFormatter.ISO_LOCAL_DATE);
                        if(!pdfur.isEmpty() && !topicc.isEmpty() && !description.isEmpty() && !filename.isEmpty() && !formattedDate.isEmpty() && !from.isEmpty()) {
                            PPTUpModel model = new PPTUpModel(pdfur, topicc, description, filename, formattedDate, from);
                            Log.e("key", "" + key);
                            reference.child(key).setValue(model).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    progressDialog.dismiss();
                                    Toast.makeText(getContext(), "File Uploaded Successfully", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }else {
                            progressDialog.dismiss();
                            Toast.makeText(getContext(), "File not uploaded", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(getContext(), "fail to get uri", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(getContext(), "error"+e, Toast.LENGTH_SHORT).show();
            }
        });
    }


}
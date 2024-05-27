package com.example.environmentalstudies.Admin.Fragment;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.environmentalstudies.R;
import com.example.environmentalstudies.Admin.Model.PPTUpModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;


public class UploadPPTFrag extends Fragment {
    ImageView ppt;
    EditText topic,desc;
    Button submit;
    Uri pptUri;
    ProgressDialog progressDialog;


    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    pickPPTFile();
                } else {
                    Toast.makeText(getContext(), "Permission denied", Toast.LENGTH_SHORT).show();
                }
            });

    private final ActivityResultLauncher<String[]> pickPPTFileLauncher =
            registerForActivityResult(new ActivityResultContracts.OpenDocument(), uris -> {
                if (uris != null) {
                    pptUri = uris; // Assuming you only handle single file selection
                    ppt.setImageResource(R.drawable.ppt);
                }
            });


    public UploadPPTFrag() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_upload_p_p_t, container, false);

        ppt=view.findViewById(R.id.img_pdf);
        topic=view.findViewById(R.id.ed_topic);
        desc=view.findViewById(R.id.ed_desc);
        submit=view.findViewById(R.id.btn_submit);

        progressDialog=new ProgressDialog(getContext());
        progressDialog.setTitle("Uploading wait...");
        progressDialog.setIndeterminate(true);

        ppt.setOnClickListener(new View.OnClickListener() {
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
                if(pptUri!=null && !topicc.isEmpty() && !ds.isEmpty()) {
                    progressDialog.show();
                    uploadFile(topicc,ds);
                }else {
                    Toast.makeText(getContext(), "select PPT", Toast.LENGTH_SHORT).show();
                }
            }
        });



        return view;
    }

    private void requestStoragePermissionAndPickFile() {
        if (ContextCompat.checkSelfPermission(
                requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE) ==
                PackageManager.PERMISSION_GRANTED) {
            // Permission is granted, proceed to pick a file
            pickPPTFile();
        } else {
            // Request permission
            requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
    }

    private void pickPPTFile() {
        String[] mimeTypes = {"application/vnd.ms-powerpoint", "application/vnd.openxmlformats-officedocument.presentationml.presentation"};
        pickPPTFileLauncher.launch(mimeTypes);
    }

    private void uploadFile(String topicc,String description) {
        String filename;
        if(pptUri!=null) {
            String extension = getFileExtension(pptUri);
            filename = System.currentTimeMillis() + extension;
            Log.e("pptup","filename="+filename);
        }else {
            progressDialog.dismiss();
            Toast.makeText(getContext(), "please select file properly", Toast.LENGTH_SHORT).show();
            return;
        }
        StorageReference storageReference = FirebaseStorage.getInstance().getReference("ppt_uploads");
        StorageReference fileRef = storageReference.child(filename);

        fileRef.putFile(pptUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("PPT_Uploads");
                        String key=reference.push().getKey();
                        String pptUr= String.valueOf(uri);
                        LocalDate today = LocalDate.now();
                        // Format the date as a String (e.g., "2024-03-16")
                        String formattedDate = today.format(DateTimeFormatter.ISO_LOCAL_DATE);
                        PPTUpModel model=new PPTUpModel(pptUr,topicc,description,filename, formattedDate,"PPT");
                        reference.child(key).setValue(model).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                progressDialog.dismiss();
                                Toast.makeText(getContext(), "File Uploaded Successfully", Toast.LENGTH_SHORT).show();
                            }
                        });
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

  /*  private String getFileExtension(Uri uri) {
        String extension = null;
        // Check if the URI scheme is "content"
        if ("content".equals(uri.getScheme())) {
            // If it's a content URI, try to obtain the MIME type
            String mimeType = getContext().getContentResolver().getType(uri);
            // Extract the file extension from the MIME type
            if (mimeType != null) {
                String[] parts = mimeType.split("/");
                if (parts.length == 2 && parts[1] != null) {
                    extension = parts[1];
                }
            }
        } else {
            // For other types of URIs, extract the file extension from the URI itself
            String uriString = uri.toString();
            int lastDotIndex = uriString.lastIndexOf('.');
            if (lastDotIndex != -1 && lastDotIndex < uriString.length() - 1) {
                extension = uriString.substring(lastDotIndex + 1);
            }
        }
        return extension != null ? "." + extension : "";
    }

   */

    public String getFileExtension(Uri uri) {
        /*
        ContentResolver contentResolver = getContext().getContentResolver();
        String mimeType = contentResolver.getType(uri);
        Log.e("ppt","mimeType="+mimeType);

        if (mimeType != null) {
            // Split the MIME type string to extract the subtype, which typically contains the file extension
            String[] parts = mimeType.split("/");
            if (parts.length == 2) {
                return "." + parts[1];
            }
        }

        // If MIME type cannot be determined or doesn't contain a subtype, return a default extension
        return ".dat"; // You can choose a default extension according to your use case



            String path = uri.getPath();
            Log.e("ppturl","url="+uri);
            // Get the last segment of the path, which typically contains the file name
            String filename = path.substring(path.lastIndexOf("/") + 1);

            // Get the file extension from the filename
            String extension = "";
            int lastDotIndex = filename.lastIndexOf('.');
            if (lastDotIndex != -1) {
                extension = filename.substring(lastDotIndex);
            }
            return extension;

         */

            ContentResolver contentResolver = getContext().getContentResolver();
            String mimeType = contentResolver.getType(uri);

            if (mimeType != null) {
                // Use MimeTypeMap to get file extension from MIME type
                String extension = MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType);
                if (extension != null) {
                    return "." + extension;
                }
            }

            // If MIME type cannot be determined or doesn't have a corresponding extension, return a default extension
            return ".dat"; // You can choose a default extension according to your use case

    }



}
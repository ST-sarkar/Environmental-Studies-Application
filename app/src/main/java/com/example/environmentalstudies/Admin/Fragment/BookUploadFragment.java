package com.example.environmentalstudies.Admin.Fragment;

import static android.app.Activity.RESULT_OK;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.environmentalstudies.R;
import com.example.environmentalstudies.User.Model.bookBuyModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class BookUploadFragment extends Fragment {
    Button submit;
    TextInputEditText title, author, price, uri;
    ImageView bookImg;
    private DatabaseReference databaseReference;
    private static final int PICK_IMAGE_REQUEST = 1;
    Uri imageUri;
    ProgressDialog progressDialog;

    private final ActivityResultLauncher<String> pickImageFileLauncher =
            registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
                if (uri != null) {
                    imageUri = uri;
                    // Set the image URI to the ImageView
                    bookImg.setImageURI(imageUri);
                    submit.setVisibility(View.VISIBLE);
                }
            });

    public BookUploadFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_book_upload, container, false);

        title = view.findViewById(R.id.ed_book_title);
        author = view.findViewById(R.id.ed_book_auther);
        price = view.findViewById(R.id.ed_book_price);
        uri = view.findViewById(R.id.ed_book_uri);
        bookImg = view.findViewById(R.id.img_book_upload);
        submit = view.findViewById(R.id.btn_book_submit);

        progressDialog = new ProgressDialog(getContext());
        progressDialog.setTitle("Uploading wait...");
        progressDialog.setIndeterminate(true);

        submit.setVisibility(View.GONE);

        bookImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Call method to pick an image from external storage
                pickImageFromExternalStorage();
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Call method to load image from local storage
                if (!author.getText().toString().isEmpty() && !title.getText().toString().isEmpty() && !price.getText().toString().isEmpty() && !uri.getText().toString().isEmpty()) {
                    if (Integer.parseInt(price.getText().toString()) > 0) {
                        progressDialog.show();
                        uploadImageToFirebase();
                    } else {
                        Toast.makeText(getContext(), "price must be greater than 0", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getContext(), "All fields are mandatory!!", Toast.LENGTH_SHORT).show();
                }

            }
        });

        return view;
    }

    private void uploadImageToFirebase() {
        StorageReference imageRef = FirebaseStorage.getInstance().getReference("Book_Images");

        // Upload image to Firebase Storage
        imageRef.child(System.currentTimeMillis() + ".png").putFile(imageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // Get the download URL of the uploaded image
                        taskSnapshot.getStorage().getDownloadUrl()
                                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        // Store the download URL to Firebase Realtime Database
                                        String imageUrl = uri.toString();
                                        storeImageUrlToDatabase(imageUrl);
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        progressDialog.dismiss();
                                        Toast.makeText(getContext(), "Failed to get download URL", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(getContext(), "Upload failed" + e, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void storeImageUrlToDatabase(String imageUrl) {
        // Create a new entry in the database with the image URL
        databaseReference = FirebaseDatabase.getInstance().getReference("Book_Data");
        String key = databaseReference.push().getKey();

        String strtitle = title.getText().toString();
        String strauthor = author.getText().toString();
        String num = price.getText().toString();
        int priced;
        if (!num.isEmpty()) {
            priced = Integer.parseInt(num);
        } else {
            priced = 0;
        }
        String weburi = uri.getText().toString();

        bookBuyModel model = new bookBuyModel(imageUrl, strtitle, strauthor, priced, weburi);

        databaseReference.child(key).setValue(model);
        progressDialog.dismiss();
        Toast.makeText(getContext(), "Book Uploaded Successfully", Toast.LENGTH_SHORT).show();
        title.setText("");
        author.setText("");
        price.setText("");
        uri.setText("");
    }

    private void pickImageFromExternalStorage() {
        pickImageFileLauncher.launch("image/*");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            // Get the URI of the selected image
            imageUri = data.getData();
            // Set the image URI to the ImageView
            bookImg.setImageURI(imageUri);
            submit.setVisibility(View.VISIBLE);
        }
    }
}

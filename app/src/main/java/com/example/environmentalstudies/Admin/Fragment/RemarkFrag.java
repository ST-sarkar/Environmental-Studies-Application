package com.example.environmentalstudies.Admin.Fragment;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.environmentalstudies.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class RemarkFrag extends Fragment {
    EditText remark;
    ImageView pdf;
    Button submit;


    public RemarkFrag() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_remark, container, false);

        remark=view.findViewById(R.id.remark);
        pdf=view.findViewById(R.id.pdf);
        submit=view.findViewById(R.id.submit);

        Bundle bundle=getArguments();

        Uri uri= Uri.parse(bundle.getString("uri"));
        String studUid=bundle.getString("taskId");


        pdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                displayPdf(uri);
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String getremark=remark.getText().toString();
                if(!getremark.isEmpty()){
                    addRemark(studUid,getremark);
                }else {
                    Toast.makeText(getContext(), "Give remark", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return view;
    }

    private void addRemark(String studUid,String remark) {
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference();
        String uid= FirebaseAuth.getInstance().getCurrentUser().getUid();

        reference.child("submissions").child(uid).child(studUid).child("remark").setValue(remark)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(getContext(), "Remark submitted", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), "Fail to remark"+e, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void displayPdf(Uri pdfUri) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(pdfUri, "application/pdf");
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        Intent chooser = Intent.createChooser(intent, "Open PDF");
        try {
            startActivity(chooser);
        } catch (ActivityNotFoundException e) {
            // Informs the user that no PDF readers are installed and possibly recommend installing one
            Toast.makeText(getContext(), "No PDF reader found. Please install one to view the document.", Toast.LENGTH_LONG).show();
        }
    }

}
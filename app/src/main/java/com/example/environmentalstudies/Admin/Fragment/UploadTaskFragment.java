package com.example.environmentalstudies.Admin.Fragment;

import android.app.DatePickerDialog;
import android.icu.util.Calendar;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.example.environmentalstudies.Admin.Model.TaskUpload;
import com.example.environmentalstudies.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;


public class UploadTaskFragment extends Fragment {
    EditText title;
    EditText desc;
    EditText dead_date;
    EditText publish_date;

    public UploadTaskFragment() {
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
        View view=inflater.inflate(R.layout.fragment_upload_task, container, false);

        title=view.findViewById(R.id.task_Title);
        desc=view.findViewById(R.id.task_Desc);
        dead_date=view.findViewById(R.id.task_deadline);
        publish_date=view.findViewById(R.id.task_publish);

        Button upload=view.findViewById(R.id.btn_Upload);
        Button clear=view.findViewById(R.id.btn_clear);
        Button pickDateButton1=view.findViewById(R.id.pick_date1);
        Button pickDateButton2=view.findViewById(R.id.pick_date2);


        pickDateButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        getContext(),
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                // Display the chosen date
                                String selectedDate = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
                                publish_date.setText(selectedDate);
                            }
                        }, year, month, day);
                datePickerDialog.show();
            }
        });


        pickDateButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        getContext(),
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                // Display the chosen date
                                String selectedDate = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
                                dead_date.setText(selectedDate);
                            }
                        }, year, month, day);
                datePickerDialog.show();
            }
        });




        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                title.setText("");
                desc.setText("");
                dead_date.setText("");
                publish_date.setText("");

            }
        });

        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

                String uid= Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
                if(!uid.isEmpty()) {
                    String ddt = dead_date.getText().toString();
                    String pubdt = publish_date.getText().toString();
                    String titel = title.getText().toString();
                    String ds = desc.getText().toString();

                    if (!pubdt.isEmpty() && !ddt.isEmpty() && !titel.isEmpty() && !ds.isEmpty()) {
                        TaskUpload task = new TaskUpload(ddt, pubdt, titel, ds);
                        reference.child("Tasks").child(uid).setValue(task).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(getContext(), "Task uploaded successfully", Toast.LENGTH_SHORT).show();
                                clearUI();

                            }
                        });
                    }

                }else {
                    Toast.makeText(getContext(), "Error: Task upload failed", Toast.LENGTH_SHORT).show();
                }

            }
        });

        return view;
    }

    private void clearUI() {
        title.setText("");
        desc.setText("");
        dead_date.setText("");
        publish_date.setText("");
    }
}
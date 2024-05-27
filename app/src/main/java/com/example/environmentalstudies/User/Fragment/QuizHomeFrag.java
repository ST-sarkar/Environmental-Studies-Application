package com.example.environmentalstudies.User.Fragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.os.StrictMode;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;



import com.example.environmentalstudies.User.ExamQuize;
import com.example.environmentalstudies.User.NormalQuize;
import com.example.environmentalstudies.User.QuizActivity;

import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.environmentalstudies.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class QuizHomeFrag extends Fragment {

    CardView quiz, unitWise, exam;
    List<String> units=new ArrayList<>();
    List<String> Units=new ArrayList<>();

    ProgressDialog progressDialog;
    String selectedUnit;

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_quiz_home, container, false);

        progressDialog = new ProgressDialog(getContext());

        quiz = view.findViewById(R.id.quiz);
        unitWise = view.findViewById(R.id.unitWise);
        exam = view.findViewById(R.id.exam);

        //new LoadDataTask().execute();
        //getUnit();


        quiz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getQueCount();
            }
        });

        unitWise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*showUnitCheckDialog();*/
                progressDialog.show();
                showUnitSelectionDialog();

            }
        });
        exam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showExamSettingsDialog();
            }
        });

        return view;
    }


    @SuppressLint("MissingInflatedId")
    private void showUnitSelectionDialog() {
        final String[] selectedUnit = new String[1];

        // Assuming you have a method to fetch units from Firebase
        fetchUnitsFromFirebase(new OnUnitsFetchedListener() {
            @Override
            public void onUnitsFetched(List<String> units) {
                Collections.sort(units);
                //Toast.makeText(getContext(), "Units fetched successfully", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
                LayoutInflater inflater = getLayoutInflater();
                View dialogLayout = inflater.inflate(R.layout.dialog_unit_selection, null);
                final Spinner spinner = dialogLayout.findViewById(R.id.spinnerUnit);
                EditText ed_queno=dialogLayout.findViewById(R.id.ed_que_no);

                ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, units);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner.setAdapter(adapter);
                adapter.notifyDataSetChanged();

                // Set the listener for spinner selection outside and before the button click listeners
                spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                        // Here, you capture the selected unit as soon as the user selects it
                        selectedUnit[0] = adapterView.getItemAtPosition(position).toString();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {
                        selectedUnit[0] = ""; // This is optional based on your logic
                    }
                });

                Button btnContinue = dialogLayout.findViewById(R.id.btnContinue);
                Button btnCancel = dialogLayout.findViewById(R.id.btnCancel);

                final AlertDialog dialog = new AlertDialog.Builder(getContext())
                        .setTitle("Select Unit")
                        .setView(dialogLayout)
                        .create();

                btnContinue.setOnClickListener(view -> {
                    String que_no=ed_queno.getText().toString();
                    if ("".equals(selectedUnit[0])) {
                        Toast.makeText(getContext(), "Please select a unit", Toast.LENGTH_SHORT).show();
                    } else {
                        Intent intent = new Intent(getContext(), QuizActivity.class);
                        intent.putExtra("unit", selectedUnit[0]);
                        intent.putExtra("number_que", que_no);
                        getContext().startActivity(intent);
                        dialog.dismiss();
                    }
                });

                btnCancel.setOnClickListener(view -> dialog.dismiss());
                dialog.show();
            }

            @Override
            public void onError(String errorMessage) {
                Toast.makeText(getContext(), "Error fetching units: " + errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Interface for callback when units are fetched from Firebase
        interface OnUnitsFetchedListener {
        void onUnitsFetched(List<String> units);
        void onError(String errorMessage);
    }

    // Method to fetch units from Firebase
    private void fetchUnitsFromFirebase(final OnUnitsFetchedListener listener) {
        DatabaseReference unitsRef = FirebaseDatabase.getInstance().getReference().child("Quizzes");
        unitsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<String> units = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    units.add(snapshot.getKey());
                }
                listener.onUnitsFetched(units);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                listener.onError(databaseError.getMessage());
            }
        });
    }



    private void showExamSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View dialogLayout = getLayoutInflater().inflate(R.layout.dialog_exam_settings, null);
        builder.setView(dialogLayout);

        RadioGroup radioGroupSet = dialogLayout.findViewById(R.id.radioGroupSet);
        //TextView txtExamTime = dialogLayout.findViewById(R.id.txtExamTime);
        Button btnStartExam = dialogLayout.findViewById(R.id.btnStartExam);

            RadioButton radioButton1 = new RadioButton(getContext());
            radioButton1.setText("Unit Test");
            radioGroupSet.addView(radioButton1);

        RadioButton radioButton2 = new RadioButton(getContext());
        radioButton2.setText("Final Test");
        radioGroupSet.addView(radioButton2);
        final AlertDialog examSettingsDialog = builder.create();

        btnStartExam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int selectedSetId = radioGroupSet.getCheckedRadioButtonId();
                if (selectedSetId != -1) {
                    RadioButton selectedSetRadioButton = radioGroupSet.findViewById(selectedSetId);
                    String selectedSet = selectedSetRadioButton.getText().toString().trim();
                    Intent intent = new Intent(getContext(), ExamQuize.class);
                    /*intent.putExtra("selectedUnit", selectedUnit);*/
                    if(selectedSet.equals("Unit Test")){
                        intent.putExtra("mark",20);
                    }else {
                        intent.putExtra("mark",70);
                    }
                    //intent.putExtra("selectedSet",selectedSet);
                    startActivity(intent);

                    examSettingsDialog.dismiss();
                } else {
                    Toast.makeText(getContext(), "Please select an exam set", Toast.LENGTH_SHORT).show();
                }
            }
        });
        examSettingsDialog.show();
    }
    private void getQueCount() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View dialogLayout = getLayoutInflater().inflate(R.layout.dialog_quecount, null);
        builder.setView(dialogLayout);

        EditText queCountET = dialogLayout.findViewById(R.id.queCET);
        Button btnStartExam = dialogLayout.findViewById(R.id.btnStartExam);


        final AlertDialog examSettingsDialog = builder.create();


        btnStartExam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                examSettingsDialog.dismiss();

                int queC = Integer.parseInt(queCountET.getText().toString().trim());

                Intent intent = new Intent(getContext(), NormalQuize.class);
                intent.putExtra("marks",queC);
                startActivity(intent);

            }
        });
        examSettingsDialog.show();
    }


}
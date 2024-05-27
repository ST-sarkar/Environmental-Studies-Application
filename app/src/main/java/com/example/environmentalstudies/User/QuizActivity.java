package com.example.environmentalstudies.User;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.environmentalstudies.Admin.Model.AddMcqModel;
import com.example.environmentalstudies.R;
import com.example.environmentalstudies.User.Model.StudentData;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class QuizActivity extends AppCompatActivity {

    TextView unit, que, txtExamTime,txmark;
    ProgressDialog progressDialog;
    RadioButton option1, option2, option3, option4;
    Button saveButton;
    String unitno;
    ArrayList<String> actualAnswer;
    ArrayList<String> selectedAnswer;

    String checkedUnit, selectedSet;
    RadioGroup optionsRadioGroup;
    ArrayList<JSONArray> UJSONArray;
    ArrayList<JSONArray> allQuizzes;

    int i = 0, mark = 0, fail = 0, correctAnswer = 0;
    int currentUnitIndex = 0, Qcount = 2;

    private CountDownTimer examTimer;

    private List<AddMcqModel> questions = new ArrayList<>();
    private List<AddMcqModel> Questions = new ArrayList<>();
    private int currentQuestionIndex = 0;
    private int numberOfQue = 0;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);


        progressDialog = new ProgressDialog(this);
        actualAnswer = new ArrayList<>();
        selectedAnswer = new ArrayList<>();
        option1 = findViewById(R.id.option1);
        unit = findViewById(R.id.unit);
        que = findViewById(R.id.que);
        option2 = findViewById(R.id.option2);
        option3 = findViewById(R.id.option3);
        option4 = findViewById(R.id.option4);
        optionsRadioGroup = findViewById(R.id.optionsRadioGroup);
        saveButton = findViewById(R.id.saveButton);
        txtExamTime = findViewById(R.id.txtExamTime);
        //unit no
        unitno = getIntent().getStringExtra("unit");
        if(getIntent().getStringExtra("number_que").equals("")){
            numberOfQue=0;
        }else {
            numberOfQue = Integer.parseInt(getIntent().getStringExtra("number_que"));
        }
        unit.setText(unitno);
        //mark
        txmark = findViewById(R.id.mark);
        txmark.setText(Questions.size()+"");

        progressDialog.dismiss();
        //}


        // Initialize Firebase and fetch questions
        initializeFirebase();

        // Start the quiz timer for 2 hours
        //startTimer();

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check answer and update score
                //int selectedId = optionsGroup.getCheckedRadioButtonId();
                // Assuming the first button has ID 1, compare with correct answer
                progressDialog.show();

                int selectedId = optionsRadioGroup.getCheckedRadioButtonId();

                if (selectedId == option1.getId()) {
                    selectedAnswer.add(option1.getText().toString());
                } else if (selectedId == option2.getId()) {
                    selectedAnswer.add(option2.getText().toString());
                } else if (selectedId == option3.getId()) {
                    selectedAnswer.add(option3.getText().toString());
                } else if (selectedId == option4.getId()) {
                    selectedAnswer.add(option4.getText().toString());
                } else {
                    Toast.makeText(getApplicationContext(), "Please select an answer", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                    return; // Exit the onClick method to prevent further execution

                }
                clearUI();
                currentQuestionIndex++;


                if (currentQuestionIndex < Questions.size()) {
                    progressDialog.dismiss();
                    displayNextQuestion();
                } else {
                    // Quiz finished, show score
                    //finishQuiz();
                    progressDialog.dismiss();
                    //txtExamTime.setText("Exam Time: 00:00");
                    viewResult();
                    showResultDialog();
                }

            }
        });

    }


    private void initializeFirebase() {
        questions.clear();
        if(!unitno.isEmpty()) {
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Quizzes").child(unitno);
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            AddMcqModel question = snapshot.getValue(AddMcqModel.class);
                            questions.add(question);
                        }
                        if (questions.size()>0) {
                            int numberOfQuestionsNeeded = numberOfQue; // Or any number you need
                            if(numberOfQuestionsNeeded==0){
                                Questions = questions;
                            }
                            else if (questions.size() > numberOfQuestionsNeeded) {
                                Questions = questions.subList(0, numberOfQuestionsNeeded);
                            } else {
                                Questions = questions;
                            }

                            txmark.setText(Questions.size()+"");
                            displayNextQuestion();
                        } else {
                            Toast.makeText(QuizActivity.this, "question list is empty", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(QuizActivity.this, "snapshot not present", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle errors
                    Toast.makeText(QuizActivity.this, "error in fetching questions", Toast.LENGTH_SHORT).show();
                }
            });
        }else {
            Toast.makeText(this, "unit value is empty", Toast.LENGTH_SHORT).show();
        }
    }




    private void displayNextQuestion() {
        AddMcqModel question = Questions.get(currentQuestionIndex);

        //Toast.makeText(this, "question index"+currentQuestionIndex+question.getAns(), Toast.LENGTH_SHORT).show();

        que.setText((currentQuestionIndex + 1) + ") " + question.getQue());
        //questionText.setText(question.getQuestion());
        //optionsRadioGroup.removeAllViews();

        option1.setText(question.getOp1());
        option2.setText(question.getOp2());
        option3.setText(question.getOp3());
        option4.setText(question.getOp4());
        actualAnswer.add(question.getAns());

        // Dynamically add options as RadioButtons to optionsGroup
        // ...

        // Remember to set IDs for radio buttons, e.g., 1 for the first option, 2 for the second, etc.
    }

    private void finishQuiz() {
        // Display score and optionally allow the user to restart the quiz or exit

    }

    private void clearUI() {
        que.setText("");
        option1.setText("");
        option2.setText("");
        option3.setText("");
        option4.setText("");
        optionsRadioGroup.clearCheck();
    }

    public void viewResult() {
        correctAnswer=0;
        fail=0;
        progressDialog.show();
        int minSize = Math.min(actualAnswer.size(), selectedAnswer.size());
        for (int count = 0; count < minSize; count++) {
            if (actualAnswer.get(count).equals(selectedAnswer.get(count))) {
                correctAnswer++;
            } else {
                fail++;
            }

        }
        progressDialog.dismiss();
    }


    @SuppressLint("MissingInflatedId")
    private void showResultDialog() {

        //txtExamTime.setText("Exam Time: 00:00");

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.result_dialog, null);
        builder.setView(dialogView);

        TextView dialogTitle = dialogView.findViewById(R.id.dialogTitle);
        TextView dialogMessage = dialogView.findViewById(R.id.dialogMessage);
        Button okButton = dialogView.findViewById(R.id.okButton);

        dialogTitle.setText("Quiz Result");
        dialogMessage.setText("Total Marks: " + Questions.size() + "\nIncorrect Questions: " + fail + "\nYou answered " + correctAnswer + " out of " +  Questions.size() + " questions correctly.");

        StudentData studentData=new StudentData();
        studentData.setStudentSharedPreferences(getApplicationContext());
        studentData.UpdateStudentData(Questions.size(),correctAnswer);

        final AlertDialog alertDialog = builder.create();

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                onBackPressed();
            }
        });
        alertDialog.show();
    }



    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

}


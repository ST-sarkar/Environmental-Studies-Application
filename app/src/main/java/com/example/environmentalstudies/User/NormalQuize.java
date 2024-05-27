package com.example.environmentalstudies.User;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;


import com.example.environmentalstudies.Admin.Model.AddMcqModel;
import com.example.environmentalstudies.R;
import com.example.environmentalstudies.User.Model.StudentData;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class NormalQuize extends AppCompatActivity {

    private TextView markTextView;
    private TextView que;
    private RadioGroup optionsRadioGroup;
    private RadioButton option1;
    private RadioButton option2;
    private RadioButton option3;
    private RadioButton option4;
    private Button saveButton;
    private CountDownTimer examTimer;
    ProgressDialog progressDialog1;

    private List<AddMcqModel> questions = new ArrayList<>();
    private List<AddMcqModel> Questions = new ArrayList<>();
    private int currentQuestionIndex = 0;
    private int numberOfQue = 0;
    List<String> actualAnswer=new ArrayList<>();
    List<String> selectedAnswer=new ArrayList<>();

    int i = 0, mark = 0, fail = 0, correctAnswer = 0;
    int totalmarks;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_normal_quize);


        markTextView = findViewById(R.id.mark);
        que = findViewById(R.id.que);
        optionsRadioGroup = findViewById(R.id.optionsRadioGroup);
        option1 = findViewById(R.id.option1);
        option2 = findViewById(R.id.option2);
        option3 = findViewById(R.id.option3);
        option4 = findViewById(R.id.option4);
        saveButton = findViewById(R.id.saveButton);

        progressDialog1=new ProgressDialog(this);
        progressDialog1.setTitle("Loding...");
        progressDialog1.show();

        totalmarks=getIntent().getIntExtra("marks",70);
        numberOfQue=totalmarks;

        markTextView.setText(String.valueOf(totalmarks));


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
                progressDialog1.show();

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
                    progressDialog1.dismiss();
                    return; // Exit the onClick method to prevent further execution
                }
                clearUI();
                currentQuestionIndex++;

                if (currentQuestionIndex < questions.size()) {
                    progressDialog1.dismiss();
                    displayNextQuestion();
                } else {
                    // Quiz finished, show score
                    //finishQuiz();
                    progressDialog1.dismiss();
                    //txtExamTime.setText("Exam Time: 00:00");
                    viewResult();
                    showResultDialog();
                }


            }
        });

//    progressDialog.show();
    }


    private void initializeFirebase() {
        // Clear any existing questions
        questions.clear();

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Quizzes");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Check if data exists
                if (dataSnapshot.exists()) {
                    // Clear the temporary list each time onDataChange is called
                    List<AddMcqModel> tempQuestions = new ArrayList<>();

                    // Iterate through each child (quiz) under "Quizzes"
                    for (DataSnapshot quizSnapshot : dataSnapshot.getChildren()) {
                        // Iterate through each child (question) under each quiz
                        for (DataSnapshot questionSnapshot : quizSnapshot.getChildren()) {
                            // Parse the question data and add it to the temporary list
                            AddMcqModel question = questionSnapshot.getValue(AddMcqModel.class);
                            tempQuestions.add(question);
                        }
                    }

                    if (!tempQuestions.isEmpty()) {
                        // Shuffle the fetched questions to randomize their order
                        Collections.shuffle(tempQuestions);
                        // Toast.makeText(ExamQuize.this, "inside list not empty", Toast.LENGTH_SHORT).show();

                        // Optional: Select a subset if you don't need all questions
                        int numberOfQuestionsNeeded = numberOfQue; // Or any number you need
                        if (tempQuestions.size() > numberOfQuestionsNeeded) {
                            questions = tempQuestions.subList(0, numberOfQuestionsNeeded);
                        } else {
                            questions = tempQuestions;
                        }

                        // Display the first question
                        displayNextQuestion();
                    } else {
                        Toast.makeText(NormalQuize.this, "Question list is empty", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(NormalQuize.this, "No quizzes found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle errors
                Toast.makeText(NormalQuize.this, "Error in fetching questions: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void displayNextQuestion() {
        AddMcqModel question = questions.get(currentQuestionIndex);

        //Toast.makeText(this, "question index"+currentQuestionIndex+question.getAns(), Toast.LENGTH_SHORT).show();

        que.setText((currentQuestionIndex + 1) + ") " + question.getQue());
        //questionText.setText(question.getQuestion());
        //optionsRadioGroup.removeAllViews();

        option1.setText(question.getOp1());
        option2.setText(question.getOp2());
        option3.setText(question.getOp3());
        option4.setText(question.getOp4());
        actualAnswer.add(question.getAns());
        progressDialog1.dismiss();
        // Dynamically add options as RadioButtons to optionsGroup
        // ...

        // Remember to set IDs for radio buttons, e.g., 1 for the first option, 2 for the second, etc.
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

        fail=0;
        correctAnswer=0;
        progressDialog1.show();
        int minSize = Math.min(actualAnswer.size(), selectedAnswer.size());
        for (int count = 0; count < minSize; count++) {
            if (actualAnswer.get(count).equals(selectedAnswer.get(count))) {
                correctAnswer++;
            } else {
                fail++;
            }
        }
        progressDialog1.dismiss();
    }


    @SuppressLint("MissingInflatedId")
    private void showResultDialog() {


        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.result_dialog, null);
        builder.setView(dialogView);

        TextView dialogTitle = dialogView.findViewById(R.id.dialogTitle);
        TextView dialogMessage = dialogView.findViewById(R.id.dialogMessage);
        Button okButton = dialogView.findViewById(R.id.okButton);

        dialogTitle.setText("Quiz Result");
        dialogMessage.setText("Total Marks: " + questions.size() + "\nIncorrect Questions: " + fail + "\nYou answered " + correctAnswer + " out of " + questions.size() + " questions correctly.");

        StudentData studentData=new StudentData();
        studentData.setStudentSharedPreferences(getApplicationContext());
        studentData.UpdateStudentData(questions.size(),correctAnswer);

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
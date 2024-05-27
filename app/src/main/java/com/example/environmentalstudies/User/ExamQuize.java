package com.example.environmentalstudies.User;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.environmentalstudies.Admin.Model.AddMcqModel;
import com.example.environmentalstudies.R;
import com.example.environmentalstudies.RoomDatabase.DownloadedFileViewModel;
import com.example.environmentalstudies.User.Model.StudentData;
import com.example.environmentalstudies.User.Model.boardModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class ExamQuize extends AppCompatActivity {

    TextView unit, que, txtExamTime;
    ProgressDialog progressDialog;
    RadioButton option1, option2, option3, option4;
    Button saveButton;
    String unitno;
    List<String> actualAnswer=new ArrayList<>();
    List<String> selectedAnswer=new ArrayList<>();

    String checkedUnit, selectedSet;
    int totalmarks;
    RadioGroup optionsRadioGroup;

    private DownloadedFileViewModel mStudentViewModel;
    DatabaseReference reference=FirebaseDatabase.getInstance().getReference();


    int mark = 0, fail = 0, correctAnswer = 0;

    String timeRemaining;
    long durationInMillis;


    private CountDownTimer examTimer;

    private List<AddMcqModel> questions = new ArrayList<>();
    private List<AddMcqModel> Questions = new ArrayList<>();
    private int currentQuestionIndex = 0;
    private int numberOfQue = 0;
    String name,uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exam_quize);


        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Loding..");

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
        TextView mark = findViewById(R.id.mark);
        //unit no
        unitno = getIntent().getStringExtra("unit");
        //checked unit
        checkedUnit = getIntent().getStringExtra("selectedUnit");
        //selected set
        selectedSet = getIntent().getStringExtra("selectedSet");

        totalmarks=getIntent().getIntExtra("mark",70);
        if(totalmarks==20){
            durationInMillis=1800000;
            startExamTimer();
            numberOfQue=20;

        }else {
            durationInMillis=7200000;
            startExamTimer();
            numberOfQue=70;
        }

        mark.setText(String.valueOf(totalmarks));

        StudentData studentData=new StudentData();
        studentData.setLoginSharedPreferences(getApplicationContext());
        name=studentData.getName();
        uid=studentData.getUid();


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

                if (currentQuestionIndex < questions.size()) {
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

        progressDialog.show();

    }


    private void initializeFirebase() {
        // Clear any existing questions
        questions.clear();


        reference.child("Quizzes").addValueEventListener(new ValueEventListener() {
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
                        //Toast.makeText(ExamQuize.this, "inside list not empty", Toast.LENGTH_SHORT).show();

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
                        Toast.makeText(ExamQuize.this, "Question list is empty", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(ExamQuize.this, "No quizzes found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle errors
                Toast.makeText(ExamQuize.this, "Error in fetching questions: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
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
        progressDialog.dismiss();

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
        examTimer.cancel();
        correctAnswer=0;
        fail=0;

        progressDialog.show();
        int minSize = Math.min(actualAnswer.size(), selectedAnswer.size());
        for (int count = 0; count < minSize; count++) {
            if (actualAnswer.get(count).equals(selectedAnswer.get(count))) {
                correctAnswer++;
            } else {
                //Log.e("fail","fail="+fail);
                fail++;
            }
        }
        progressDialog.dismiss();
    }


    @SuppressLint("MissingInflatedId")
    private void showResultDialog() {

        txtExamTime.setText("Exam Time: 00:00");

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

        updateLeaderBoard();

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

    private void updateLeaderBoard() {
        // Split the time string into hours, minutes, and seconds
        String[] parts = timeRemaining.split(":");
        int hours = Integer.parseInt(parts[0]);
        int minutes = Integer.parseInt(parts[1]);
        int seconds = Integer.parseInt(parts[2]);

        // Calculate total milliseconds
        long remainingMilliseconds = ((hours * 60 * 60) + (minutes * 60) + seconds) * 1000;

        getPreviusExamData(new ExamDataCallback() {
            @Override
            public void onExamDataReceived(boardModel model1) {
                // Do something with the retrieved data
                boardModel finalModel;

                if(numberOfQue==20) {
                    finalModel = new boardModel(name,correctAnswer,model1.getFinalMarks(),durationInMillis-remainingMilliseconds,model1.getFinalTime(),correctAnswer+remainingMilliseconds,model1.getFinalSort());

                }else {
                    finalModel = new boardModel(name,model1.getUtMarks(),correctAnswer,model1.getUtTime(),durationInMillis-remainingMilliseconds,model1.getUtSort(),correctAnswer+remainingMilliseconds);

                }
                updateExamData(finalModel);
            }

            @Override
            public void onDataNotAvailable() {
                // Handle case when data is not available
                boardModel defaultModel = new boardModel("sushant", 0, 0, 0, 0, 0, 0);
                // Do something with defaultModel
            }
        });




    }

    private void getPreviusExamData(final ExamDataCallback callback) {
        reference.child("LeaderBoard").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.e("quiz", "data exist");
                boardModel model = snapshot.getValue(boardModel.class);
                if (model != null) {
                    callback.onExamDataReceived(model);
                } else {
                    callback.onDataNotAvailable();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onDataNotAvailable();
            }
        });
    }


    private void updateExamData(boardModel model) {
        reference.child("LeaderBoard").child(uid).setValue(model).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(ExamQuize.this, "success", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ExamQuize.this, "fail", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }


    private void startExamTimer() {
        examTimer = new CountDownTimer(durationInMillis, 1000) {
            public void onTick(long millisUntilFinished) {
                /*long minutes = millisUntilFinished / (60 * 1000);
                long seconds = (millisUntilFinished % (60 * 1000)) / 1000;
                final String timeRemaining = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
*/
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        long hours = millisUntilFinished / (60 * 60 * 1000);
                        long minutes = (millisUntilFinished % (60 * 60 * 1000)) / (60 * 1000);
                        long seconds = (millisUntilFinished % (60 * 1000)) / 1000;
                        timeRemaining = String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds);
                        Log.d("ExamTimer", "Updating UI: " + timeRemaining);
                        txtExamTime.setText("Time: " + timeRemaining);
                    }
                });
            }

            public void onFinish() {
                Log.d("ExamTimer", "Exam time finished.");

                txtExamTime.setText("Exam Time: 00:00");
                viewResult();
                showResultDialog();
                if (examTimer != null) {
                    examTimer.cancel();
                }
            }
        }.start();
    }

    interface ExamDataCallback {
        void onExamDataReceived(boardModel model);
        void onDataNotAvailable();
    }


}
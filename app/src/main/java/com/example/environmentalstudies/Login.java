package com.example.environmentalstudies;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.environmentalstudies.Admin.AdminDashboard;
import com.example.environmentalstudies.Admin.Model.UserProfile;
import com.example.environmentalstudies.User.UserDashboard;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class Login extends AppCompatActivity {

    EditText UsernameET, PasswordET;
    ProgressBar loginProgress;
    TextView signupBtn, forgot_pass;
    Button LoginBtn;
    String username, email, password;
    FirebaseAuth auth = FirebaseAuth.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        UsernameET = findViewById(R.id.UsernameET);
        PasswordET = findViewById(R.id.PasswordET);
        signupBtn = findViewById(R.id.tx_signUp);
        LoginBtn = findViewById(R.id.LoginBtn);
        loginProgress = findViewById(R.id.loginProgress);
        forgot_pass = findViewById(R.id.tx_forgot_pass);

        //username=auth.getCurrentUser().getUid();

        LoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                email = UsernameET.getText().toString();
                password = PasswordET.getText().toString();
                loginProgress.setVisibility(View.VISIBLE);
                if (!email.isEmpty() && !password.isEmpty()) {

                    if (password.length() > 6) {

                            auth.signInWithEmailAndPassword(email, password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                @Override
                                public void onSuccess(AuthResult authResult) {
                                    if (email.equals("admin@gmail.com") && password.equals("admin@123")) {
                                        startActivity(new Intent(Login.this, AdminDashboard.class));
                                        loginProgress.setVisibility(View.GONE);
                                        finish();

                                    } else {
                                        updateProfileData();
                                        username = Objects.requireNonNull(auth.getCurrentUser()).getUid();
                                        SharedPreferences.Editor editor = getSharedPreferences("LoginData", 0).edit();
                                        editor.putString("UserName", username);
                                        editor.putString("PassWord", password);
                                        editor.apply();
                                        startActivity(new Intent(Login.this, UserDashboard.class));
                                        loginProgress.setVisibility(View.GONE);
                                        finish();
                                    }
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(Login.this, "Login UnSuccessful", Toast.LENGTH_SHORT).show();
                                    loginProgress.setVisibility(View.GONE);
                                }
                            });

                    } else {
                        Toast.makeText(Login.this, "Invalid Password !!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(Login.this, "Fields are empty! !", Toast.LENGTH_SHORT).show();
                }
            }
        });

        signupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Login.this, Register.class));
            }
        });

        forgot_pass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Login.this, ForgotPassActivity.class);
                intent.putExtra("email", UsernameET.getText().toString());
                startActivity(intent);
            }
        });
    }

    private void updateProfileData() {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        //UserProfile profile=new UserProfile(name,phone,address,username,password);
        username = Objects.requireNonNull(auth.getCurrentUser()).getUid();

        reference.child("UserProfile").child(username).child("password").setValue(password).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(Login.this, "Login Successful", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(Login.this, "Login Successful" + e, Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            if(user.getEmail().equals("admin@gmail.com")){
                startActivity(new Intent(Login.this, AdminDashboard.class));
                finish();
            }else {
                startActivity(new Intent(Login.this, UserDashboard.class));
                finish();
            }
        }
    }

}

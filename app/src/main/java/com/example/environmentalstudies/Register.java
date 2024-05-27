package com.example.environmentalstudies;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import com.example.environmentalstudies.Admin.Model.UserProfile;
import com.example.environmentalstudies.User.Model.StudentData;
import com.example.environmentalstudies.User.UserDashboard;
import com.example.environmentalstudies.databinding.ActivityRegisterBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.apache.poi.ss.formula.functions.T;

import java.util.HashMap;
import java.util.Map;


public class Register extends AppCompatActivity {

    ActivityRegisterBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                
                String name = binding.editTextName.getText().toString();
                //String email = binding.editTextEmail.getText().toString();
                String phone = binding.editTextPhone.getText().toString();
                String address = binding.editTextAddress.getText().toString();
                String username = binding.editTextUsername.getText().toString();
                String password = binding.editTextPassword.getText().toString();


                if (name.isEmpty()||phone.isEmpty()||address.isEmpty()||username.isEmpty()||password.isEmpty())
                {
                    Toast.makeText(Register.this, "All field Required..", Toast.LENGTH_SHORT).show();
                }else if (!(phone.length()==10))
                {
                    Toast.makeText(Register.this, "Please enter Valid Mobile Number", Toast.LENGTH_SHORT).show();
                }else if (!Patterns.EMAIL_ADDRESS.matcher(username).matches()){
                    Toast.makeText(Register.this, "Please Enter Valid Email", Toast.LENGTH_SHORT).show();
                }else {
                    RegisterUser(name,phone,address,username,password);
                }

            }
        });

    }

    private void RegisterUser(String name, String phone, String address, String username, String password) {

        FirebaseAuth auth=FirebaseAuth.getInstance();
        auth.createUserWithEmailAndPassword(username,password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                DatabaseReference reference= FirebaseDatabase.getInstance().getReference();
                String uid=auth.getCurrentUser().getUid();
                UserProfile profile=new UserProfile(name,phone,address,username,password);

                reference.child("UserProfile").child(uid).setValue(profile).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(Register.this, "Registration Successful", Toast.LENGTH_SHORT).show();

                        StudentData studentData=new StudentData();
                        studentData.setLoginSharedPreferences(getApplicationContext());
                        studentData.updateLoginInfo(uid,password,name);
                        startActivity(new Intent(Register.this, Login.class));
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Register.this, "Registration UnSuccessful"+e, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(Register.this, "Registration UnSuccessful"+e, Toast.LENGTH_SHORT).show();
            }
        });

        /*
        ProgressDialog pd = new ProgressDialog(this);
        pd.setCancelable(false);
        pd.setMessage("Please wait..");
        pd.show();

        new Handler().post(new Runnable() {
            @Override
            public void run() {

                StringRequest request = new StringRequest(Request.Method.POST, "http://www.testproject.life/Projects/EnvStudies/ENV_studentRegister.php", new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        if(response.equals("exits")){
                            Toast.makeText(Register.this, "Username already used Choose another.", Toast.LENGTH_SHORT).show();
                        }else if (response.equals("Inserted")){
                            Toast.makeText(Register.this, "Register Successfully.", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(Register.this,Login.class));
                            finishAffinity();

                        }else {
                            Log.e("response:",response);
                            Toast.makeText(Register.this, "Something went wrong try later....", Toast.LENGTH_SHORT).show();
                        }
                        pd.dismiss();

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("error:",error.getMessage());
                        pd.dismiss();
                    }
                }){

                    @Override
                    protected Map<String, String> getParams() {

                        Map<String , String> param = new HashMap<>();
                        param.put("sName",name);
                        param.put("sMobile",phone);
                        param.put("sEmail",email);
                        param.put("sAddress",address);
                        param.put("sUsername",username);
                        param.put("sPassword",password);

                        return param;
                    }
                };

                RequestQueue queue = Volley.newRequestQueue(Register.this);
                queue.add(request);



            }
        });

         */


    }
}
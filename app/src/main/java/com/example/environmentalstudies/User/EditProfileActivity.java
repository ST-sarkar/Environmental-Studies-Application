package com.example.environmentalstudies.User;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.StrictMode;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.environmentalstudies.Admin.Model.UserProfile;
import com.example.environmentalstudies.R;
import com.example.environmentalstudies.User.Fragment.Profile;
import com.example.environmentalstudies.User.Model.StudentData;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;



public class EditProfileActivity extends AppCompatActivity {
    EditText name,mobile,address,password;

    String uname,pword,prev_email;
    Button btn_save;

    DatabaseReference reference;

    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        progressDialog=new ProgressDialog(this);
        progressDialog.setTitle("Wait..");

        name=findViewById(R.id.name);
        mobile=findViewById(R.id.mobile);
        address=findViewById(R.id.address);
        //username=findViewById(R.id.username);
        password=findViewById(R.id.password);

        btn_save=findViewById(R.id.btn_save);

        uname=getSharedPreferences("LoginData",0).getString("UserName","a");
        pword=getSharedPreferences("LoginData",0).getString("PassWord","a");

        getProfileData();



        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String Name=name.getText().toString().trim();
                String Mobile=mobile.getText().toString().trim();
                String Address=address.getText().toString().trim();
                //String UserName=username.getText().toString().trim();
                String PassWord=password.getText().toString().trim();

                if (Name.isEmpty() || Mobile.isEmpty() || Address.isEmpty() || PassWord.isEmpty())
                {
                    Toast.makeText(EditProfileActivity.this, "All field must fill", Toast.LENGTH_SHORT).show();
                }else {
                    saveProfile(Name,Mobile,Address,PassWord);
                }

            }
        });

        progressDialog.show();

    }
    public void getProfileData()
    {
        reference= FirebaseDatabase.getInstance().getReference();
        reference.child("UserProfile").child(uname).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if(task.getResult().exists())
                {
                    UserProfile profile=task.getResult().getValue(UserProfile.class);
                    name.setText(profile.getName());
                    mobile.setText(profile.getPhone());
                    address.setText(profile.getAddress());
                    //username.setText(profile.getUsername());
                    password.setText(profile.getPassword());

                    prev_email=profile.getUsername();
                    pword=profile.getPassword();
                    progressDialog.dismiss();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(EditProfileActivity.this, "error :"+e, Toast.LENGTH_SHORT).show();
            }
        });
    }
    public void saveProfile(String name,String mobile,String address,String password)
    {
        progressDialog.setMessage("Wait...");
        progressDialog.show();

        UserProfile profile=new UserProfile(name,mobile,address,prev_email,password);
        FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();
        if(user!=null && !prev_email.isEmpty() && pword.length()>=6)
        {// Re-authenticate the user
            AuthCredential credential = EmailAuthProvider.getCredential(prev_email, pword);

            user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        // Step 1: Update the password
                        user.updatePassword(password).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {

                                    String uid=user.getUid();
                                    StudentData studentData=new StudentData();
                                    studentData.setLoginSharedPreferences(getApplicationContext());
                                    studentData.updateLoginInfo(uid,password,name);

                                    // Password are successfully updated
                                    getProfileUpdate(profile);
                                    progressDialog.dismiss();
                                    //Toast.makeText(EditProfileActivity.this, "Profile updated successfully", Toast.LENGTH_SHORT).show();

                                } else {
                                    // Handle failure
                                    progressDialog.dismiss();
                                    Toast.makeText(getApplicationContext(), "Failed to update password", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    } else {
                        // Re-authentication failed
                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(), "Re-authentication failed", Toast.LENGTH_SHORT).show();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e("re-auth",""+e);
                    Toast.makeText(EditProfileActivity.this, ""+e, Toast.LENGTH_LONG).show();
                }
            });
        }else
        {
            Toast.makeText(this, "Invalid Password "+prev_email, Toast.LENGTH_SHORT).show();
        }

    }

    public void getProfileUpdate(UserProfile profile)
    {
        reference= FirebaseDatabase.getInstance().getReference();
        reference.child("UserProfile").child(String.valueOf(uname)).setValue(profile).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                progressDialog.dismiss();
                Toast.makeText(EditProfileActivity.this, "Profile Successfully Updated", Toast.LENGTH_SHORT).show();
                Profile profileFragment = new Profile();
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container_view_id, profileFragment)
                        .addToBackStack(null) // Optional, if you want to add the transaction to the back stack
                        .commit();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(EditProfileActivity.this, "error in updation"+e, Toast.LENGTH_SHORT).show();
            }
        });
    }



}
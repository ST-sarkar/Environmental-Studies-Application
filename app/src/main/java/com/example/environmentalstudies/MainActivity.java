package com.example.environmentalstudies;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.example.environmentalstudies.User.UserDashboard;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {


                String check = getSharedPreferences("LoginData",0).getString("UserName","e");

                if (check.equals("e")){
                    startActivity(new Intent(MainActivity.this, Login.class));
                    finish();
                }else {
                    startActivity(new Intent(MainActivity.this, UserDashboard.class));
                    finish();

                }

            }
        },2000);


    }
}
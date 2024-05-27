package com.example.environmentalstudies.User;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import com.example.environmentalstudies.User.Fragment.*;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.environmentalstudies.databinding.ActivityUserDashboardBinding;


import com.example.environmentalstudies.Admin.Fragment.AdminHome;
import com.example.environmentalstudies.Login;
import com.example.environmentalstudies.R;
import com.example.environmentalstudies.User.Fragment.Profile;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class UserDashboard extends AppCompatActivity {

    ActivityUserDashboardBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding  = ActivityUserDashboardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);


        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.fragment_container, new UserHomeFrag());
        fragmentTransaction.commit();
        //setFragment(new AdminHome());
        nav();

    }

    private void nav() {

        binding.bottomNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                if (item.getItemId() == R.id.nav_Home) {
                    setFragment(new UserHomeFrag());

                    return true;
                }else if (item.getItemId() == R.id.nav_profile){
                    setFragment(new Profile());

                    return true;
                }
                else if (R.id.nav_study==item.getItemId()) {
                    setFragment(new StudyMaterialFrag());
                    return true;
                } else if (R.id.nav_classroom==item.getItemId()) {
                    setFragment(new ClassWorkFrag());
                    return  true;
                }
                return false;
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.option_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (R.id.nav_Logout == item.getItemId()){

            logoutUser();
            return true;
        }

        return true;
    }

    private void setFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.commit();
    }

    public void logoutUser() {
        // Sign out from FirebaseAuth
        FirebaseAuth.getInstance().signOut();

        // Clear SharedPreferences or any login state you've set
        SharedPreferences preferences = getSharedPreferences("LoginData", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.apply();

        // Navigate back to Login Activity
        Intent intent = new Intent(this, Login.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

}
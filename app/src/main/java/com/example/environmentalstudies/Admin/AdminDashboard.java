package com.example.environmentalstudies.Admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.environmentalstudies.Admin.Fragment.AddQuizeQue;
import com.example.environmentalstudies.Admin.Fragment.AdminHome;
import com.example.environmentalstudies.Admin.Fragment.BookUploadFragment;
import com.example.environmentalstudies.Admin.Fragment.UploadNotesFrag;
import com.example.environmentalstudies.Admin.Fragment.UploadPPTFrag;
import com.example.environmentalstudies.Admin.Fragment.UploadTaskFragment;
import com.example.environmentalstudies.Admin.Model.TaskUpload;
import com.example.environmentalstudies.Login;
import com.example.environmentalstudies.R;
import com.example.environmentalstudies.User.Adapter.TaskAdapter;
import com.example.environmentalstudies.databinding.ActivityAdminDashboardBinding;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AdminDashboard extends AppCompatActivity {

    ActivityAdminDashboardBinding binding;
    DrawerLayout drawerLayout;
    ActionBarDrawerToggle actionBarDrawerToggle;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAdminDashboardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        progressDialog=new ProgressDialog(this);
        progressDialog.show();

        setSupportActionBar(binding.toolbar);
        drawerLayout = binding.adminDrawer;
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.nav_open, R.string.nav_close);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);




        setFragment(new AdminHome(),"");
        nav();
        actionBarDrawerToggle.syncState();
        progressDialog.dismiss();

    }
    private void nav() {
        final String[] from = {""};
        binding.navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.nav_Home) {
                    setFragment(new AdminHome(), from[0]);
                    drawerLayout.closeDrawer(GravityCompat.START);
                    return true;
                } else if (item.getItemId() == R.id.nav_addQue){
                    setFragment(new AddQuizeQue(), from[0]);
                    drawerLayout.closeDrawer(GravityCompat.START);
                    return true;
                } else if (item.getItemId()==R.id.nav_task) {
                    setFragment(new UploadTaskFragment(), from[0]);
                    drawerLayout.closeDrawer(GravityCompat.START);
                    return true;
                } else if (item.getItemId()==R.id.nav_UpPPT) {
                    from[0] ="PPT";
                    setFragment(new UploadPPTFrag(), from[0]);
                    drawerLayout.closeDrawer(GravityCompat.START);
                    return true;
                } else if (item.getItemId()==R.id.nav_UpNotes) {
                    from[0] ="NOTE";
                    setFragment(new UploadNotesFrag(),from[0]);
                    drawerLayout.closeDrawer(GravityCompat.START);
                    return true;
                } else if (item.getItemId()==R.id.nav_UpProject) {
                    from[0] ="PROJECT";
                    setFragment(new UploadNotesFrag(),from[0]);
                    drawerLayout.closeDrawer(GravityCompat.START);
                    return true;
                } else if (item.getItemId()==R.id.nav_UpQBank) {
                    from[0] ="QBANK";
                    setFragment(new UploadNotesFrag(),from[0]);
                    drawerLayout.closeDrawer(GravityCompat.START);
                    return true;
                }else if (item.getItemId()==R.id.nav_UpBooks) {
                    from[0] ="BOOKS";
                    setFragment(new BookUploadFragment(),from[0]);
                    drawerLayout.closeDrawer(GravityCompat.START);
                    return true;
                } else if (item.getItemId() == R.id.nav_Logout){
                    FirebaseAuth.getInstance().signOut();
                    startActivity(new Intent(AdminDashboard.this, Login.class));
                    finish();
                }
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
        });
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (actionBarDrawerToggle.onOptionsItemSelected(item)){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    private void setFragment(Fragment fragment,String from) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        Bundle bundle=new Bundle();
        bundle.putString("from",from);
        fragment.setArguments(bundle);
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.commit();
    }


}
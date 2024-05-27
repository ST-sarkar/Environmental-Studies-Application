package com.example.environmentalstudies.User.Fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.environmentalstudies.R;
import com.example.environmentalstudies.User.Adapter.LeaderBoardAdapter;
import com.example.environmentalstudies.User.Model.boardModel;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class LeaderBoardFrag extends Fragment {
    private final String[] titles = new String[]{"Unit Exam", "Final Exam"};
    ViewPager2 viewPager;
    TabLayout tabLayout;

    public LeaderBoardFrag() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_leader_board, container, false);

        viewPager = view.findViewById(R.id.view_pager);
        viewPager.setAdapter(new LeaderBoardFrag.boardViewPagerAdapter(this));
        tabLayout = view.findViewById(R.id.tabs);
        viewPager.setUserInputEnabled(true);


        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> tab.setText(titles[position])
        ).attach();

        return view;
    }

    public static class boardViewPagerAdapter extends FragmentStateAdapter {
        public boardViewPagerAdapter(Fragment fragment) {
            super(fragment);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            // Return a NEW fragment instance in createFragment(int)
            switch (position) {
                case 0:
                    return new utLeaderBoardFragment("unit");
                case 1:
                    return new utLeaderBoardFragment("final");
                default:
                    return new utLeaderBoardFragment("sss"); // This should never happen. Always account for each position above
            }
        }

        @Override
        public int getItemCount() {
            return 2; // Three fragments
        }
    }

}
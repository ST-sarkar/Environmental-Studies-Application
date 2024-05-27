package com.example.environmentalstudies.User.Fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.environmentalstudies.R;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;


public class DownloadFrag extends Fragment {

    private final String[] titles = new String[]{"Notes", "Projects", "Question Banks"};
    ViewPager2 viewPager;
    TabLayout tabLayout;

    public DownloadFrag() {
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
        View view=inflater.inflate(R.layout.fragment_download, container, false);
        //getActivity().getActionBar().setTitle("Downloads");

        viewPager = view.findViewById(R.id.view_pager);
        viewPager.setAdapter(new MyViewPagerAdapter(this));
        tabLayout = view.findViewById(R.id.tabs);
        viewPager.setUserInputEnabled(true);


        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> tab.setText(titles[position])
        ).attach();

        return view;
    }

    public static class MyViewPagerAdapter extends FragmentStateAdapter {
        public MyViewPagerAdapter(Fragment fragment) {
            super(fragment);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            // Return a NEW fragment instance in createFragment(int)
            switch (position) {
                case 0:
                    return new TabFragment("Notes");
                case 1:
                    return new TabFragment("Projects");
                case 2:
                    return new TabFragment("Qbanks");
                default:
                    return new TabFragment("sss"); // This should never happen. Always account for each position above
            }
        }

        @Override
        public int getItemCount() {
            return 3; // Three fragments
        }
    }
}


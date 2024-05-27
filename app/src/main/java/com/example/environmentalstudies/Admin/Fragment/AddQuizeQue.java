package com.example.environmentalstudies.Admin.Fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.environmentalstudies.Admin.Addmcq;
import com.example.environmentalstudies.R;

public class AddQuizeQue extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        startActivity(new Intent(getActivity(), Addmcq.class));
        return inflater.inflate(R.layout.fragment_add_quize_que, container, false);
    }
}
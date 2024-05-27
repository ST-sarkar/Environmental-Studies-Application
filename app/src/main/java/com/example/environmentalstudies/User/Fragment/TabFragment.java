package com.example.environmentalstudies.User.Fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.environmentalstudies.R;
import com.example.environmentalstudies.RoomDatabase.DownloadedFile;
import com.example.environmentalstudies.RoomDatabase.DownloadedFileViewModel;
import com.example.environmentalstudies.User.Adapter.DownloadAdapter;
import com.example.environmentalstudies.User.DownloadedPDFViewActivity;

import java.util.ArrayList;
import java.util.List;


public class TabFragment extends Fragment {

    String from="-";
    RecyclerView recyclerView;
    private DownloadedFileViewModel downloadedFileViewModel;
    List<DownloadedFile> allFiles=new ArrayList<>();
    DownloadAdapter adapter;
    DownloadAdapter.OnItemClickListener listener;


    public TabFragment() {
        // Required empty public constructor

    }

    public TabFragment(String from) {
        // Required empty public constructor
        this.from=from;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            from = getArguments().getString("from");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_tab, container, false);

        recyclerView = view.findViewById(R.id.rec_download);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Initialize your adapter with an empty list or initial dataset
        adapter = new DownloadAdapter(allFiles, getParentFragmentManager());
        adapter.setListener(listener);
        recyclerView.setAdapter(adapter);

        fetchData();

        return view;
    }

    private void fetchData() {
        downloadedFileViewModel = new ViewModelProvider(this).get(DownloadedFileViewModel.class);
        allFiles.clear();
        Observer<List<DownloadedFile>> observer = files -> {
            // Update the adapter's dataset and notify the adapter of the change
            allFiles.addAll(files);
            adapter.setFiles(files); // Assuming you have a method in your adapter to update the dataset
            adapter.notifyDataSetChanged();
        };

        if(from.equals("Notes")) {
            downloadedFileViewModel.getAllNoteDownloadedFiles().observe(getViewLifecycleOwner(), observer);
        } else if (from.equals("Projects")) {
            downloadedFileViewModel.getAllProjectDownloadedFiles().observe(getViewLifecycleOwner(), observer);
        } else if (from.equals("Qbanks")) {
            downloadedFileViewModel.getAllQbankDownloadedFiles().observe(getViewLifecycleOwner(), observer);
        } else {
            Toast.makeText(getContext(), "data not fetched", Toast.LENGTH_SHORT).show();
        }
    }



}
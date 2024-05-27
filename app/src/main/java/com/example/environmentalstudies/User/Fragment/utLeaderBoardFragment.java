package com.example.environmentalstudies.User.Fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.environmentalstudies.R;
import com.example.environmentalstudies.User.Adapter.LeaderBoardAdapter;
import com.example.environmentalstudies.User.Model.boardModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class utLeaderBoardFragment extends Fragment {
    String from;
    RecyclerView recyclerView;
    Button viewFullBoard;
    List<boardModel> modelList=new ArrayList<>();
    List<boardModel> fiveItemList=new ArrayList<>();

    LeaderBoardAdapter adapter;

    public utLeaderBoardFragment(String from) {
        // Required empty public constructor
        this.from=from;
    }

    public utLeaderBoardFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_ut_leader_board, container, false);

        recyclerView=view.findViewById(R.id.rec_board);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        viewFullBoard=view.findViewById(R.id.view_list);

        setBoardAdapter(from);
        viewFullBoard.setVisibility(View.GONE);

        viewFullBoard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fiveItemList.clear();
                fiveItemList.addAll(modelList);
                adapter.notifyDataSetChanged();
            }
        });

        fetchData(from);
        return view;
    }

    private void fetchData(String from) {

        DatabaseReference reference= FirebaseDatabase.getInstance().getReference();
        Query query;
        if(from.equals("unit")) {
            query = reference.child("LeaderBoard").orderByChild("utMarks");
        }else {
            query = reference.child("LeaderBoard").orderByChild("finalMarks");
        }
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    for (DataSnapshot dataSnapshot:snapshot.getChildren()){
                        modelList.add(dataSnapshot.getValue(boardModel.class));
                    }
                    if(modelList.size()>5) {
                        fiveItemList.addAll(modelList.subList(0, 5));
                        viewFullBoard.setVisibility(View.VISIBLE);
                    }else {
                        fiveItemList.addAll(modelList);
                    }
                    adapter.notifyDataSetChanged();

                }else {
                    Toast.makeText(getContext(), "data not present", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void setBoardAdapter(String from) {

        adapter=new LeaderBoardAdapter(fiveItemList,from);
        recyclerView.setAdapter(adapter);
    }
}
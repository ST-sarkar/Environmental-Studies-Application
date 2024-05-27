package com.example.environmentalstudies.User.Fragment;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.environmentalstudies.R;
import com.example.environmentalstudies.User.Adapter.BookBuyAdapter;
import com.example.environmentalstudies.User.Model.bookBuyModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class BookBuyFragment extends Fragment {

    RecyclerView recyclerView;
    BookBuyAdapter adapter;
    ProgressDialog progressDialog;
    List<bookBuyModel> bookList=new ArrayList<>();
    public BookBuyFragment() {
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
        View view=inflater.inflate(R.layout.fragment_book_buy, container, false);

        recyclerView=view.findViewById(R.id.rec_book);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        progressDialog=new ProgressDialog(getContext());
        progressDialog.setTitle("Loding..");


        setAdapter();

        fetchBookData();

        progressDialog.show();

        return view;
    }



    private void fetchBookData() {
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference();
        reference.child("Book_Data").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                        bookBuyModel model=dataSnapshot.getValue(bookBuyModel.class);
                        bookList.add(model);
                    }
                    progressDialog.dismiss();
                    adapter.notifyDataSetChanged();
                }else {
                    Toast.makeText(getContext(), "data not present", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        progressDialog.dismiss();
    }

    private void setAdapter() {
        adapter=new BookBuyAdapter(bookList);
        recyclerView.setAdapter(adapter);
    }
}
package com.example.environmentalstudies.Admin.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.example.environmentalstudies.Admin.Model.AddMcqModel;
import com.example.environmentalstudies.R;

import java.util.ArrayList;

public class AddMCQAdapter extends RecyclerView.Adapter<AddMCQAdapter.Viewholder> {

    ArrayList<AddMcqModel> arrayList;

    public AddMCQAdapter(ArrayList<AddMcqModel> arrayList) {
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Viewholder(LayoutInflater.from(parent.getContext()).inflate(R.layout.addmcqcard,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull Viewholder holder, int position) {

        AddMcqModel po = arrayList.get(position);
        int qn= position + 1;

        holder.questionTextView.setText("Question "+qn+" : \n" + po.getQue());
        holder.option1TextView.setText("A : " + po.getOp1());
        holder.option2TextView.setText("B : " + po.getOp2());
        holder.option3TextView.setText("C : " + po.getOp3());
        holder.option4TextView.setText("D : " + po.getOp4());
        holder.answerTextView.setText("Answer: " + po.getAns());
        holder.unitTextView.setText("Unit: " + po.getUnit());


    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class Viewholder extends RecyclerView.ViewHolder {

        private TextView questionTextView, option1TextView, option2TextView, option3TextView, option4TextView, answerTextView, unitTextView;
        public Viewholder(@NonNull View itemView) {
            super(itemView);

            questionTextView = itemView.findViewById(R.id.questionTV);
            option1TextView = itemView.findViewById(R.id.option1TV);
            option2TextView = itemView.findViewById(R.id.option2TV);
            option3TextView = itemView.findViewById(R.id.option3TV);
            option4TextView = itemView.findViewById(R.id.option4TV);
            answerTextView = itemView.findViewById(R.id.answerTV);
            unitTextView = itemView.findViewById(R.id.unitTV);
        }
    }
}

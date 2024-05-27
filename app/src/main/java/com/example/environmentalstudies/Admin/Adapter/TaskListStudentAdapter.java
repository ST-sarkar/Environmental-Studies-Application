package com.example.environmentalstudies.Admin.Adapter;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.environmentalstudies.Admin.Fragment.RemarkFrag;
import com.example.environmentalstudies.R;
import com.example.environmentalstudies.User.Fragment.UploadTaskFrag;
import com.example.environmentalstudies.User.Model.TaskSubModel;

import java.util.ArrayList;
import java.util.List;

public class TaskListStudentAdapter extends RecyclerView.Adapter<TaskListStudentAdapter.StudViewHolder> {
    List<String> taskId = new ArrayList<>();
    List<TaskSubModel> taskList = new ArrayList<>();
    List<String> studList=new ArrayList<>();
    FragmentManager fragmentManager;

    public TaskListStudentAdapter(List<TaskSubModel> taskList, List<String> taskId, List<String> studList, FragmentManager fragmentManager) {
        this.studList=studList;
        this.taskId=taskId;
        this.taskList=taskList;
        this.fragmentManager=fragmentManager;
    }

    @NonNull
    @Override
    public StudViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.stud_list_item,parent,false);
        return new StudViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StudViewHolder holder, int position) {

        holder.name.setText(studList.get(position));
        holder.sub_date.setText(taskList.get(position).getSubDate());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString("uri", taskList.get(holder.getAdapterPosition()).getPdfUri());
                bundle.putString("taskId", taskId.get(holder.getAdapterPosition()));

                    // Example: Navigate to NextFragment
                RemarkFrag nextFragment = new RemarkFrag();
                nextFragment.setArguments(bundle);
                fragmentManager.beginTransaction()
                            .replace(R.id.fragment_container, nextFragment)
                            .addToBackStack(null)
                            .commit();

            }
        });
    }

    @Override
    public int getItemCount() {
        return taskId.size();
    }

    public static class StudViewHolder extends RecyclerView.ViewHolder {
        TextView name,sub_date;
        public StudViewHolder(@NonNull View itemView) {
            super(itemView);
            name=itemView.findViewById(R.id.name);
            sub_date=itemView.findViewById(R.id.submit_date);
        }
    }
}

package com.example.environmentalstudies.User.Adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.environmentalstudies.Admin.Fragment.ListStudTaskFrag;
import com.example.environmentalstudies.Admin.Model.TaskUpload;
import com.example.environmentalstudies.R;
import com.example.environmentalstudies.User.Fragment.UploadTaskFrag;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.MyViewHolder> {
    List<TaskUpload> Tasks;
    List<String> taskId;
    FragmentManager fragmentManager;
    String user;

    public TaskAdapter(List<TaskUpload> tasks, List<String> taskId, FragmentManager fragmentManager, String user) {
        this.Tasks = tasks;
        this.taskId=taskId;
        this.fragmentManager=fragmentManager;
        this.user=user;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.task_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        //Toast.makeText(holder.itemView.getContext(), "in binding veiw", Toast.LENGTH_SHORT).show();
        TaskUpload upload=Tasks.get(position);
        holder.title.setText(upload.getTitle());
        holder.deaddate.setText(upload.getDeadline());
        holder.publishdate.setText(upload.getPublishdate());

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                if(!user.equals("student")){
                    showDeleteConfirmationDialog(holder.itemView.getContext());
                }
                return false;
            }
        });



        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(holder.itemView.getContext(), UploadTaskFrag.class);
                intent.putExtra("title",upload.getTitle());
                intent.putExtra("desc",upload.getDesc());
                intent.putExtra("deadline",Tasks.get(holder.getAdapterPosition()).getDeadline());
                intent.putExtra("taskId",taskId.get(holder.getAdapterPosition()));

                Bundle bundle = new Bundle();
                bundle.putString("title", upload.getTitle());
                bundle.putString("desc", upload.getDesc());
                bundle.putString("deadline",Tasks.get(holder.getAdapterPosition()).getDeadline());
                bundle.putString("taskId", taskId.get(holder.getAdapterPosition()));

                if(user.equals("student")) {
                    // Example: Navigate to NextFragment
                    UploadTaskFrag nextFragment = new UploadTaskFrag();
                    nextFragment.setArguments(bundle);
                    fragmentManager.beginTransaction()
                            .replace(R.id.fragment_container, nextFragment)
                            .addToBackStack(null)
                            .commit();
                }else{
                    ListStudTaskFrag frag=new ListStudTaskFrag();
                    frag.setArguments(bundle);
                    fragmentManager.beginTransaction()
                            .replace(R.id.fragment_container, frag)
                            .addToBackStack(null)
                            .commit();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return Tasks.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView title,publishdate,deaddate;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            title=itemView.findViewById(R.id.title);
            publishdate=itemView.findViewById(R.id.publish_date);
            deaddate=itemView.findViewById(R.id.deadline_date);
        }
    }

    public void showDeleteConfirmationDialog(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Confirm Deletion");
        builder.setMessage("Are you sure you want to delete this data?");

        // Add the buttons
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                DatabaseReference reference= FirebaseDatabase.getInstance().getReference();
                reference.child("Tasks").removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        reference.child("submissions").removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(context, "Task successfully deleted", Toast.LENGTH_SHORT).show();
                                taskId.clear();
                                Tasks.clear();
                                notifyDataSetChanged();
                            }
                        });
                    }
                });

            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked Cancel button
                // Dismiss the dialog
                dialog.dismiss();
            }
        });

        // Create and show the AlertDialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}

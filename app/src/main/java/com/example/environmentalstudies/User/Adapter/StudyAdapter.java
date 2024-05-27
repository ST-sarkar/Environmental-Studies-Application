package com.example.environmentalstudies.User.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.environmentalstudies.R;
import com.example.environmentalstudies.User.Model.NoteDataModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class StudyAdapter extends RecyclerView.Adapter<StudyAdapter.StudyViewHolder> {
    List<NoteDataModel> pdfList=new ArrayList<>();
    private BookItemClickListener mClickListener;
    Context context;
    List<String> keyList=new ArrayList<>();
    String from;

    public StudyAdapter(List<NoteDataModel> pdfList, List<String> keyList, Context context, String from, BookItemClickListener itemClickListener) {
        this.pdfList = pdfList;
        this.mClickListener=itemClickListener;
        this.context=context;
        this.keyList=keyList;
        this.from=from;
    }

    @NonNull
    @Override
    public StudyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.study_material_item_view,parent,false);
        Log.e("book","onCreate");
        return new StudyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StudyViewHolder holder, int position) {

        Log.e("BookonBind","book onbind");
        NoteDataModel model=pdfList.get(position);
        holder.name.setText("Material Name: "+model.getTopic());
        holder.descc.setText(model.getDesc());
        if(model.isBook()){
            holder.bookmark.setImageResource(R.drawable.baseline_bookmark);
        }else {
            holder.bookmark.setImageResource(R.drawable.baseline_bookmark_add_24);
        }
        holder.bookmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean flag=false;
                int pos=holder.getAdapterPosition();
                String key=keyList.get(pos);
                String type=pdfList.get(pos).getType();
                String location=getLocation(type);
                if(from.equals("BookMark")){
                    flag=false;
                    pdfList.remove(pos);
                    removeBook(key);
                    holder.bookmark.setImageResource(R.drawable.baseline_bookmark_add_24);
                }else if(pdfList.get(pos).isBook()){
                    flag=false;
                    pdfList.get(pos).setBook(flag);
                    holder.bookmark.setImageResource(R.drawable.baseline_bookmark_add_24);
                    removeBook(key);
                }else {
                    flag=true;
                    pdfList.get(pos).setBook(flag);
                    holder.bookmark.setImageResource(R.drawable.baseline_bookmark);
                    updateBookmark(location,key);
                }
                //pdfList.get(pos).setBook(flag);
                notifyDataSetChanged();


            }
        });

        holder.option.setOnClickListener(view -> {
            if (mClickListener != null)
                mClickListener.onDownloadClick(view, holder.getAdapterPosition());
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mClickListener != null) mClickListener.onViewClick(view, holder.getAdapterPosition());
            }
        });

    }

    private void updateBookmark(String location, String key) {
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference();
        String uid= FirebaseAuth.getInstance().getCurrentUser().getUid();
        reference.child("BookMarks").child(uid).child(key).child("location").setValue(location).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(context, "success", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context, "fail to update event", Toast.LENGTH_SHORT).show();
            }
        });
    }

    void removeBook(String key){
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference();
        String uid= FirebaseAuth.getInstance().getCurrentUser().getUid();
        reference.child("BookMarks").child(uid).child(key).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(context, "success", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private String getLocation(String type) {
        if(type.equals("NOTE"))
        {
            return "Note_Uploads";
        } else if (type.equals("PROJECT")) {
            return "Project_Uploads";

        } else if (type.equals("QBANK")) {
            return "QBank_Uploads";
        }else {
            return "-";
        }
    }

    @Override
    public int getItemCount() {
        return pdfList.size();
    }

    public static class StudyViewHolder extends RecyclerView.ViewHolder {

        TextView name,descc;
        ImageView option,bookmark;
        public StudyViewHolder(@NonNull View itemView) {
            super(itemView);
            name=itemView.findViewById(R.id.tx_pdf_title);
            descc=itemView.findViewById(R.id.tx_pdf_desc);
            option=itemView.findViewById(R.id.img_download);
            bookmark=itemView.findViewById(R.id.img_bookmark);

        }
    }

    // parent activity will implement this method to respond to click events
    public interface BookItemClickListener {
        void onDownloadClick(View view, int position);
        void onViewClick(View view, int position);
    }

}

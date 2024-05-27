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

import com.example.environmentalstudies.Admin.Model.PPTUpModel;
import com.example.environmentalstudies.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class BookMarkAdapter extends RecyclerView.Adapter<BookMarkAdapter.BookViewHolder> {
    List<PPTUpModel> pdfList=new ArrayList<>();
    Context context;
    List<String> keyList=new ArrayList<>();
    String from;

    public BookMarkAdapter(List<PPTUpModel> pdfList,List<String> keyList,  Context context,  String from) {
        this.pdfList = pdfList;
        this.context = context;
        this.keyList = keyList;
        this.from = from;
    }

    @NonNull
    @Override
    public BookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.study_material_item_view,parent,false);
        Log.e("book","onCreate");
        return new BookViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookViewHolder holder, int position) {
        Log.e("BookonBind","book onbind");
        PPTUpModel model=pdfList.get(position);
        holder.name.setText("Material Name: "+model.getTopic());
        holder.descc.setText(model.getDesc());



    }

    @Override
    public int getItemCount() {
        return pdfList.size();
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


    public static class BookViewHolder extends RecyclerView.ViewHolder {
        TextView name,descc;
        ImageView option,bookmark;
        public BookViewHolder(@NonNull View itemView) {
            super(itemView);
            name=itemView.findViewById(R.id.tx_pdf_title);
            descc=itemView.findViewById(R.id.tx_pdf_desc);
            option=itemView.findViewById(R.id.img_download);
            bookmark=itemView.findViewById(R.id.img_bookmark);
        }
    }

}

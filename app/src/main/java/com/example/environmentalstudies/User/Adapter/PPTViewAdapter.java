package com.example.environmentalstudies.User.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.environmentalstudies.Admin.Model.PPTUpModel;
import com.example.environmentalstudies.R;

import org.apache.poi.ss.formula.NameIdentifier;

import java.util.ArrayList;
import java.util.List;

public class PPTViewAdapter extends RecyclerView.Adapter<PPTViewAdapter.PPTViewHolder> {
    List<PPTUpModel> pptList=new ArrayList<>();
    private PPTViewAdapter.ItemClickListener mClickListener;
    Context context;

    public PPTViewAdapter(List<PPTUpModel> pptList, Context context,PPTViewAdapter.ItemClickListener mClickListener) {
        this.pptList = pptList;
        this.mClickListener = mClickListener;
        this.context = context;
    }

    @NonNull
    @Override
    public PPTViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.ppt_view_item,parent,false);

        return new PPTViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PPTViewHolder holder, int position) {
        PPTUpModel model=pptList.get(position);
        holder.name.setText("Material Name: "+model.getTopic());
        holder.descc.setText(model.getDesc());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mClickListener != null) mClickListener.onViewClick(view, holder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return pptList.size();
    }

    public static class PPTViewHolder extends RecyclerView.ViewHolder {
        TextView name,descc;
        public PPTViewHolder(@NonNull View itemView) {
            super(itemView);
            name=itemView.findViewById(R.id.tx_topic_ppt);
            descc=itemView.findViewById(R.id.tx_desc_ppt);
        }
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onDownloadClick(View view, int position);
        void onViewClick(View view, int position);
    }
}

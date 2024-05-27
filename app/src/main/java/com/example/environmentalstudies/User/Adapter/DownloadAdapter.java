package com.example.environmentalstudies.User.Adapter;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.environmentalstudies.R;
import com.example.environmentalstudies.RoomDatabase.DownloadedFile;
import com.example.environmentalstudies.User.DownloadedPDFViewActivity;
import com.example.environmentalstudies.User.Fragment.PdfViewFrag;

import java.util.ArrayList;
import java.util.List;

public class DownloadAdapter extends RecyclerView.Adapter<DownloadAdapter.DownloadViewHolder> {
    List<DownloadedFile> downloadedFileList=new ArrayList<>();
    FragmentManager manager;
    private OnItemClickListener listener;

    public DownloadAdapter(List<DownloadedFile> downloadedFileList,FragmentManager manager) {
        this.downloadedFileList = downloadedFileList;
        this.manager=manager;
    }

    public void setListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public DownloadViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.download_item,parent,false);

        return new DownloadViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DownloadViewHolder holder, int position) {
        DownloadedFile downloadedFile=downloadedFileList.get(position);
        holder.title.setText(downloadedFile.getTopic());
        holder.desc.setText(downloadedFile.getDescription());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*
                PdfViewFrag frag=new PdfViewFrag();
                Bundle bundle=new Bundle();
                bundle.putString("fileName",downloadedFileList.get(holder.getAdapterPosition()).getFileName());
                frag.setArguments(bundle);
                manager.beginTransaction().replace(R.id.fragment_container,frag).addToBackStack(null).commit();

                 */
                //listener.onItemClick(holder.getAdapterPosition());
                Intent intent=new Intent(holder.itemView.getContext(), DownloadedPDFViewActivity.class);
                String filename=downloadedFileList.get(holder.getAdapterPosition()).getFileName();
                intent.putExtra("filename",filename);
                holder.itemView.getContext().startActivity(intent);
                notifyDataSetChanged();

            }
        });
    }

    @Override
    public int getItemCount() {
        return downloadedFileList.size();
    }

    public class DownloadViewHolder extends RecyclerView.ViewHolder {
        TextView title,desc;
        public DownloadViewHolder(@NonNull View itemView) {
            super(itemView);
            title=itemView.findViewById(R.id.tx_pdf_title);
            desc=itemView.findViewById(R.id.tx_pdf_desc);
        }
    }

    public void setFiles(List<DownloadedFile> FileList){
        this.downloadedFileList.clear();
        this.downloadedFileList.addAll(FileList);
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

}

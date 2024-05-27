package com.example.environmentalstudies.User.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.environmentalstudies.R;
import com.example.environmentalstudies.User.Model.boardModel;

import java.util.ArrayList;
import java.util.List;

public class LeaderBoardAdapter extends RecyclerView.Adapter<LeaderBoardAdapter.BoardHolderView> {
    List<boardModel> dataList=new ArrayList<>();
    String from;

    public LeaderBoardAdapter(List<boardModel> dataList,String from) {
        this.dataList = dataList;
        this.from=from;
    }

    @NonNull
    @Override
    public BoardHolderView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.leader_board_item,parent,false);
        return new BoardHolderView(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BoardHolderView holder, int position) {
        boardModel model=dataList.get(position);
        holder.name.setText(model.getName());
        if(from.equals("unit")) {

            holder.time.setText(model.getUtTime()+"Millies");
            holder.number.setText((position+1)+"");
        }else {
            holder.time.setText(model.getFinalTime()+"Millies");
            holder.number.setText((position+1)+"");
        }

       // holder.time.setText(model.get);
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public class BoardHolderView extends RecyclerView.ViewHolder {
        TextView name,time,number;
        ImageView logo;
        public BoardHolderView(@NonNull View itemView) {
            super(itemView);
            name=itemView.findViewById(R.id.tx_name);
            time=itemView.findViewById(R.id.tx_time);
            logo=itemView.findViewById(R.id.img_top);
            number=itemView.findViewById(R.id.tx_num);
        }
    }
}

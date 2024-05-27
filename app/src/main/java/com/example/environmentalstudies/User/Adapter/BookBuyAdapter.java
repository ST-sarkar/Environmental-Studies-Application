package com.example.environmentalstudies.User.Adapter;

import static androidx.core.content.ContextCompat.startActivity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.environmentalstudies.R;
import com.example.environmentalstudies.User.Model.bookBuyModel;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class BookBuyAdapter extends RecyclerView.Adapter<BookBuyAdapter.BookViewHolder> {
    List<bookBuyModel> bookList=new ArrayList<>();

    public BookBuyAdapter(List<bookBuyModel> bookList) {
        this.bookList = bookList;
    }

    @NonNull
    @Override
    public BookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.book_buy_item,parent,false);
        return new BookViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookViewHolder holder, int position) {

        bookBuyModel model=bookList.get(position);
        Context context=holder.itemView.getContext();
        holder.name.setText(model.getBookName());
        holder.auther.setText(model.getAutherName());
        holder.price.setText(String.valueOf(model.getPrice()));
        Glide.with(context).load(model.getUri()).into(holder.imageView);

        holder.buy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Create an Intent with ACTION_VIEW and the website URL
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(bookList.get(holder.getAdapterPosition()).getWeburi()));

                // Check if there's a web browser available to handle the Intent
                //if (intent.resolveActivity(context.getPackageManager()) != null) {
                // Start the activity to open the website
                context.startActivity(intent);
                //} else {
                // If no web browser is available, show a toast message
                //  Toast.makeText(context, "No web browser found", Toast.LENGTH_SHORT).show();
                //}
            }
        });
    }

    @Override
    public int getItemCount() {
        return bookList.size();
    }

    public class BookViewHolder extends RecyclerView.ViewHolder {
        TextView name,auther,price;
        ImageView imageView;
        Button buy;
        public BookViewHolder(@NonNull View itemView) {
            super(itemView);
            name=itemView.findViewById(R.id.book_title);
            auther=itemView.findViewById(R.id.book_auther);
            price=itemView.findViewById(R.id.book_rs);
            imageView=itemView.findViewById(R.id.img_book);
            buy=itemView.findViewById(R.id.btn_buy);
        }
    }
}
   
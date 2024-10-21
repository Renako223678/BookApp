package com.example.bookapp.Adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bookapp.Models.ModelBook;
import com.example.bookapp.R;

import java.util.List;

public class BooksAdapter extends RecyclerView.Adapter<BooksAdapter.BookViewHolder> {

    private Context context;
    private List<ModelBook> bookList;

    public BooksAdapter(Context context, List<ModelBook> bookList) {
        this.context = context;
        this.bookList = bookList;
    }

    @NonNull
    @Override
    public BookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.book_row, parent, false);
        return new BookViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookViewHolder holder, int position) {
        ModelBook book = bookList.get(position);
        holder.titleTextView.setText(book.getTitle());
        holder.descriptionTextView.setText(book.getDescription());

        holder.itemView.setOnClickListener(v -> {
            // Open the PDF when the book item is clicked
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(book.getPdfUrl()));
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return bookList.size();
    }

    static class BookViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView, descriptionTextView;

        BookViewHolder(View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.bookTitleTextView);
            descriptionTextView = itemView.findViewById(R.id.bookDescriptionTextView);
        }
    }
}

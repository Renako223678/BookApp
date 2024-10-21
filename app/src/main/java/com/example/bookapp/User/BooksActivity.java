package com.example.bookapp.User;

import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bookapp.Adapter.BooksAdapter;
import com.example.bookapp.Models.ModelBook;
import com.example.bookapp.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;



public class BooksActivity extends AppCompatActivity {

    private RecyclerView recyclerViewBooks;
    private BooksAdapter booksAdapter;
    private List<ModelBook> bookList;
    private DatabaseReference booksRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_books);

        // Set up the Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Initialize RecyclerView
        recyclerViewBooks = findViewById(R.id.recyclerViewBooks);
        recyclerViewBooks.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewBooks.setHasFixedSize(true);

        // Initialize book list and adapter
        bookList = new ArrayList<>();
        booksAdapter = new BooksAdapter(this, bookList);
        recyclerViewBooks.setAdapter(booksAdapter);

        // Firebase Database reference
        booksRef = FirebaseDatabase.getInstance().getReference("Books");

        // Fetch books from Firebase
        fetchBooks();
    }

    private void fetchBooks() {
        booksRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                bookList.clear();  // Clear the list before adding new data
                for (DataSnapshot bookSnapshot : snapshot.getChildren()) {
                    ModelBook book = bookSnapshot.getValue(ModelBook.class);
                    bookList.add(book);
                }
                booksAdapter.notifyDataSetChanged();  // Notify adapter about data change
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(BooksActivity.this, "Failed to load books.", Toast.LENGTH_SHORT).show();
            }
        });
    }

}


package com.example.bookapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bookapp.Adapter.AdapterCategory;
import com.example.bookapp.Models.ModelCategory;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class AdminDashboardActivity extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;
    private RecyclerView recyclerView;
    private AdapterCategory adapterCategory;
    private ArrayList<ModelCategory> categoryArrayList;
    private SearchView searchView;
    private Button addCategoryButton;
    private FloatingActionButton pdfButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        // Initialize Firebase Auth
        firebaseAuth = FirebaseAuth.getInstance();
        checkUser();

        // Initialize Views
        recyclerView = findViewById(R.id.recyclerView);
        searchView = findViewById(R.id.searchView);
        addCategoryButton = findViewById(R.id.addCategoryButton);
        pdfButton = findViewById(R.id.pdfButton);

        // Initialize RecyclerView and adapter
        categoryArrayList = new ArrayList<>();
        adapterCategory = new AdapterCategory(this, categoryArrayList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapterCategory);

        // Load categories from Firebase
        loadCategoriesFromFirebase();

        // Setup search functionality


        // Add category button click listener
        addCategoryButton.setOnClickListener(v -> {
            // Handle the action to add a new category
            Intent intent = new Intent(AdminDashboardActivity.this, CategoryAddActivity.class);
            startActivity(intent);
        });

        // Export to PDF functionality (if needed)
        pdfButton.setOnClickListener(v -> {
            // Handle the action to generate a PDF report
            exportCategoriesToPDF();
        });
    }

    private void checkUser() {
        // Check if the user is logged in
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user == null) {
            // Redirect to login screen if not logged in
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
    }

    private void loadCategoriesFromFirebase() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Categories");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                categoryArrayList.clear();  // Clear previous data

                for (DataSnapshot ds : snapshot.getChildren()) {
                    ModelCategory category = ds.getValue(ModelCategory.class);
                    if (category != null) {
                        categoryArrayList.add(category);
                    }
                }

                // Notify the adapter that data has changed
                adapterCategory.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AdminDashboardActivity.this, "" + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }





    private void exportCategoriesToPDF() {
        // Code to export the categories to a PDF document
        Log.d("PDF", "Exporting categories to PDF...");
        // You can use libraries like iText or PdfDocument to generate the PDF
    }
}

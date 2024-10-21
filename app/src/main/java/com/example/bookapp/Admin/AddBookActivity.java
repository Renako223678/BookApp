package com.example.bookapp.Admin;

import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.bookapp.Models.ModelBook;
import com.example.bookapp.Models.ModelCategory;
import com.example.bookapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class AddBookActivity extends AppCompatActivity {

    private EditText bookNameInput, bookDescriptionInput;
    private TextView categoryTv, fileNameTv;
    private Button submitBtn, uploadPdfBtn;
    private ImageButton backBtn;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;
    private Uri pdfUri;
    private List<String> categoriesList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_book);

        // Initialize views
        bookNameInput = findViewById(R.id.bookaddInput);
        bookDescriptionInput = findViewById(R.id.bookDescriptionInput);
        categoryTv = findViewById(R.id.categoryTv);
        fileNameTv = findViewById(R.id.fileNameTv);
        submitBtn = findViewById(R.id.submitBtn);
        uploadPdfBtn = findViewById(R.id.uploadPdfBtn);
        backBtn = findViewById(R.id.backBtn);

        // Initialize Firebase references
        databaseReference = FirebaseDatabase.getInstance().getReference("Books");
        storageReference = FirebaseStorage.getInstance().getReference("Books");

        // Load categories from Firebase
        loadCategoriesFromFirebase();

        // Set click listeners
        backBtn.setOnClickListener(view -> {
            Intent intent = new Intent(AddBookActivity.this, AdminDashboardActivity.class);
            startActivity(intent);
        });

        categoryTv.setOnClickListener(v -> showCategoryDialog());
        uploadPdfBtn.setOnClickListener(v -> openFilePicker());
        submitBtn.setOnClickListener(v -> uploadBook());
    }

    private void loadCategoriesFromFirebase() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Categories");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                categoriesList.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    ModelCategory category = ds.getValue(ModelCategory.class);
                    if (category != null) {
                        categoriesList.add(category.getCategory());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AddBookActivity.this, "Failed to load categories", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showCategoryDialog() {
        if (categoriesList.isEmpty()) {
            Toast.makeText(this, "No categories available", Toast.LENGTH_SHORT).show();
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Category");

        // Convert categories list to an array
        String[] categoriesArray = categoriesList.toArray(new String[0]);

        builder.setItems(categoriesArray, (dialog, which) -> {
            String selectedCategory = categoriesArray[which];
            categoryTv.setText(selectedCategory);
        });

        builder.create().show();
    }

    private void openFilePicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("application/pdf");
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            pdfUri = data.getData();
            if (pdfUri != null) {
                fileNameTv.setText(getFileName(pdfUri));
            }
        }
    }

    private String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            try (Cursor cursor = getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    result = cursor.getString(nameIndex);
                }
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

    private void uploadBook() {
        String bookTitle = bookNameInput.getText().toString().trim();
        String bookDescription = bookDescriptionInput.getText().toString().trim();
        String category = categoryTv.getText().toString().trim();
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        if (bookTitle.isEmpty() || bookDescription.isEmpty() || category.isEmpty() || pdfUri == null) {
            Toast.makeText(this, "Please fill all fields and upload a PDF", Toast.LENGTH_SHORT).show();
            return;
        }

        // Show a loading dialog (optional)
        // Upload PDF to Firebase Storage
        final StorageReference pdfRef = storageReference.child(System.currentTimeMillis() + ".pdf");
        pdfRef.putFile(pdfUri).addOnSuccessListener(taskSnapshot -> {
            pdfRef.getDownloadUrl().addOnSuccessListener(downloadUrl -> {
                String bookId = databaseReference.push().getKey();
                if (bookId != null) {
                    ModelBook book = new ModelBook(bookId, uid, bookTitle, category, bookDescription, downloadUrl.toString());
                    databaseReference.child(bookId).setValue(book).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(AddBookActivity.this, "Book uploaded successfully", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(AddBookActivity.this, "Failed to upload book: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
        }).addOnFailureListener(e -> {
            Toast.makeText(AddBookActivity.this, "Upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }
}

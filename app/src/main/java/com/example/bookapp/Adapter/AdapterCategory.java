package com.example.bookapp.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bookapp.Models.ModelCategory;
import com.example.bookapp.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class AdapterCategory extends RecyclerView.Adapter<AdapterCategory.CategoryViewHolder> {
    private Context context;
    private ArrayList<ModelCategory> categoryList;

    // Constructor
    public AdapterCategory(Context context, ArrayList<ModelCategory> categoryList) {
        this.context = context;
        this.categoryList = categoryList;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the layout for each category item
        View view = LayoutInflater.from(context).inflate(R.layout.row_category, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        // Get current category
        ModelCategory category = categoryList.get(position);

        // Bind data to the views
        holder.categoryNameTextView.setText(category.getCategory());
        //delete category
        // Handle delete category action
        holder.deleteCategoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteCategory(category.getId());  // Call the delete method with category ID
            }
        });
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    // ViewHolder class to hold each category item view
    class CategoryViewHolder extends RecyclerView.ViewHolder {
        TextView categoryNameTextView;
        Button deleteCategoryButton;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            // Initialize views
            categoryNameTextView = itemView.findViewById(R.id.categoryTitleTextView);
            deleteCategoryButton = itemView.findViewById(R.id.deleteCategoryButton);
        }
    }
    // Method to delete category from Firebase
    private void deleteCategory(String categoryId) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Categories");
        ref.child(categoryId)
                .removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Category deleted successfully
                        Toast.makeText(context, "Category deleted successfully...", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Deletion failed
                        Toast.makeText(context, "Failed to delete category: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

}

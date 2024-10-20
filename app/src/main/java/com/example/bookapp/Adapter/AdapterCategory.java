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
    private ArrayList<ModelCategory> categoryList; // Original list
    private ArrayList<ModelCategory> filteredCategoryList; // Filtered list

    // Constructor
    public AdapterCategory(Context context, ArrayList<ModelCategory> categoryList) {
        this.context = context;
        this.categoryList = categoryList;
        this.filteredCategoryList = new ArrayList<>(categoryList);
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_category, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        ModelCategory category = filteredCategoryList.get(position); // Use filtered list

        holder.categoryNameTextView.setText(category.getCategory());

        // Handle delete category action
        holder.deleteCategoryButton.setOnClickListener(v -> deleteCategory(category.getId()));
    }

    @Override
    public int getItemCount() {
        return filteredCategoryList.size(); // Return filtered list size
    }

    class CategoryViewHolder extends RecyclerView.ViewHolder {
        TextView categoryNameTextView;
        Button deleteCategoryButton;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            categoryNameTextView = itemView.findViewById(R.id.categoryTitleTextView);
            deleteCategoryButton = itemView.findViewById(R.id.deleteCategoryButton);
        }
    }

    private void deleteCategory(String categoryId) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Categories");
        ref.child(categoryId).removeValue()
                .addOnSuccessListener(aVoid -> Toast.makeText(context, "Category deleted successfully...", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(context, "Failed to delete category: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    public void filter(String query) {
        filteredCategoryList.clear();  // Clear the current filtered list

        if (query.isEmpty()) {
            filteredCategoryList.addAll(categoryList);  // Show all categories if query is empty
        } else {
            for (ModelCategory category : categoryList) {
                if (category.getCategory().toLowerCase().contains(query.toLowerCase())) {
                    filteredCategoryList.add(category);  // Add matching categories
                }
            }
        }
        notifyDataSetChanged();  // Notify the adapter to refresh the view
    }
}

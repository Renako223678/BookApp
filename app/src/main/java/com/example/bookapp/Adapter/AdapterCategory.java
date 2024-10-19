package com.example.bookapp.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bookapp.Models.ModelCategory;
import com.example.bookapp.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class AdapterCategory extends RecyclerView.Adapter<AdapterCategory.HolderCategory> implements Filterable {

    private Context context;
    private ArrayList<ModelCategory> categoryArrayList; // Original list
    private ArrayList<ModelCategory> filteredCategoryList; // Filtered list

    // Constructor
    public AdapterCategory(Context context, ArrayList<ModelCategory> categoryArrayList) {
        this.context = context;
        this.categoryArrayList = categoryArrayList;
        this.filteredCategoryList = new ArrayList<>(categoryArrayList); // Initialize filtered list
    }

    @NonNull
    @Override
    public HolderCategory onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the custom row layout for each category
        View view = LayoutInflater.from(context).inflate(R.layout.row_category, parent, false);
        return new HolderCategory(view);
    }

    // ViewHolder class to hold each category item
    public class HolderCategory extends RecyclerView.ViewHolder {

        // Declare views from the row_category.xml layout
        TextView categoryTitle;
        Button deleteBTN;

        public HolderCategory(@NonNull View itemView) {
            super(itemView);

            // Initialize views
            categoryTitle = itemView.findViewById(R.id.category_title);
            deleteBTN = itemView.findViewById(R.id.ic_delete);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull HolderCategory holder, int position) {
        // Get the data for the current category
        ModelCategory modelCategory = filteredCategoryList.get(position); // Use filtered list

        // Set the category title to the TextView
        holder.categoryTitle.setText(modelCategory.getCategory());

        // Set the click listener for the delete button
        holder.deleteBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Call method to delete the category from Firebase
                deleteCategory(modelCategory);
            }
        });
    }

    @Override
    public int getItemCount() {
        // Return the total number of filtered categories
        return filteredCategoryList.size();
    }

    // Method to delete a category from Firebase
    private void deleteCategory(ModelCategory modelCategory) {
        // Get reference to the category in Firebase using its ID
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Categories");
        ref.child(modelCategory.getId()).removeValue()
                .addOnSuccessListener(aVoid -> {
                    // Success - you can notify the user if needed
                    categoryArrayList.remove(modelCategory); // Remove the item from the original list
                    filteredCategoryList.remove(modelCategory); // Remove from the filtered list
                    notifyDataSetChanged(); // Notify adapter to update the UI
                })
                .addOnFailureListener(e -> {
                    // Failure - you can notify the user if needed
                });
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String searchText = constraint.toString().toLowerCase();
                FilterResults results = new FilterResults();

                if (searchText.isEmpty()) {
                    // If the search text is empty, return the original list
                    results.values = categoryArrayList;
                    results.count = categoryArrayList.size();
                } else {
                    // Create a new list to hold filtered categories
                    List<ModelCategory> filteredList = new ArrayList<>();
                    for (ModelCategory category : categoryArrayList) {
                        // Check if the category title contains the search text
                        if (category.getCategory().toLowerCase().contains(searchText)) {
                            filteredList.add(category);
                        }
                    }
                    results.values = filteredList;
                    results.count = filteredList.size();
                }

                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                // Update the filtered category list and notify the adapter
                filteredCategoryList = (ArrayList<ModelCategory>) results.values;
                notifyDataSetChanged();
            }
        };
    }
}

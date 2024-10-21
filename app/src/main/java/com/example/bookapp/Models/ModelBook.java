package com.example.bookapp.Models;

public class ModelBook {
    private String bookId;
    private String uid;
    private String title;
    private String category;
    private String description;
    private String pdfUrl;

    public ModelBook() {
        // Default constructor required for calls to DataSnapshot.getValue(ModelBook.class)
    }

    public ModelBook(String bookId, String uid, String title, String category, String description, String pdfUrl) {
        this.bookId = bookId;
        this.uid = uid;
        this.title = title;
        this.category = category;
        this.description = description;
        this.pdfUrl = pdfUrl;
    }

    // Getters and setters
    public String getBookId() {
        return bookId;
    }

    public String getUid() {
        return uid;
    }

    public String getTitle() {
        return title;
    }

    public String getCategory() {
        return category;
    }

    public String getDescription() {
        return description;
    }

    public String getPdfUrl() {
        return pdfUrl;
    }
}

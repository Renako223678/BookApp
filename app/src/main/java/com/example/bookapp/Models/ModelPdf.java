package com.example.bookapp.Models;

public class ModelPdf {
    String uid,id,title,description,category,url;
    long timestamp;
    public ModelPdf() {
    }
    public ModelPdf(String uid, String id, String title, String description, String category, String url, long timestamp){
        this.uid=uid;
        this.id=id;
        this.title=title;
        this.url = url;
        this.description=description;
        this.category=category;
        this.timestamp = timestamp;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}

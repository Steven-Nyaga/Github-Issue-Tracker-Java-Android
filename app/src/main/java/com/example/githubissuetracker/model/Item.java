package com.example.githubissuetracker.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Item {

    @SerializedName("title")
    @Expose
    private String title;

    @SerializedName("created_at")
    @Expose
    private String created_at;

    @SerializedName("comments")
    @Expose
    private String comments;

    @SerializedName("html_url")
    @Expose
    private String url;


    public Item(String title, String created_at, String comments, String url){
        this.title = title;
        this.created_at = created_at;
        this.comments = comments;
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    private List<Item> items;

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }
}

package com.myapp.android.popularmovies.models;

public class ReviewItem{
    private static final String LOG_TAG = ReviewItem.class.getSimpleName();
    private static final boolean DEBUG = false; // Set this to false to disable logs.



    private String author;
    private String content;



    public String getAuthor(){ return author;}

    public void setAuthor(String author){
        this.author = author;
    }

    public String getContent(){ return  content; }

    public void setContent(String content){
        this.content = content;
    }

    private ReviewItem() {
    }

}


package com.myapp.android.popularmovies.models;

public class TrailerItem {
    private static final String LOG_TAG = TrailerItem.class.getSimpleName();
    private static final boolean DEBUG = false; // Set this to false to disable logs.



    private String name;
    private String key;
    private String site;


    public String getName(){ return name;}

    public void setName(String name){
        this.name = name;
    }

    public String getKey(){ return  key; }

    public void setKey(String key){
        this.key = key;
    }

    public String getSite(){ return  site; }

    public void setSite(String site){
        this.site = site;
    }


    private TrailerItem() {
    }

}


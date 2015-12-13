package com.myapp.android.popularmovies.rests;


import com.myapp.android.popularmovies.MovieDB;
import com.myapp.android.popularmovies.models.ReviewItem;
import com.myapp.android.popularmovies.models.TrailerItem;


import java.util.List;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;

public interface MovieDBApi {


    @GET("/movie/{id}/reviews")
    void getReviews(@Path(MovieDB.ID_PARAM) String id, @Query(MovieDB.API_KEY_PARAM) String key,
                   Callback<List<ReviewItem>> callback);

    @GET("/movie/{id}/videos")
    void getVideos(@Path(MovieDB.ID_PARAM) String id, @Query(MovieDB.API_KEY_PARAM) String key,
                    Callback<List<TrailerItem>> callback);




}



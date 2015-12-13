package com.myapp.android.popularmovies.rests;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.myapp.android.popularmovies.MovieDB;

import retrofit.RestAdapter;
import retrofit.converter.GsonConverter;

/**
 * Created by ashbey on 8/20/2015.
 * This class is used to fetch data from TMDB server
 */

public class RestClient {
    // Keep a copy of our API service cached
    private static MovieDBApi moviedbApi;
    public static MovieDBApi getMovieDBApiClient(){

        if(moviedbApi == null){
            /*
            Gson gson = new GsonBuilder()
                    .setDateFormat("yyyy-MM-dd")
                    .create();
            */
            Gson gson = new GsonBuilder()
                    .registerTypeAdapterFactory(new ItemTypeAdapterFactory())
                    .setDateFormat("yyyy-MM-dd")
                    .create();
            RestAdapter restAdapter = new RestAdapter.Builder()
                    .setEndpoint(MovieDB.BASE_URL)
                    .setConverter(new GsonConverter(gson))
                    .build();
            moviedbApi = restAdapter.create(MovieDBApi.class);
        }
        return moviedbApi;
    }
}



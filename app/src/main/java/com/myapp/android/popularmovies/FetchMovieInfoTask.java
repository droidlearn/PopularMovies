package com.myapp.android.popularmovies;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.myapp.android.popularmovies.data.MovieContract;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Vector;

public class FetchMovieInfoTask extends AsyncTask<String, Void, Void> {

    private final String TAG = FetchMovieInfoTask.class.getSimpleName();

    private final Context mContext;

    public FetchMovieInfoTask(Context context) {
        mContext = context;

    }

    private void getMovieDataFromJson(String movieResponseJsonStr, String pivot)
            throws JSONException {


        // These are the names of the JSON objects that need to be extracted.
        //original title
        //movie poster image thumbnail
        //A plot synopsis (called overview in the api)
        //user rating (called vote_average in the api)
        //release date

        final String TMD_id = "id";
        final String TMD_results = "results";
        final String TMD_original_title = "original_title";
        final String TMD_poster_image = "poster_path";
        final String TMD_plot_synopsis = "overview";
        final String TMD_user_rating = "vote_average";
        final String TMD_release_date = "release_date";
        final String TMD_backdrop_path = "backdrop_path";
        final String TMD_vote_count = "vote_count";



        try
        {
            //Validate we have a valid Json String
            if (!isValidJSON(movieResponseJsonStr))
                return;

            JSONObject movieJson = new JSONObject(movieResponseJsonStr);


            JSONArray resultsArray = movieJson.getJSONArray(TMD_results);

            Log.d(TAG, "Movie info length = " + resultsArray.length());


            // xxx Before adding a new movieInfo for a pivot, Delete existing movies for the pivot to update new data.

            // Insert the new movie information into the database
            Vector<ContentValues> cVVector = new Vector<ContentValues>(resultsArray.length());


            for (int i = 0; i < resultsArray.length(); i++) {

                JSONObject movieInfo = resultsArray.getJSONObject(i);

                String NA = "Not Available";
                String id = NA;
                String original_title = NA;
                String poster_image = NA;
                String plot_synopsis = NA;
                String user_rating = NA;
                String release_date = NA;
                String backdrop_path = NA;
                String vote_count = NA;



                if (null != movieInfo.getString(TMD_id) && ! JSONObject.NULL.equals(movieInfo.get(TMD_id)) )
                    id = (String) movieInfo.getString(TMD_id);


                if (null != movieInfo.get(TMD_original_title) && ! JSONObject.NULL.equals(movieInfo.get(TMD_original_title)))
                    original_title = (String) movieInfo.get(TMD_original_title);

                if (null != movieInfo.get(TMD_poster_image) && ! JSONObject.NULL.equals(movieInfo.get(TMD_poster_image)) )
                    poster_image = (String) movieInfo.get(TMD_poster_image);

                if (null != movieInfo.get(TMD_plot_synopsis) && ! JSONObject.NULL.equals(movieInfo.get(TMD_plot_synopsis)) )
                    plot_synopsis = (String) movieInfo.get(TMD_plot_synopsis);

                if (null != movieInfo.get(TMD_release_date) && ! JSONObject.NULL.equals(movieInfo.get(TMD_release_date)) ) {
                    String dt = (String) movieInfo.get(TMD_release_date);
                    DateFormat originalFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
                    DateFormat targetFormat = new SimpleDateFormat("MMMM dd, yyyy", Locale.ENGLISH);
                    Date date = null;
                    try {
                        date = originalFormat.parse(dt);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    release_date = targetFormat.format(date);
                }

                if (null != movieInfo.get(TMD_backdrop_path) && ! JSONObject.NULL.equals(movieInfo.get(TMD_backdrop_path)) )
                    backdrop_path = (String) movieInfo.get(TMD_backdrop_path);

                if (null != movieInfo.get(TMD_user_rating) && ! JSONObject.NULL.equals(movieInfo.get(TMD_user_rating)) ) {
                    String urating = String.valueOf(movieInfo.get(TMD_user_rating));

                    if (null != movieInfo.get(TMD_vote_count) && ! JSONObject.NULL.equals(movieInfo.get(TMD_vote_count)) ) {
                        Integer vc = movieInfo.getInt(TMD_vote_count);
                        vote_count = vc.toString();
                    }

                    user_rating = urating  + " from " + vote_count + " reviews";

                }


                Log.d(TAG, "Movie Info for ****  id: (" + i + ")  = " + id);
                Log.d(TAG, "title  : " + original_title);
                Log.d(TAG, "image  : " + poster_image);
                Log.d(TAG, "plot   : " + plot_synopsis);
                Log.d(TAG, "rating : " + user_rating);
                Log.d(TAG, "release: " + release_date);


                ContentValues movieValues = new ContentValues();

                movieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_KEY, id);
                movieValues.put(MovieContract.MovieEntry.COLUMN_ORIGINAL_TITLE, original_title);
                movieValues.put(MovieContract.MovieEntry.COLUMN_POSTER_IMAGE, poster_image);
                movieValues.put(MovieContract.MovieEntry.COLUMN_PLOT_SYNOPSIS, plot_synopsis);
                movieValues.put(MovieContract.MovieEntry.COLUMN_USER_RATING, user_rating);
                movieValues.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE, release_date);
                movieValues.put(MovieContract.MovieEntry.COLUMN_BACKDROP_PATH, backdrop_path);
                movieValues.put(MovieContract.MovieEntry.COLUMN_PIVOT, pivot);

                cVVector.add(movieValues);


            }

            // Delete old data before inserting new data
            //mIsData = true;
            if ( cVVector.size() > 0 ) {

                int deleted = mContext.getContentResolver().delete(MovieContract.MovieEntry.CONTENT_URI, MovieContract.MovieEntry.COLUMN_PIVOT  + " = ?", new String[]{pivot});
                Log.d(TAG, "FetchMovieTask deleted stale data. " + deleted + " deleted");

            }



            int inserted = 0;
            // add to database
            if ( cVVector.size() > 0 ) {
                ContentValues[] cvArray = new ContentValues[cVVector.size()];
                cVVector.toArray(cvArray);
                inserted = mContext.getContentResolver().bulkInsert(MovieContract.MovieEntry.CONTENT_URI, cvArray);

            }

            Log.d(TAG, "FetchMovieTask Complete. " + inserted + " Inserted");


        }
        catch (JSONException e)
        {
            Log.e(TAG, e.getMessage(), e);
            e.printStackTrace();
        }


        /* Debug code start

        //String sortOrder = MovieContract.MovieEntry.COLUMN_MOVIE_KEY + " ASC";
        Uri moviesForPopularSettingUri = MovieContract.MovieEntry.buildMovieWithPopularSetting(pivot);


        Cursor cur = mContext.getContentResolver().query(MovieContract.MovieEntry.CONTENT_URI, null,
                MovieContract.MovieEntry.COLUMN_PIVOT + " =?", new String[] {pivot}, null);
        cVVector = new Vector<ContentValues>(cur.getCount());
        if (cur.moveToFirst())
        {
            do {
                ContentValues cv = new ContentValues();
                DatabaseUtils.cursorRowToContentValues(cur, cv);
                cVVector.add(cv);
            } while (cur.moveToNext());

        }

        cur.close();

        Log.d(TAG, "FetchMovieTask Complete. " + cVVector.size() + " read");


        //return(convertContentValuesToMovieInfos(cVVector));

       Debug code end */

    }




    @Override
    protected Void doInBackground(String... params) {

        // These two need to be declared outside the try/catch
        // so that they can be closed in the finally block.
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // Will contain the raw JSON response as a string.
        String popularMoviesStr = null;

        try {
            // Construct the URL for obtaining Popular Movies data query
            // Possible parameters are avaiable at https://www.themoviedb.org/documentation/api/discover
            // http://openweathermap.org/API#forecast
            // By rating http://api.themoviedb.org/3/discover/movie?sort_by=vote_average.desc&api_key=12345678
            // By popularity http://api.themoviedb.org/3/discover/movie?sort_by=popularity.desc&api_key=12345678

            final String MOVIE_BASE_URL = "http://api.themoviedb.org/3/discover/movie?primary_release_year=2015";
            final String QUERY_PARAM = "sort_by";

            // For future use
            final String COUNTRY_PARAM = "certification_country";
            final String RATINGS_PARAM = "certification";

            //API key
            final String API_KEY = "api_key";

            //What are the highest rated movies rated R?
            //URL: /discover/movie/?certification_country=US&certification=R&sort_by=vote_average.desc
            //What are the most popular kids movies?
            //URL: /discover/movie?certification_country=US&certification.lte=G&sort_by=popularity.desc
            //Poster http://image.tmdb.org/t/p/w185//qARJ35IrJNFzFWQGcyWP4r1jyXE.jpg

            String pivot = params[0];

            Uri builtUri = Uri.parse(MOVIE_BASE_URL).buildUpon()
                    .appendQueryParameter(QUERY_PARAM, params[0])
                    .appendQueryParameter(API_KEY, mContext.getResources().getString(R.string.API_DEV_KEY))
                    .build();

            Log.v(TAG, "Built URI=" + builtUri.toString());

            URL url = new URL(builtUri.toString());

            // Create the request to themoviedb, and open the connection
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                // Nothing to do.
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                // But it does make debugging a *lot* easier if you print out the completed
                // buffer for debugging.
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                return null;
            }

            popularMoviesStr = buffer.toString();
            if (null != popularMoviesStr)
                Log.v(TAG, "Server response: " + popularMoviesStr);

            getMovieDataFromJson(popularMoviesStr, pivot);

        }
        catch (IOException e) {
            Log.e(TAG, "Error ", e);
            // If the code didn't successfully get the Movie data, there's no point in attemping
            // to parse it.

        }
        catch (JSONException e) {
            Log.e(TAG, e.getMessage(), e);
            e.printStackTrace();
        }

        finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(TAG, "Error closing stream", e);
                }
            }
        }

        return null;

    }

    private boolean isValidJSON(String jsonStr) {
        try {

            new JSONObject(jsonStr);
        } catch (JSONException ex) {
            try {
                new JSONArray(jsonStr);
            } catch (JSONException exe) {
                return false;
            }
        }

        return true;

    }

}




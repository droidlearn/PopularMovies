package com.myapp.android.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

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
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * A placeholder fragment containing a simple view.
 */
public class MovieFragment extends Fragment {


    private final String TAG = MovieFragment.class.getSimpleName();

    private final String API_DEV_KEY = "XXXXXXXXXXXXXXXXXXXXXX.";

    private ImageAdapter mImageAdapter;

    private ArrayList<MovieInfo> movieInfos;

    private GridView gridView;

    private String mSortBy = null;

    private boolean mIsData = false;

    private SharedPreferences.OnSharedPreferenceChangeListener mListener = null;

    public MovieFragment() {
        movieInfos = new ArrayList<MovieInfo>();
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        Log.d(TAG, "onSaveInstanceState called");
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList("movies", movieInfos);

    }

    /*
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        Log.v(TAG, "onActivityCreated called");
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            movieInfos = savedInstanceState.getParcelableArrayList("movies");
        }
        else
        {
            if (null == movieInfos || movieInfos.isEmpty())
            {
                movieInfos = new ArrayList<MovieInfo>();
                updateMovies();
            }
            //returning from back stack, data is fine, no action is needed
        }
    }
    */

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate called");
        super.onCreate(savedInstanceState);

        SharedPreferences prefs =
                PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());

            mListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {

                /* When preferences change data needs to be re-fetched due to change in conditions */
                mIsData = false;

            }
        };


        prefs.registerOnSharedPreferenceChangeListener(mListener);

        if(savedInstanceState == null || !savedInstanceState.containsKey("movies")) {
           movieInfos = new ArrayList<MovieInfo>();
           mIsData = false;
        }
        else {
            Log.d(TAG, "onCreate using savedInstanceState to refresh the view");
            /*
            if (null != mImageAdapter) {
                mImageAdapter.clear();
                mImageAdapter.notifyDataSetChanged();
            }
            */
            movieInfos = savedInstanceState.getParcelableArrayList("movies");
            Log.d(TAG, "onCreate using savedInstanceState Size of data retrieved = " + movieInfos.size());
            mIsData = true;
            /*
            //mImageAdapter.notifyDataSetChanged();
            mImageAdapter = new ImageAdapter(getActivity().getApplicationContext(), movieInfos);
            gridView.setAdapter(mImageAdapter);
             */
        }

        // Add this line in order for this fragment to handle menu events.
        setHasOptionsMenu(true);



    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.movie_fragment, menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }






    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Log.d(TAG, "onCreateView called");


        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        mImageAdapter = new ImageAdapter(getActivity().getApplicationContext(), movieInfos);

        // Get a reference to the GridView, and attach this adapter to it.

        gridView = (GridView) rootView.findViewById(R.id.gridview_movies_layout);
        gridView.setAdapter(mImageAdapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {


            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                MovieInfo mi = mImageAdapter.getItem(position);

                //Context context = getActivity().getApplicationContext();
                //Toast.makeText(context, "" + position + mi.original_title,
                //      Toast.LENGTH_SHORT).show();


                MovieInfo mip = new MovieInfo(mi.id, mi.original_title, mi.poster_image, mi.plot_synopsis, mi.user_rating, mi.release_date, mi.backdrop_path);

                Intent intent = new Intent(getActivity(), DetailActivity.class);
                intent.putExtra("com.myapp.android.popularmovies.SelectedMovie", mip);

                startActivity(intent);


            }


        });

        return rootView;
    }

    @Override
    public void onStart() {
        Log.d(TAG, "onStart called");
        super.onStart();

        // Fetch new data on preference change or when there is no data to begin with
        if (!mIsData) {
            movieInfos = new ArrayList<MovieInfo>();
            updateMovies();

        }

    }


    //Based on a stackoverflow snippet
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


    private void updateMovies() {

        if (! isNetworkAvailable())
        {
            Log.i(TAG, "No network connectivity available ");
            return;
        }

        FetchMovieInfoTask movieInfoTask = new FetchMovieInfoTask();


        SharedPreferences sharedPrefs =
                PreferenceManager.getDefaultSharedPreferences(getActivity());
        String sortby = sharedPrefs.getString(
                getString(R.string.pref_sort_key),
                getString(R.string.pref_sort_most_popular));

        Log.v(TAG, "Sort by = " + sortby);
        mSortBy = sortby;


        movieInfoTask.execute(mSortBy);
    }




    public class FetchMovieInfoTask extends AsyncTask<String, Void, ArrayList<MovieInfo>> {

        private final String TAG = FetchMovieInfoTask.class.getSimpleName();


        private ArrayList<MovieInfo> getMovieDataFromJson(String movieResponseJsonStr)
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


            //Validate we have a valid Json String
            if (!isValidJSON(movieResponseJsonStr))
                return null;

            JSONObject movieJson = new JSONObject(movieResponseJsonStr);


            JSONArray resultsArray = movieJson.getJSONArray(TMD_results);

            Log.d(TAG, "Movie info length = " + resultsArray.length());


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
                    Double user_rating_db = (Double) movieInfo.get(TMD_user_rating);

                    if (null != movieInfo.get(TMD_vote_count) && ! JSONObject.NULL.equals(movieInfo.get(TMD_vote_count)) ) {
                        Integer vc = movieInfo.getInt(TMD_vote_count);
                        vote_count = vc.toString();
                    }

                    user_rating = Double.toString(user_rating_db) + " from " + vote_count + " reviews";

                }


                Log.v(TAG, "Movie Info for ****  id: (index) " + id + "(" + i + ")");
                Log.v(TAG, "title  : " + original_title);
                Log.v(TAG, "image  : " + poster_image);
                Log.v(TAG, "plot   : " + plot_synopsis);
                Log.v(TAG, "rating : " + user_rating);
                Log.v(TAG, "release: " + release_date);


                movieInfos.add(i, new MovieInfo(id, original_title, poster_image, plot_synopsis, user_rating, release_date, backdrop_path));
            }

            mIsData = true;
            return movieInfos;
        }


        @Override
        protected ArrayList<MovieInfo> doInBackground(String... params) {

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
                // By rating http://api.themoviedb.org/3/discover/movie?sort_by=vote_average.desc&api_key=011a293a33a5da37413bddc072d45e35
                // By popularity http://api.themoviedb.org/3/discover/movie?sort_by=popularity.desc&api_key=011a293a33a5da37413bddc072d45e35

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


                Uri builtUri = Uri.parse(MOVIE_BASE_URL).buildUpon()
                        .appendQueryParameter(QUERY_PARAM, params[0])
                        .appendQueryParameter(API_KEY, API_DEV_KEY)
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


            } catch (IOException e) {
                Log.e(TAG, "Error ", e);
                // If the code didn't successfully get the Movie data, there's no point in attemping
                // to parse it.
                return null;
            } finally {
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

            //Poster http://image.tmdb.org/t/p/w185//qARJ35IrJNFzFWQGcyWP4r1jyXE.jpg
            //

            try {
                return getMovieDataFromJson(popularMoviesStr);
            } catch (JSONException e) {
                Log.e(TAG, e.getMessage(), e);
                e.printStackTrace();
            }

            return null;

        }

        @Override
        protected void onPostExecute(ArrayList<MovieInfo> result) {
            Log.d(TAG, "onPostExecute called");
            super.onPostExecute(result);
            if (result != null) {
                Log.d(TAG, "onPostExecute, clearing and re-init of adapter");
                mImageAdapter.clear();
                mImageAdapter.notifyDataSetChanged();
                mImageAdapter = new ImageAdapter(getActivity().getApplicationContext(), result);
                gridView.setAdapter(mImageAdapter);

            }

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


    }

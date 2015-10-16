package com.myapp.android.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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

import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class MovieFragment extends Fragment {


    private final String TAG = MovieFragment.class.getSimpleName();

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

            movieInfos = savedInstanceState.getParcelableArrayList("movies");
            Log.d(TAG, "onCreate using savedInstanceState Size of data retrieved = " + movieInfos.size());
            mIsData = true;
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

        FetchMovieInfoTask movieInfoTask = new FetchMovieInfoTask(getActivity(), mImageAdapter, gridView, movieInfos, mIsData);

        SharedPreferences sharedPrefs =
                PreferenceManager.getDefaultSharedPreferences(getActivity());
        String sortby = sharedPrefs.getString(
                getString(R.string.pref_sort_key),
                getString(R.string.pref_sort_most_popular));

        Log.d(TAG, "Sort by = " + sortby);
        mSortBy = sortby;


        movieInfoTask.execute(mSortBy);
    }


    }

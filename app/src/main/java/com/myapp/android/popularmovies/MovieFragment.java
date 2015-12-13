package com.myapp.android.popularmovies;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.myapp.android.popularmovies.data.MovieContract;


/**
 * A placeholder fragment containing a simple view.
 */
public class MovieFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{


    private static final int MOVIE_LOADER = 0;

    private final String TAG = MovieFragment.class.getSimpleName();

    private MovieCursorAdapter mCursorAdapter;

    //private ArrayList<MovieInfo> movieInfos;

    private GridView gridView;


    private boolean mIsData = false;

    private int mPosition;
    private SharedPreferences.OnSharedPreferenceChangeListener mListener = null;

    private String mPivot = null;

    public MovieFragment() {
        //movieInfos = new ArrayList<MovieInfo>();
    }


    private static final String[] MOVIE_COLUMNS = {

            MovieContract.MovieEntry._ID,
            MovieContract.MovieEntry.COLUMN_MOVIE_KEY,
            MovieContract.MovieEntry.COLUMN_ORIGINAL_TITLE,
            MovieContract.MovieEntry.COLUMN_PIVOT,
            MovieContract.MovieEntry.COLUMN_USER_RATING,
            MovieContract.MovieEntry.COLUMN_BACKDROP_PATH,
            MovieContract.MovieEntry.COLUMN_POSTER_IMAGE,
            MovieContract.MovieEntry.COLUMN_PLOT_SYNOPSIS,
            MovieContract.MovieEntry.COLUMN_RELEASE_DATE

    };


    static final int COL_MOVIE_ID = 0;
    static final int COL_MOVIE_KEY = 1;
    static final int COL_MOVIE_ORIGINAL_TITLE = 2;
    static final int COL_MOVIE_PIVOT = 3;
    static final int COL_MOVIE_USER_RATING = 4;
    static final int COL_MOVIE_BACKDROP_PATH = 5;
    static final int COL_MOVIE_POSTER_IMAGE = 6;
    static final int COL_MOVIE_PLOT_SYNOPSIS = 7;
    static final int COL_MOVIE_RELEASE_DATE = 8;



    @Override
    public void onSaveInstanceState(Bundle outState) {
        Log.d(TAG, "onSaveInstanceState called");
        super.onSaveInstanceState(outState);


    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate called");

        super.onCreate(savedInstanceState);

        if(savedInstanceState == null)
        {

            SharedPreferences sharedPrefs =
                    PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
            mPivot = sharedPrefs.getString(
                    getString(R.string.pref_sort_key),
                    getString(R.string.pref_sort_most_popular));

            mPosition = 0;

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


        SharedPreferences sharedPrefs =
                PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
        String mPivotStr = sharedPrefs.getString(
                getString(R.string.pref_sort_key),
                getString(R.string.pref_sort_most_popular));

        // Some change in list item
        if (mPivotStr != mPivot)
        {
            mPivot = mPivotStr;
            mPosition = -1;
            onPivotChanged();
        }

        // xxx Just added from ForecastFragment.xml and also added the button to menu movie_fragment.xml
        if (id == R.id.action_refresh) {
            updateMovies();
            return true;
        }



        return super.onOptionsItemSelected(item);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Log.d(TAG, "onCreateView called");



        mCursorAdapter = new MovieCursorAdapter(getActivity().getApplicationContext(), null, 0);

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        // Get a reference to the GridView, and attach this adapter to it.

        gridView = (GridView) rootView.findViewById(R.id.gridview_movies_layout);
        gridView.setAdapter(mCursorAdapter);


        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {


            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {


                Context context = getActivity().getApplicationContext();
                //Toast.makeText(context, "" + position ,
                  //            Toast.LENGTH_SHORT).show();


                Cursor cursor = (Cursor) adapterView.getItemAtPosition(position);

                if (cursor != null) {
                    SharedPreferences sharedPrefs =
                            PreferenceManager.getDefaultSharedPreferences(getActivity());
                    String pivot = sharedPrefs.getString(
                            getString(R.string.pref_sort_key),
                            getString(R.string.pref_sort_most_popular));

                    Log.i(TAG, "Invoking callback for = " + pivot);



                    if (pivot.equalsIgnoreCase("favorites.desc"))
                        ((Callback)getActivity()).onItemSelected(MovieContract.FavoriteEntry.buildFavoriteUri(cursor.getLong(COL_MOVIE_KEY)));
                    else
                        ((Callback)getActivity()).onItemSelected(MovieContract.MovieEntry.buildMovieWithSettingByMovieId(pivot,
                                cursor.getString(COL_MOVIE_KEY)));

                    mPosition = position;



                }
                else
                {

                    Log.v(TAG, "Detail Cursor is null");
                }

            }

        });



        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
            gridView.setNumColumns(3);
        } else{
            gridView.setNumColumns(2);
        }


        return rootView;
    }


    /**
     * A callback interface that all activities containing this fragment must
     * implement. This mechanism allows activities to be notified of item
     * selections.
     */
    public interface Callback {
        /**
         * DetailFragmentCallback for when an item has been selected.
         */
        public void onItemSelected(Uri movieUri);
    }




    //Based on a stackoverflow snippet
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {

        if(savedInstanceState == null) {
            updateMovies();
        }

        getLoaderManager().initLoader(MOVIE_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }


    // since we read the pivot when we create the loader, all we need to do is restart things
    void onPivotChanged() {

        updateMovies();
        getLoaderManager().restartLoader(MOVIE_LOADER, null, this);
    }


    private void updateMovies() {

        if (! isNetworkAvailable())
        {
            Log.i(TAG, "No network connectivity available ");
            return;
        }


        FetchMovieInfoTask movieInfoTask = new FetchMovieInfoTask(getActivity());
        SharedPreferences sharedPrefs =
                PreferenceManager.getDefaultSharedPreferences(getActivity());
        String pivot = sharedPrefs.getString(
                getString(R.string.pref_sort_key),
                getString(R.string.pref_sort_most_popular));

        Log.d(TAG, "Sort by = " + pivot);


        mPivot = pivot;

        if (mPivot.equalsIgnoreCase("favorites.desc"))
            return;


        movieInfoTask.execute(pivot);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {

        SharedPreferences sharedPrefs =
                PreferenceManager.getDefaultSharedPreferences(getActivity());
        String pivot = sharedPrefs.getString(
                getString(R.string.pref_sort_key),
                getString(R.string.pref_sort_most_popular));

        Uri moviesUri;

        if (pivot.equalsIgnoreCase("favorites.desc"))
        {

            moviesUri = MovieContract.FavoriteEntry.CONTENT_URI;

        }
        else
        {
            moviesUri = MovieContract.MovieEntry.buildMovieWithSetting(pivot);

        }

        Log.i(TAG, "moviesUri = " + moviesUri.toString());
        return new CursorLoader(getActivity(),
                moviesUri,
                MOVIE_COLUMNS,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {

        if (cursor != null)
        {

            SharedPreferences sharedPrefs =
                    PreferenceManager.getDefaultSharedPreferences(getActivity());
            String pivot = sharedPrefs.getString(
                    getString(R.string.pref_sort_key),
                    getString(R.string.pref_sort_most_popular));

            if (pivot.equalsIgnoreCase("favorites.desc"))
            {
                if (!cursor.moveToFirst())
                {
                    Toast.makeText(getActivity(), getString(R.string.no_favorites_selected),
                                    Toast.LENGTH_SHORT).show();

                }

            }


            if(((MainActivity) this.getActivity()).isLayoutTwoPane()){
                if (gridView.getSelectedItemPosition() == GridView.INVALID_POSITION) {
                    gridView.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            int itemPosition = 0;
                            if (mPosition != GridView.INVALID_POSITION) {
                                itemPosition = mPosition;
                            }
                            int tot = gridView.getCount();
                            if (tot > 0 && tot >= itemPosition) {
                                gridView.setItemChecked(itemPosition, true);
                                gridView.performItemClick(gridView.getChildAt(itemPosition), itemPosition, itemPosition);
                                gridView.smoothScrollToPosition(itemPosition);
                                //so that if mPosition is invalid it is set to 0
                                mPosition = itemPosition;
                            }
                        }
                    }, 1000);
                }
            }

            if (mPosition != GridView.INVALID_POSITION) {
                gridView.smoothScrollToPosition(mPosition);
            }


        }



        mCursorAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        mCursorAdapter.swapCursor(null);
    }


}

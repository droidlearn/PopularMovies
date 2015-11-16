package com.myapp.android.popularmovies;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.myapp.android.popularmovies.data.MovieContract;
import com.squareup.picasso.Picasso;



/**
 * A placeholder fragment containing a simple view.
 */
public class DetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String LOG_TAG = DetailFragment.class.getSimpleName();

    private static final String TAG  = DetailFragment.class.getSimpleName();

    private ShareActionProvider mShareActionProvider;
    private static final int DETAIL_LOADER = 0;
    private MovieInfo mi = null;


    private static final String[] MOVIE_COLUMNS = {

            MovieContract.MovieEntry.TABLE_NAME + "." + MovieContract.MovieEntry._ID,
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



    private ImageView poster;
    private TextView title;
    private TextView  plot;
    private TextView  release_date;
    private TextView  user_rating;
    private ImageView backdrop;



    public DetailFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);


        // The detail Activity called via intent.  Inspect the intent for forecast data.
        Intent intent = getActivity().getIntent();

        if (intent != null)
        {

            Log.v(LOG_TAG, "Extracting Uri from Intent");
            Uri mi = intent.getParcelableExtra("com.myapp.android.popularmovies.SelectedMovie");


            //Context mContext = getActivity().getApplicationContext();
            //Toast.makeText(mContext, "" + mi.original_title,
            //        Toast.LENGTH_SHORT).show();



            poster = (ImageView) rootView.findViewById(R.id.poster);
            title =   (TextView) rootView.findViewById(R.id.movie_title);
            plot  =   (TextView) rootView.findViewById(R.id.plot);
            release_date = (TextView) rootView.findViewById(R.id.release_date);
            user_rating = (TextView) rootView.findViewById(R.id.user_rating);
            backdrop = (ImageView) rootView.findViewById(R.id.backdrop);

        }

        return rootView;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(DETAIL_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }



    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.detailfragment, menu);


        // Retrieve the share menu item
        MenuItem menuItem = menu.findItem(R.id.action_share);

        // Get the provider and hold onto it to set/change the share intent.
        mShareActionProvider =
                (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);

        // Attach an intent to this ShareActionProvider.  You can update this at any time,
        // like when the user selects a new piece of data they might like to share.
        if (mi != null ) {
            mShareActionProvider.setShareIntent(createShareForecastIntent());
        } else {
            Log.d(LOG_TAG, "onLoad not finished so mi is null?");
        }

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.v(LOG_TAG, "In onCreateLoader");

        Intent intent = getActivity().getIntent();
        if (intent == null) {
            return null;
        }

        Uri muri = intent.getParcelableExtra("com.myapp.android.popularmovies.SelectedMovie");

        // Now create and return a CursorLoader that will take care of
        // creating a Cursor for the data being displayed.
        return new CursorLoader(
                getActivity(),
                //intent.getData(),
                muri,
                MOVIE_COLUMNS,
                null,
                null,
                null
        );
    }


    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.v(LOG_TAG, "In onLoadFinished");

        if (!data.moveToFirst()) { return; }

        Log.v(LOG_TAG, "Convert cursor to MovieInfo");

        mi = convertContentValuesToMovieInfos(data);

        if (mi != null)
        {
            String mid = mi.id;
            title.setText(mi.original_title);


            String url = "http://image.tmdb.org/t/p/w185/" + mi.poster_image;
            Picasso.with(getActivity().getApplicationContext())
                    .load(url)
                    .error(R.drawable.error)
                            //.noFade().resize(150, 150)
                            //.centerCrop()
                    .into(poster);


            String backdropUrl = "http://image.tmdb.org/t/p/w185/" + mi.backdrop_path;
            Picasso.with(getActivity().getApplicationContext())
                    .load(backdropUrl)
                    .error(R.drawable.error)
                            //.noFade().resize(150, 150)
                            //.centerCrop()
                    .into(backdrop);


            plot.setText(mi.plot_synopsis);
            user_rating.setText(mi.user_rating);
            release_date.setText(mi.release_date);
        }

        //If onCreateOptionsMenu has already happened, we need to update the share intent now.

        if (mShareActionProvider != null) {
            mShareActionProvider.setShareIntent(createShareForecastIntent());
        }


    }





    @Override
    public void onLoaderReset(Loader<Cursor> loader) { }


    private Intent createShareForecastIntent() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT,
                "" + TAG);
        return shareIntent;
    }

    private MovieInfo convertContentValuesToMovieInfos(Cursor cur) {

        return (new MovieInfo(cur.getString(DetailFragment.COL_MOVIE_KEY),
                cur.getString(DetailFragment.COL_MOVIE_ORIGINAL_TITLE),
                cur.getString(DetailFragment.COL_MOVIE_POSTER_IMAGE),
                cur.getString(DetailFragment.COL_MOVIE_PLOT_SYNOPSIS),
                cur.getString(DetailFragment.COL_MOVIE_USER_RATING),
                cur.getString(DetailFragment.COL_MOVIE_RELEASE_DATE),
                cur.getString(DetailFragment.COL_MOVIE_BACKDROP_PATH)));

    }


}

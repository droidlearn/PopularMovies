package com.myapp.android.popularmovies;

import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.myapp.android.popularmovies.adapter.ReviewListAdapter;
import com.myapp.android.popularmovies.adapter.TrailerListAdapter;
import com.myapp.android.popularmovies.data.MovieContract;
import com.myapp.android.popularmovies.data.MovieProvider;
import com.myapp.android.popularmovies.models.ReviewItem;
import com.myapp.android.popularmovies.models.TrailerItem;
import com.myapp.android.popularmovies.rests.RestClient;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Vector;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;



/**
 * A placeholder fragment containing a simple view.
 */
public class DetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>, TrailerListAdapter.TrailerListAdapterCallback  {

    private static final String LOG_TAG = DetailFragment.class.getSimpleName();

    private ShareActionProvider mShareActionProvider;
    private static final int DETAIL_LOADER = 0;
    private static final int REVIEW_LOADER = 1;
    private static final int TRAILER_LOADER = 2;

    private MovieInfo mi = null;

    private Uri mUri;
    static final String DETAIL_URI = "URI";

    private String mMovie;

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


    private static final String[] REVIEW_COLUMNS = {
            MovieContract.ReviewEntry.TABLE_NAME + "." + MovieContract.ReviewEntry._ID,
            MovieContract.ReviewEntry.COLUMN_AUTHOR,
            MovieContract.ReviewEntry.COLUMN_REVIEW_STR,
    };
    public static final int COL_REVIEW_AUTHOR = 1;
    public static final int COL_REVIEW_STR = 2;


    private static final String[] VIDEO_COLUMNS = {
            MovieContract.TrailerEntry.TABLE_NAME + "." + MovieContract.TrailerEntry._ID,
            MovieContract.TrailerEntry.COLUMN_NAME,
            MovieContract.TrailerEntry.COLUMN_KEY,
    };
    public static final int COL_TRAILER_NAME = 1;
    public static final int COL_TRAILER_KEY = 2;


    private ImageView poster;
    private TextView title;
    private TextView  plot;
    private TextView  release_date;
    private TextView  user_rating;
    private ImageView backdrop;
    private TextView trailer_header;
    private TextView review_header;

    private ImageButton favoriteBtn;

    private Long movieId;


    private ReviewListAdapter mReviewListAdapter;
    private TrailerListAdapter mTrailerListAdapter;

    private String mMovieShareText;
    private static final String mYouTubeUrlPrefix = "https://www.youtube.com/watch?v=";

    private int mFavoriteSelected = 0;


    public DetailFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        Bundle arguments = getArguments();
        if (arguments != null)
        {
            mUri  = arguments.getParcelable(DetailFragment.DETAIL_URI);

            if (mUri != null)
            {
                Log.i(LOG_TAG, "Retrieving movie id = " + mUri.getLastPathSegment());
                movieId = Long.parseLong(mUri.getLastPathSegment());

                //If already displaying favorites, no query required

                if (checkData(MovieContract.FavoriteEntry.CONTENT_URI, MovieContract.FavoriteEntry.COLUMN_MOVIE_KEY, Long.toString(movieId)))
                    mFavoriteSelected = 1;
            }

        }


        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);


        mReviewListAdapter = new ReviewListAdapter(getActivity(), null, 0);

        WrappedListView wrappedListView = (WrappedListView)   rootView.findViewById(R.id.review_listView);
        wrappedListView.setAdapter(mReviewListAdapter);
        wrappedListView.setExpanded(true);

        ((WrappedListView) rootView.findViewById(R.id.review_listView)).setAdapter(mReviewListAdapter);


        mTrailerListAdapter = new TrailerListAdapter(getActivity(), null, 0, this);

        //Custom grid view without scroll bar wrapped to the height of videos
        WrappedGridView wrappedGridView = (WrappedGridView) rootView.findViewById(R.id.trailer_gridView);
        wrappedGridView.setAdapter(mTrailerListAdapter);
        wrappedGridView.setExpanded(true);

        favoriteBtn = (ImageButton) rootView.findViewById(R.id.favoriteBtn);


        favoriteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mFavoriteSelected == 0) {
                    mFavoriteSelected = 1;
                    ContentValues values = new ContentValues();
                    values.put("id", movieId);
                    getActivity().getContentResolver().insert(MovieContract.FavoriteEntry.CONTENT_URI, values);



                    Toast.makeText(getActivity(), getString(R.string.added_to_favorites), Toast.LENGTH_SHORT).show();
                } else {
                    mFavoriteSelected = 0;
                    getActivity().getContentResolver().delete(MovieContract.FavoriteEntry.buildFavoriteUri(movieId), null, null);
                    Toast.makeText(getActivity(), getString(R.string.removed_from_favorites), Toast.LENGTH_SHORT).show();
                }
                toggleFavoriteButton();
            }
        });


        poster = (ImageView) rootView.findViewById(R.id.poster);
        title =   (TextView) rootView.findViewById(R.id.movie_title);
        plot  =   (TextView) rootView.findViewById(R.id.plot);
        release_date = (TextView) rootView.findViewById(R.id.release_date);
        user_rating = (TextView) rootView.findViewById(R.id.user_rating);
        backdrop = (ImageView) rootView.findViewById(R.id.backdrop);

        trailer_header = (TextView) rootView.findViewById(R.id.trailer_hdr);
        review_header = (TextView) rootView.findViewById(R.id.review_hdr);


         return rootView;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {

        getLoaderManager().initLoader(DETAIL_LOADER, null, this);
        getLoaderManager().initLoader(REVIEW_LOADER, null, this);
        getLoaderManager().initLoader(TRAILER_LOADER, null, this);

        if(savedInstanceState == null) {
            updateReviews();
            updateTrailers();
        }

        super.onActivityCreated(savedInstanceState);
    }


    void onPivotChanged(String newPivot) {

        Uri uri = mUri;
        if (null != uri) {

            Uri updateUri  = MovieContract.MovieEntry.buildMovieWithSetting(newPivot);
            mUri = updateUri;
            getLoaderManager().restartLoader(DETAIL_LOADER, null, this);

        }

    }


    public static boolean isAppInstalled(String uri, Context context) {
        PackageManager pm = context.getPackageManager();
        boolean installed = false;
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            installed = true;
        } catch (PackageManager.NameNotFoundException e) {
            installed = false;
        }
        return installed;
    }


    public void onClickCallback(String key) {
        if (isAppInstalled("com.google.android.youtube", getActivity())) {

            try {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + key));
                startActivity(intent);
            } catch (ActivityNotFoundException ex) {
                Intent intent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse("http://www.youtube.com/watch?v=" + key));
                startActivity(intent);
            }
        } else {
            try{
                Intent intent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse("http://www.youtube.com/watch?v=" + key));
                startActivity(intent);

            } catch (ActivityNotFoundException ex) {
                Toast.makeText(getActivity(), getString(R.string.playback_error), Toast.LENGTH_SHORT).show();
            }

        }
    }



    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        menu.clear();
        inflater.inflate(R.menu.detailfragment, menu);


        // Retrieve the share menu item
        MenuItem menuItem = menu.findItem(R.id.action_share);

        // Get the provider and hold onto it to set/change the share intent.
        mShareActionProvider =
                (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);

        // Attach an intent to this ShareActionProvider.  You can update this at any time,
        // like when the user selects a new piece of data they might like to share.
        if (mi != null ) {
            mShareActionProvider.setShareIntent(createShareMovieIntent());
        } else {
            Log.d(LOG_TAG, "onLoad not finished so mi is null?");
        }

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.v(LOG_TAG, "In onCreateLoader");

        if (null != mUri) {

            if (id == DETAIL_LOADER) {
                // Now create and return a CursorLoader that will take care of
                // creating a Cursor for the data being displayed.
                return new CursorLoader(
                        getActivity(),
                        //intent.getData(),
                        mUri,
                        MOVIE_COLUMNS,
                        null,
                        null,
                        null
                );
            }
            else if (id == REVIEW_LOADER)
            {
                return new CursorLoader(
                        getActivity(),
                        MovieContract.ReviewEntry.CONTENT_URI,
                        REVIEW_COLUMNS,
                        MovieContract.ReviewEntry.COLUMN_MOVIE_ID + " = ? ",
                        new String[]{Long.toString(movieId)},
                        MovieContract.ReviewEntry.COLUMN_INDEX + " DESC");

            }
            else if (id == TRAILER_LOADER)
            {

                return new CursorLoader(getActivity(),
                        MovieContract.TrailerEntry.CONTENT_URI,
                        VIDEO_COLUMNS,
                        MovieContract.TrailerEntry.COLUMN_MOVIE_ID + " = ? ",
                        new String[]{Long.toString(movieId)},
                        null);

            }

        }

        return null;
    }


    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.v(LOG_TAG, "In onLoadFinished");

        if (data == null || !data.moveToFirst()) { return; }


       if(DETAIL_LOADER == loader.getId()) {

           toggleFavoriteButton();

           Log.v(LOG_TAG, "Convert cursor to MovieInfo");

           mi = convertContentValuesToMovieInfos(data);

           if (mi != null) {
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

               trailer_header.setText(getString(R.string.trailer));
               review_header.setText(getString(R.string.reviews));

               //mMovieShareText = String.format("%s - %s - %s - %s", mi.original_title, mi.user_rating, mi.plot_synopsis, mi.release_date);


           }

       }
       else if(REVIEW_LOADER == loader.getId()){
           mReviewListAdapter.swapCursor(data);
            if(review_header.getVisibility() == View.INVISIBLE) review_header.setVisibility(View.VISIBLE);
       } else if(TRAILER_LOADER == loader.getId()){
           mTrailerListAdapter.swapCursor(data);
           if(trailer_header.getVisibility() == View.INVISIBLE) trailer_header.setVisibility(View.VISIBLE);
       }





        //If onCreateOptionsMenu has already happened, we need to update the share intent now.

        if (mShareActionProvider != null) {
            mShareActionProvider.setShareIntent(createShareMovieIntent());
        }


    }





    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

        if(REVIEW_LOADER == loader.getId()){
            mReviewListAdapter.swapCursor(null);
        } else if(TRAILER_LOADER == loader.getId()){
            mTrailerListAdapter.swapCursor(null);
        }


    }


    private Intent createShareMovieIntent() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT,
                mMovieShareText + LOG_TAG);
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

    private void updateReviews()
    {
        if (null == mUri) {
            return;
        }
        //If the reviews are already in db then no ned to fetch them from TMDB server
        if(checkData(MovieContract.ReviewEntry.CONTENT_URI, MovieContract.ReviewEntry.COLUMN_MOVIE_ID, Long.toString(movieId))) {
            Log.i(LOG_TAG,"Update review in DB " + movieId.toString());
            return;

        }

        //Fetch reviews and store in DB.
        RestClient.getMovieDBApiClient().getReviews(Long.toString(movieId), this.getResources().getString(R.string.API_DEV_KEY), new Callback<List<ReviewItem>>() {
            @Override
            public void success(List<ReviewItem> reviewItems, Response response) {
                int size = reviewItems.size();
                if (size > 0) {
                    int counter = 0;
                    Vector<ContentValues> cVVector = new Vector<ContentValues>(size);
                    for (ReviewItem m : reviewItems) {
                        ContentValues movieValues = new ContentValues();

                        movieValues.put(MovieContract.ReviewEntry.COLUMN_MOVIE_ID, movieId);
                        movieValues.put(MovieContract.ReviewEntry.COLUMN_AUTHOR, m.getAuthor());
                        movieValues.put(MovieContract.ReviewEntry.COLUMN_REVIEW_STR, m.getContent());
                        movieValues.put(MovieContract.ReviewEntry.COLUMN_INDEX, counter);
                        cVVector.add(movieValues);
                        counter++;
                    }
                    int inserted = 0;
                    // add to database
                    if (cVVector.size() > 0) {
                        ContentValues[] cvArray = new ContentValues[cVVector.size()];
                        cVVector.toArray(cvArray);
                        FragmentActivity fa = getActivity();
                        if (fa != null) {
                            inserted = fa.getContentResolver().bulkInsert(MovieContract.ReviewEntry.CONTENT_URI, cvArray);
                        }
                    }

                    Log.i(LOG_TAG, "updateReviewList Complete. " + inserted + " Inserted");

                }

            }


            @Override
            public void failure(RetrofitError error) {
                Log.e(LOG_TAG, "Error : " + error.getMessage());
            }
        });


    }


    private void updateTrailers()
    {


        if (null == mUri) {
            return;
        }

        if(checkData(MovieContract.TrailerEntry.CONTENT_URI, MovieContract.TrailerEntry.COLUMN_MOVIE_ID, Long.toString(movieId))) {

            getTrailerUrl();
            if (mShareActionProvider != null) {
                mShareActionProvider.setShareIntent(createShareMovieIntent());
            }
            return;
        }

        //Fetch trailers from server and store in DB
        RestClient.getMovieDBApiClient().getVideos(Long.toString(movieId), this.getResources().getString(R.string.API_DEV_KEY), new Callback<List<TrailerItem>>() {
            @Override
            public void success(List<TrailerItem> TrailerItems, Response response) {
                int size = TrailerItems.size();
                if (size > 0) {
                    int counter = 0;
                    Vector<ContentValues> cVVector = new Vector<ContentValues>(size);
                    for (TrailerItem m : TrailerItems) {
                        ContentValues movieValues = new ContentValues();
                        if (m.getSite().equals("YouTube")) {
                            if (0 == counter) {
                                //Set the share intent url
                                mMovieShareText = m.getName() + " - " + mYouTubeUrlPrefix + m.getKey();
                                if (mShareActionProvider != null) {
                                    mShareActionProvider.setShareIntent(createShareMovieIntent());
                                }
                            }

                            movieValues.put(MovieContract.TrailerEntry.COLUMN_MOVIE_ID, movieId);
                            movieValues.put(MovieContract.TrailerEntry.COLUMN_NAME, m.getName());
                            movieValues.put(MovieContract.TrailerEntry.COLUMN_KEY, m.getKey());
                            cVVector.add(movieValues);
                            counter++;
                        }
                    }
                    int inserted = 0;
                    // add to database
                    if (counter > 0) {
                        ContentValues[] cvArray = new ContentValues[cVVector.size()];
                        cVVector.toArray(cvArray);
                        FragmentActivity fa = getActivity();
                        if (fa != null) {
                            inserted = getActivity().getContentResolver().bulkInsert(MovieContract.TrailerEntry.CONTENT_URI, cvArray);
                        }
                    }

                    Log.i(LOG_TAG, "updateTrailerList Complete. " + inserted + " Inserted");

                }

            }

            @Override
            public void failure(RetrofitError error) {
                Log.e(LOG_TAG, "Error : " + error.getMessage());

            }
        });


    }

    private boolean checkData(Uri uri, String col_name, String col_value){
        boolean exists = false;
        Cursor cursor = getActivity().getContentResolver().query(uri,new String[] {"COUNT(*)"},  col_name + " = ?", new String[]{col_value},null);
        try {
            if (cursor.moveToFirst()) {
                if (cursor.getInt(0) > 0) {
                    cursor.close();
                    exists=true;
                }
            }
        } finally{
            cursor.close();
        }
        return exists;
    }

    private void getTrailerUrl(){
        Uri uri = MovieContract.TrailerEntry.CONTENT_URI.buildUpon()
                .appendQueryParameter(MovieProvider.QUERY_PARAMETER_LIMIT,
                        "1").build();
        Cursor cursor = getActivity().getContentResolver().query(uri, new String[]{MovieContract.TrailerEntry.COLUMN_NAME, MovieContract.TrailerEntry.COLUMN_KEY}, MovieContract.TrailerEntry.COLUMN_MOVIE_ID  + " = ?", new String[]{Long.toString(movieId)}, null);
        try {
            if (cursor.moveToFirst()) {
                mMovieShareText = cursor.getString(0) + " - "+  mYouTubeUrlPrefix + cursor.getString(1);
            }
        } finally{
            cursor.close();
        }

    }

    private void toggleFavoriteButton(){


        if(mFavoriteSelected ==1){
            favoriteBtn.setImageResource(R.drawable.addfavorites);
            favoriteBtn.setContentDescription(getString(R.string.remove_from_favorites));
        } else{
            favoriteBtn.setImageResource(R.drawable.removefavorites);
            favoriteBtn.setContentDescription(getString(R.string.add_to_favorites));
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();

    }




}

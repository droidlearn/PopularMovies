package com.myapp.android.popularmovies;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * {@link MovieCursorAdapter} exposes a list of movies
 * from a {@link android.database.Cursor} to a {@link android.widget.ListView}.
 */
public class MovieCursorAdapter extends CursorAdapter {


    private final String TAG = MovieCursorAdapter.class.getSimpleName();
    private Cursor mCursor;
    private int mPosition;


    public MovieCursorAdapter(Context context, Cursor c, int flags) {

        super(context, c, flags);
        mCursor = c;

    }

    private ArrayList<MovieInfo> convertContentValuesToMovieInfos(Cursor cur) {


        ArrayList<MovieInfo> mis = new ArrayList<MovieInfo>(cur.getCount());

        int i = 0;

        if (cur.moveToFirst())
        {
            do {
                mis.add(i, new MovieInfo(cur.getString(MovieFragment.COL_MOVIE_KEY),
                        cur.getString(MovieFragment.COL_MOVIE_ORIGINAL_TITLE),
                        cur.getString(MovieFragment.COL_MOVIE_POSTER_IMAGE),
                        cur.getString(MovieFragment.COL_MOVIE_PLOT_SYNOPSIS),
                        cur.getString(MovieFragment.COL_MOVIE_USER_RATING),
                        cur.getString(MovieFragment.COL_MOVIE_RELEASE_DATE),
                        cur.getString(MovieFragment.COL_MOVIE_BACKDROP_PATH)));

                i++;

            } while (cur.moveToNext());

        }

        return mis;

    }


    public MovieInfo getCurItem(Cursor cur)
    {
        if (null != cur) {

            if (cur.moveToPosition(mPosition)) {

                return(new MovieInfo(cur.getString(MovieFragment.COL_MOVIE_KEY),
                        cur.getString(MovieFragment.COL_MOVIE_ORIGINAL_TITLE),
                        cur.getString(MovieFragment.COL_MOVIE_POSTER_IMAGE),
                        cur.getString(MovieFragment.COL_MOVIE_PLOT_SYNOPSIS),
                        cur.getString(MovieFragment.COL_MOVIE_USER_RATING),
                        cur.getString(MovieFragment.COL_MOVIE_RELEASE_DATE),
                        cur.getString(MovieFragment.COL_MOVIE_BACKDROP_PATH)));
            }


        }
        return null;




    }


    /*
    public MovieInfo getItem(int position)
    {
        if (null != mCursor) {

            if (mCursor.moveToPosition(position)) {

                return(new MovieInfo(mCursor.getString(MovieFragment.COL_MOVIE_KEY),
                        mCursor.getString(MovieFragment.COL_MOVIE_ORIGINAL_TITLE),
                        mCursor.getString(MovieFragment.COL_MOVIE_POSTER_IMAGE),
                        mCursor.getString(MovieFragment.COL_MOVIE_PLOT_SYNOPSIS),
                        mCursor.getString(MovieFragment.COL_MOVIE_USER_RATING),
                        mCursor.getString(MovieFragment.COL_MOVIE_RELEASE_DATE),
                        mCursor.getString(MovieFragment.COL_MOVIE_BACKDROP_PATH)));
            }


        }
        return null;

    }

    */

    /*

    public long getItemId(int position) {

        Log.i(TAG, "#####In getITemID = " + position);
        mPosition = position;
        return position;
    }

   */


    /*
    public int getCount() {
        if (null != mCursor) {
            Log.i(TAG, "#####Cur count" + mCursor.getCount());
            return mCursor.getCount();
        }
        else
            return 0;
    }
    */

    /*
        Remember that these views are reused as needed.
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.custom_grid_layout, parent, false);

        return view;
    }

    /*
        This is where we fill-in the views with the contents of the cursor.
     */
    @Override
    public void bindView(View convertView, Context context, Cursor cur) {
        // our view is pretty simple here --- just a text view
        // we'll keep the UI functional with a simple (and slow!) binding.


        //xxx ArrayList<MovieInfo> mMovieInfo =  convertContentValuesToMovieInfos(cur);

        //MovieInfo mi = getCurItem(cur);

        MovieInfo mi = new MovieInfo(cur.getString(MovieFragment.COL_MOVIE_KEY),
                cur.getString(MovieFragment.COL_MOVIE_ORIGINAL_TITLE),
                cur.getString(MovieFragment.COL_MOVIE_POSTER_IMAGE),
                cur.getString(MovieFragment.COL_MOVIE_PLOT_SYNOPSIS),
                cur.getString(MovieFragment.COL_MOVIE_USER_RATING),
                cur.getString(MovieFragment.COL_MOVIE_RELEASE_DATE),
                cur.getString(MovieFragment.COL_MOVIE_BACKDROP_PATH));

        ImageView imageView;

        Holder holder = new Holder();


        holder.tv=(TextView)  convertView.findViewById(R.id.custom_grid_textview);
        holder.iv=(ImageView) convertView.findViewById(R.id.custom_grid_imageview);


        if (null == mi) {
            Picasso.with(mContext)
                    //.load(mThumbIds[position])
                    .load(R.drawable.error)
                    .error(R.drawable.error)
                            //.noFade().resize(150, 150)
                            //.centerCrop()
                    .into(holder.iv);
            holder.tv.setText("Error");
        }

        else
        {
           //Log.v(TAG, "position = " + position);
            String url = "http://image.tmdb.org/t/p/w185/" + mi.poster_image;
            //String url = "http://image.tmdb.org/t/p/w185/" + cur.getString(MovieFragment.COL_MOVIE_POSTER_IMAGE);
            Log.v(TAG, "URL " + url);
            Picasso.with(mContext)
                    .load(url)
                    .error(R.drawable.error)
                            //.noFade().resize(150, 150)
                            //.centerCrop()
                    .into(holder.iv);
            //holder.tv.setText(cur.getString(MovieFragment.COL_MOVIE_ORIGINAL_TITLE));
            holder.tv.setText(mi.original_title);
        }


    }


    public class Holder
    {
        TextView tv;
        ImageView iv;

    }



}
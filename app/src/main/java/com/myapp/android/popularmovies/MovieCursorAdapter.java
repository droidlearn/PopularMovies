package com.myapp.android.popularmovies;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.support.v4.widget.CursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Vector;

/**
 * {@link MovieCursorAdapter} exposes a list of movies
 * from a {@link android.database.Cursor} to a {@link android.widget.ListView}.
 */
public class MovieCursorAdapter extends CursorAdapter {
    public MovieCursorAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    private final String TAG = MovieCursorAdapter.class.getSimpleName();


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



        Vector<ContentValues> cVVector = new Vector<ContentValues>(cur.getCount());
        if (cur.moveToFirst())
        {
            do {
                ContentValues cv = new ContentValues();
                DatabaseUtils.cursorRowToContentValues(cur, cv);
                cVVector.add(cv);
            } while (cur.moveToNext());

        }


        ArrayList<MovieInfo> mMovieInfo =  convertContentValuesToMovieInfos(cur);


        ImageView imageView;

        Holder holder = new Holder();


        holder.tv=(TextView)  convertView.findViewById(R.id.custom_grid_textview);
        holder.iv=(ImageView) convertView.findViewById(R.id.custom_grid_imageview);

        int position = 0;

        if (null == mMovieInfo) {
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
           Log.v(TAG, "position = " + position);
            String url = "http://image.tmdb.org/t/p/w185/" + (mMovieInfo.get(position)).poster_image;
            Log.v(TAG, "URL " + url);
            Picasso.with(mContext)
                    .load(url)
                    .error(R.drawable.error)
                            //.noFade().resize(150, 150)
                            //.centerCrop()
                    .into(holder.iv);
            holder.tv.setText((mMovieInfo.get(position)).original_title);
        }


    }


    public class Holder
    {
        TextView tv;
        ImageView iv;

    }



}
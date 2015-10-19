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

import com.myapp.android.popularmovies.data.MovieContract;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
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


    private ArrayList<MovieInfo> convertContentValuesToMovieInfos(Vector<ContentValues> cvv) {


        ArrayList<MovieInfo> mis = new ArrayList<MovieInfo>(cvv.size());
        for (int i = 0; i < cvv.size(); i++) {
            ContentValues movieValues = cvv.elementAt(i);

            Set<Map.Entry<String, Object>> valueSet = movieValues.valueSet();

            Map<String, String> movieColumnValues = new HashMap<String, String>();

            for (Map.Entry<String, Object> entry : valueSet) {

                String columnName = entry.getKey();
                String columnValue = entry.getValue().toString();
                Log.i(TAG, "##Col Name " + columnName);
                Log.i(TAG, "##Col value " + columnValue);

                movieColumnValues.put(columnName, columnValue);
            }


            mis.add(i, new MovieInfo(movieColumnValues.get(MovieContract.MovieEntry.COLUMN_MOVIE_KEY),
                    movieColumnValues.get(MovieContract.MovieEntry.COLUMN_ORIGINAL_TITLE),
                    movieColumnValues.get(MovieContract.MovieEntry.COLUMN_POSTER_IMAGE),
                    movieColumnValues.get(MovieContract.MovieEntry.COLUMN_PLOT_SYNOPSIS),
                    movieColumnValues.get(MovieContract.MovieEntry.COLUMN_USER_RATING),
                    movieColumnValues.get(MovieContract.MovieEntry.COLUMN_RELEASE_DATE),
                    movieColumnValues.get(MovieContract.MovieEntry.COLUMN_BACKDROP_PATH)));


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


        ArrayList<MovieInfo> mMovieInfo =  convertContentValuesToMovieInfos(cVVector);


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
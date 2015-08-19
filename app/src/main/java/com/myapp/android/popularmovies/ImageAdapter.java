package com.myapp.android.popularmovies;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by yogi on 7/30/15.
 */
public class ImageAdapter extends BaseAdapter {
    private Context mContext;

    private ArrayList<MovieInfo> mMovieInfo;

    private final String TAG = ImageAdapter.class.getSimpleName();
    private static LayoutInflater inflater = null;

    public ImageAdapter(Context c, ArrayList<MovieInfo> movieInfos) {
        mContext = c;
        mMovieInfo = movieInfos;
        inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public int getCount() {
        if (null != mMovieInfo)
            return mMovieInfo.size();
        else
            return 0;
    }



    public MovieInfo getItem(int position)
    {
        if (null != mMovieInfo)
            return (mMovieInfo.get(position));

        return null;

    }


    public long getItemId(int position) {
        return position;
    }

    public class Holder
    {
        TextView tv;
        ImageView iv;

    }


    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {


        ImageView imageView;

        Holder holder = new Holder();

        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.custom_grid_layout, parent, false);
        }


        convertView = inflater.inflate(R.layout.custom_grid_layout, null);
        holder.tv=(TextView)  convertView.findViewById(R.id.custom_grid_textview);
        holder.iv=(ImageView) convertView.findViewById(R.id.custom_grid_imageview);



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
            Log.v(TAG, "URL "  + url);
            Picasso.with(mContext)
                    .load(url)
                    .error(R.drawable.error)
                    //.noFade().resize(150, 150)
                            //.centerCrop()
                    .into(holder.iv);
            holder.tv.setText((mMovieInfo.get(position)).original_title);

        }

        return convertView;
    }

    public void clear()
    {
        if (mMovieInfo != null) {
            mMovieInfo.clear();
        }
    }



}




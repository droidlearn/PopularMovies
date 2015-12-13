package com.myapp.android.popularmovies.adapter;


import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.myapp.android.popularmovies.DetailFragment;
import com.myapp.android.popularmovies.R;
import com.squareup.picasso.Picasso;

public class TrailerListAdapter extends CursorAdapter {
    private final TrailerListAdapterCallback mTrailerListAdapterCallback;

    public TrailerListAdapter(Context context, Cursor c, int flags, TrailerListAdapterCallback TrailerListAdapterCallback) {
        super(context, c, flags);
        this.mTrailerListAdapterCallback = TrailerListAdapterCallback;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.trailer_list_item, parent, false);

        ViewHolder viewHolder = new ViewHolder();

        viewHolder.name = (TextView) view.findViewById(R.id.name);
        viewHolder.video = (ImageView) view.findViewById(R.id.video);
        viewHolder.ibtn = (ImageButton) view.findViewById(R.id.btnPlay);

        view.setTag(viewHolder);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        ViewHolder viewHolder = (ViewHolder) view.getTag();
        ImageView imageView = viewHolder.video;
        String url = "http://img.youtube.com/vi/" + cursor.getString(DetailFragment.COL_TRAILER_KEY) + "/1.jpg";
        Picasso.with(context).load(url).into(imageView);
        viewHolder.name.setText(cursor.getString(DetailFragment.COL_TRAILER_NAME));
        viewHolder.ibtn.setTag(cursor.getString(DetailFragment.COL_TRAILER_KEY));
        viewHolder.ibtn.setContentDescription(context.getResources().getString(R.string.trailer));
        viewHolder.ibtn.setOnClickListener(new ImageButton.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mTrailerListAdapterCallback != null) {
                    mTrailerListAdapterCallback.onClickCallback((String) v.getTag());
                }
            }
        });
    }


    class ViewHolder {

        TextView name;
        ImageView video;
        ImageButton ibtn;


    }

    /**
     * To send back video item click to MovieDetailsFragment
     */
    public interface TrailerListAdapterCallback {
        void onClickCallback(String key);
    }
}



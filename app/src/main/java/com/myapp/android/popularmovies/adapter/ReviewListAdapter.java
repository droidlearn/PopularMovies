package com.myapp.android.popularmovies.adapter;


import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.myapp.android.popularmovies.DetailFragment;
import com.myapp.android.popularmovies.R;


public class ReviewListAdapter extends CursorAdapter {
    public ReviewListAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.review_list_item, parent, false);

        ViewHolder viewHolder = new ViewHolder();
        view.setTag(viewHolder);

        viewHolder.author = (TextView) view.findViewById(R.id.author);
        viewHolder.content = (TextView) view.findViewById(R.id.content);


        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        ViewHolder viewHolder = (ViewHolder) view.getTag();





        viewHolder.author.setText(cursor.getString(DetailFragment.COL_REVIEW_AUTHOR));
        viewHolder.content.setText(cursor.getString(DetailFragment.COL_REVIEW_STR));
    }


    class ViewHolder {

        TextView author;
        TextView content;

    }


}
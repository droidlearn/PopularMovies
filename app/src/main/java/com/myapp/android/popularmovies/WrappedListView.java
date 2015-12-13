package com.myapp.android.popularmovies;


import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.ListView;

public class WrappedListView extends ListView
{

    private boolean expanded = false;

    public WrappedListView(Context context)
    {
        super(context);
    }

    public WrappedListView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public WrappedListView(Context context, AttributeSet attrs,int defStyle)
    {
        super(context, attrs, defStyle);
    }

    private boolean isExpanded()
    {
        return expanded;
    }
    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {

        if (isExpanded())
        {

            int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
            super.onMeasure(widthMeasureSpec, expandSpec);

            ViewGroup.LayoutParams params = getLayoutParams();
            params.height = getMeasuredHeight();
        }
        else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        }
    }

    public void setExpanded(boolean expanded)
    {
        this.expanded = expanded;
    }
}

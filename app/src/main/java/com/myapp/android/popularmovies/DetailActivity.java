package com.myapp.android.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class DetailActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new DetailFragment())
                    .commit();
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class DetailFragment extends Fragment {

        private static final String LOG_TAG = DetailFragment.class.getSimpleName();

        private static final String TAG  = DetailFragment.class.getSimpleName();

        private ImageView poster;
        private TextView  title;
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
            View rootView = inflater.inflate(R.layout.activity_detail, container, false);

            // The detail Activity called via intent.  Inspect the intent for forecast data.
            Intent intent = getActivity().getIntent();

            if (intent != null)
            {

                MovieInfo mi = intent.getParcelableExtra("com.myapp.android.popularmovies.SelectedMovie");


                //Context mContext = getActivity().getApplicationContext();
                //Toast.makeText(mContext, "" + mi.original_title,
                //      Toast.LENGTH_SHORT).show();


                poster =  (ImageView) rootView.findViewById(R.id.poster);
                title =   (TextView) rootView.findViewById(R.id.movie_title);
                plot  =   (TextView) rootView.findViewById(R.id.plot);
                release_date = (TextView) rootView.findViewById(R.id.release_date);
                user_rating = (TextView) rootView.findViewById(R.id.user_rating);
                backdrop = (ImageView) rootView.findViewById(R.id.backdrop);


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


            }

            return rootView;
        }
        @Override
        public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
            // Inflate the menu; this adds items to the action bar if it is present.
            inflater.inflate(R.menu.detailfragment, menu);

            /*
            // Retrieve the share menu item
            MenuItem menuItem = menu.findItem(R.id.action_share);

            // Get the provider and hold onto it to set/change the share intent.
            ShareActionProvider mShareActionProvider =
                    (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);

            // Attach an intent to this ShareActionProvider.  You can update this at any time,
            // like when the user selects a new piece of data they might like to share.
            if (mShareActionProvider != null ) {
                mShareActionProvider.setShareIntent(createShareForecastIntent());
            } else {
                Log.d(LOG_TAG, "Share Action Provider is null?");
            }
            */
        }

        private Intent createShareForecastIntent() {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT,
                    "" + TAG);
            return shareIntent;
        }
    }
}

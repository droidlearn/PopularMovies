package com.myapp.android.popularmovies;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;


public class MainActivity extends ActionBarActivity {

    private final String LOG_TAG = MainActivity.class.getSimpleName();
    private final String MOVIEFRAGMENT_TAG = "MFTAG";
    private String mPivot;



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        SharedPreferences sharedPrefs =
                PreferenceManager.getDefaultSharedPreferences(this);
        mPivot = sharedPrefs.getString(
                getString(R.string.pref_sort_key),
                getString(R.string.pref_sort_most_popular));

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new MovieFragment(), MOVIEFRAGMENT_TAG)
                    .commit();
        }

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();



        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();

        SharedPreferences sharedPrefs =
                PreferenceManager.getDefaultSharedPreferences(this);
        String pivot = sharedPrefs.getString(
                getString(R.string.pref_sort_key),
                getString(R.string.pref_sort_most_popular));

        // update the pivot in our second pane using the fragment manager
        if (pivot != null && !pivot.equals(mPivot)) {
            MovieFragment mf = (MovieFragment)getSupportFragmentManager().findFragmentByTag(MOVIEFRAGMENT_TAG);
            if ( null != mf ) {
                mf.onPivotChanged();
            }
            mPivot = pivot;
        }
    }



}

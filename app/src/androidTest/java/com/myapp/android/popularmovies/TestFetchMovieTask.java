/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.myapp.android.popularmovies;

import android.annotation.TargetApi;
import android.test.AndroidTestCase;

public class TestFetchMovieTask extends AndroidTestCase {

    static final String ADD_PIVOT_SETTING = "Popular";
    static final String ADD_MOVIE_ID = "76341";
    static final String ADD_REVIEW_ID = "1";
    static final String ADD_MOVIE_REVIEW = "This is test data for review";

  
   
    @TargetApi(11)
    public void testAddReview() {

        /*
        // start from a clean state
        getContext().getContentResolver().delete(MovieContract.ReviewEntry.CONTENT_URI,
                MovieContract.MovieEntry.COLUMN_PIVOT + " = ?",
                new String[]{ADD_PIVOT_SETTING});

        FetchMovieTask fwt = new FetchMovieTask(getContext());
        long reviewId = fwt.addReview(ADD_MOVIE_ID, ADD_REVIEW_ID,
                ADD_MOVIE_REVIEW);

        // does addLocation return a valid record ID?
        assertFalse("Error: addLocation returned an invalid ID on insert",
                locationId == -1);

        // test all this twice
        for ( int i = 0; i < 2; i++ ) {

            // does the ID point to our location?
            Cursor locationCursor = getContext().getContentResolver().query(
                    MovieContract.ReviewEntry.CONTENT_URI,
                    new String[]{
                            MovieContract.ReviewEntry._ID,
                            MovieContract.ReviewEntry.COLUMN_LOCATION_SETTING,
                            MovieContract.ReviewEntry.COLUMN_CITY_NAME,
                            MovieContract.ReviewEntry.COLUMN_COORD_LAT,
                            MovieContract.ReviewEntry.COLUMN_COORD_LONG
                    },
                    MovieContract.ReviewEntry.COLUMN_LOCATION_SETTING + " = ?",
                    new String[]{ADD_LOCATION_SETTING},
                    null);

            // these match the indices of the projection
            if (locationCursor.moveToFirst()) {
                assertEquals("Error: the queried value of locationId does not match the returned value" +
                        "from addLocation", locationCursor.getLong(0), locationId);
                assertEquals("Error: the queried value of location setting is incorrect",
                        locationCursor.getString(1), ADD_LOCATION_SETTING);
                assertEquals("Error: the queried value of location city is incorrect",
                        locationCursor.getString(2), ADD_LOCATION_CITY);
                assertEquals("Error: the queried value of latitude is incorrect",
                        locationCursor.getDouble(3), ADD_LOCATION_LAT);
                assertEquals("Error: the queried value of longitude is incorrect",
                        locationCursor.getDouble(4), ADD_LOCATION_LON);
            } else {
                fail("Error: the id you used to query returned an empty cursor");
            }

            // there should be no more records
            assertFalse("Error: there should be only one record returned from a location query",
                    locationCursor.moveToNext());

            // add the location again
            long newLocationId = fwt.addLocation(ADD_LOCATION_SETTING, ADD_LOCATION_CITY,
                    ADD_LOCATION_LAT, ADD_LOCATION_LON);

            assertEquals("Error: inserting a location again should return the same ID",
                    locationId, newLocationId);
        }



        // reset our state back to normal
        getContext().getContentResolver().delete(MovieContract.ReviewEntry.CONTENT_URI,
                MovieContract.ReviewEntry.COLUMN_LOCATION_SETTING + " = ?",
                new String[]{ADD_LOCATION_SETTING});

        // clean up the test so that other tests can use the content provider
        getContext().getContentResolver().
                acquireContentProviderClient(MovieContract.ReviewEntry.CONTENT_URI).
                getLocalContentProvider().shutdown();
                 */
    }

}

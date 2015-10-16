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
package com.myapp.android.popularmovies.data;

import android.net.Uri;
import android.test.AndroidTestCase;

/*
    Students: This is NOT a complete test for the WeatherContract --- just for the functions
    that we expect you to write.
 */
public class TestMovieContract extends AndroidTestCase {

    // intentionally includes a slash to make sure Uri is getting quoted correctly
    private static final String TEST_MOVIE_TYPE = "popularity.desc";


    /*
        Students: Uncomment this out to test
     */
    public void testBuildMovieByType() {
        Uri movieUri = MovieContract.MovieEntry.buildMovieWithPopularSetting(TEST_MOVIE_TYPE);
        assertNotNull("Error: Null Uri returned.  You must fill-in buildMovie in " +
                        "MovieContract.",
                movieUri);
        assertEquals("Error: Movie review not properly appended to the end of the Uri",
                TEST_MOVIE_TYPE, movieUri.getLastPathSegment());
        assertEquals("Error: Movie review Uri doesn't match our expected result",
                movieUri.toString(),
                "content://com.myapp.android.popularmovies/movie/popularity.desc");
    }

}

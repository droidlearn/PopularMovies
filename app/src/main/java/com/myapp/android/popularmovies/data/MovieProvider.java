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

import android.annotation.TargetApi;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

public class MovieProvider extends ContentProvider {

    // The URI Matcher used by this content provider.
    public static final UriMatcher sUriMatcher = buildUriMatcher();
    private MovieDBHelper mOpenHelper;

    static final int MOVIE = 100;
    static final int MOVIE_BY_TYPE = 101;
    static final int MOVIE_BY_TYPE_AND_ID = 102;
    static final int REVIEW_LIST = 300;
    static final int TRAILER_LIST  = 400;
    public static final int FAVORITE = 500;
    public static final int FAVORITE_LIST = 501;
    public static final String QUERY_PARAMETER_LIMIT = "limit";
    public static final String QUERY_PARAMETER_OFFSET = "offset";


    private static final SQLiteQueryBuilder sMovieByTypeSettingQueryBuilder;

    static{
        sMovieByTypeSettingQueryBuilder = new SQLiteQueryBuilder();


        sMovieByTypeSettingQueryBuilder.setTables(
                MovieContract.MovieEntry.TABLE_NAME);
    }


    private static final SQLiteQueryBuilder sFavoriteMovieByTypeSettingQueryBuilder;

    static{
        sFavoriteMovieByTypeSettingQueryBuilder = new SQLiteQueryBuilder();


        sFavoriteMovieByTypeSettingQueryBuilder.setTables(
                MovieContract.FavoriteEntry.TABLE_NAME);
    }




    private static final SQLiteQueryBuilder sMovieReviewByIDQueryBuilder;

    static{
        sMovieReviewByIDQueryBuilder = new SQLiteQueryBuilder();
        
        //This is an inner join which looks like
        //MOVIE INNER JOIN Review ON MOVIE.Review_id = Review._id
        sMovieReviewByIDQueryBuilder.setTables(
                MovieContract.MovieEntry.TABLE_NAME + " INNER JOIN " +
                        MovieContract.ReviewEntry.TABLE_NAME +
                        " ON " + MovieContract.MovieEntry.TABLE_NAME +
                        "." + MovieContract.MovieEntry.COLUMN_MOVIE_KEY +
                        " = " + MovieContract.ReviewEntry.TABLE_NAME +
                        "." + MovieContract.ReviewEntry._ID);
    }

    //movie.column_pivot = ?
    private static final String sMovieByTypeSetting =
            MovieContract.MovieEntry.TABLE_NAME+
                    "." + MovieContract.MovieEntry.COLUMN_PIVOT + " = ? ";

    //movie.column_pivot = ? AND id = ?
    private static final String sMovieByTypeAndIDSelection =
            MovieContract.MovieEntry.TABLE_NAME+
                    "." + MovieContract.MovieEntry.COLUMN_PIVOT + " = ? AND " +
                    MovieContract.MovieEntry.COLUMN_MOVIE_KEY + " = ? ";



    private Cursor getMovieByTypeSetting(Uri uri, String[] projection, String sortOrder) {
        String typeSetting = MovieContract.MovieEntry.getTypeSettingFromUri(uri);

        String[] selectionArgs;
        String selection;

        selection = sMovieByTypeSetting;
        selectionArgs = new String[]{typeSetting};

        return sMovieByTypeSettingQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
    }

    private Cursor getMovieByTypeSettingAndID(Uri uri, String[] projection, String sortOrder) {
        String typeSetting = MovieContract.MovieEntry.getTypeSettingFromUri(uri);
        String movieID = MovieContract.MovieEntry.getMovieIDSettingFromUri(uri);

        String[] selectionArgs;
        String selection;

        selection = sMovieByTypeAndIDSelection;
        selectionArgs = new String[]{typeSetting, movieID};

        return sMovieByTypeSettingQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
    }

    //movie.column_pivot = ?
    private static final String sFavoriteMovieByTypeSetting =
            MovieContract.FavoriteEntry.TABLE_NAME+
                    "." + MovieContract.FavoriteEntry.COLUMN_PIVOT + " = ? ";



    private Cursor getFavoriteMovieByTypeSetting(Uri uri, String[] projection, String sortOrder) {
        String typeSetting = MovieContract.FavoriteEntry.getTypeSettingFromUri(uri);

        String[] selectionArgs;
        String selection;

        selection = sFavoriteMovieByTypeSetting;
        selectionArgs = new String[]{typeSetting};

        return sFavoriteMovieByTypeSettingQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
    }


    private Cursor getFavoriteMovieByTypeSettingAndID(Uri uri, String[] projection, String sortOrder) {
        String typeSetting = MovieContract.FavoriteEntry.getTypeSettingFromUri(uri);
        String movieID = MovieContract.FavoriteEntry.getMovieIDSettingFromUri(uri);

        String[] selectionArgs;
        String selection;

        selection = sMovieByTypeAndIDSelection;
        selectionArgs = new String[]{typeSetting, movieID};

        return sMovieByTypeSettingQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
    }




    /*
        Students: Here is where you need to create the UriMatcher. This UriMatcher will
        match each URI to the MOVIE, MOVIE_WITH_Review, MOVIE_WITH_Review_AND_DATE,
        and Review integer constants defined above.  You can test this by uncommenting the
        testUriMatcher test within TestUriMatcher.
     */
    static UriMatcher buildUriMatcher() {
        // I know what you're thinking.  Why create a UriMatcher when you can use regular
        // expressions instead?  Because you're not crazy, that's why.

        // All paths added to the UriMatcher have a corresponding code to return when a match is
        // found.  The code passed into the constructor represents the code to return for the root
        // URI.  It's common to use NO_MATCH as the code for this case.
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = MovieContract.CONTENT_AUTHORITY;


        // For each type of URI you want to add, create a corresponding code.
        matcher.addURI(authority, MovieContract.PATH_MOVIE + "/", MOVIE);
        matcher.addURI(authority, MovieContract.PATH_MOVIE + "/*", MOVIE_BY_TYPE);
        matcher.addURI(authority, MovieContract.PATH_MOVIE + "/*/*", MOVIE_BY_TYPE_AND_ID);
        matcher.addURI(authority, MovieContract.PATH_REVIEW + "/", REVIEW_LIST);
        matcher.addURI(authority, MovieContract.PATH_TRAILER + "/", TRAILER_LIST);
        matcher.addURI(authority, MovieContract.PATH_FAVORITE + "/*", FAVORITE);
        matcher.addURI(authority, MovieContract.PATH_FAVORITE + "/", FAVORITE_LIST);



        return matcher;
    }

    /*
        Students: We've coded this for you.  We just create a new MovieDBHelper for later use
        here.
     */
    @Override
    public boolean onCreate() {
        mOpenHelper = new MovieDBHelper(getContext());
        return true;
    }

    /*
        Students: Here's where you'll code the getType function that uses the UriMatcher.  You can
        test this by uncommenting testGetType in TestProvider.

     */
    @Override
    public String getType(Uri uri) {

        // Use the Uri Matcher to determine what kind of URI this is.
        final int match = sUriMatcher.match(uri);

        switch (match) {
            case MOVIE_BY_TYPE:
                return MovieContract.MovieEntry.CONTENT_TYPE;
            case MOVIE_BY_TYPE_AND_ID:
                return MovieContract.MovieEntry.CONTENT_ITEM_TYPE;
            case MOVIE:
                return MovieContract.MovieEntry.CONTENT_TYPE;
            case REVIEW_LIST:
                return MovieContract.ReviewEntry.CONTENT_TYPE;
            case TRAILER_LIST:
                return MovieContract.TrailerEntry.CONTENT_TYPE;
            case FAVORITE:
                return MovieContract.FavoriteEntry.CONTENT_ITEM_TYPE;
            case FAVORITE_LIST:
                return MovieContract.FavoriteEntry.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        // Here's the switch statement that, given a URI, will determine what kind of request it is,
        // and query the database accordingly.
        Cursor retCursor;
        switch (sUriMatcher.match(uri)) {
            // "MOVIE/*"
            case MOVIE_BY_TYPE:
            {
                retCursor = getMovieByTypeSetting(uri, projection, sortOrder);
                break;
            }

            // "MOVIE/*/*"
            case MOVIE_BY_TYPE_AND_ID: {
                retCursor = getMovieByTypeSettingAndID(uri, projection, sortOrder);
                break;
            }

            // "MOVIE"
            case MOVIE: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        MovieContract.MovieEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            // "Review"
            case REVIEW_LIST: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        MovieContract.ReviewEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            // "TRAILER"
            case TRAILER_LIST: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        MovieContract.TrailerEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }

            // "FAVORITE"
            case FAVORITE: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        MovieContract.FavoriteEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;

            }


            // "FAVORITE_LIST"
            case FAVORITE_LIST: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                            MovieContract.FavoriteEntry.TABLE_NAME,
                            projection,
                            selection,
                            selectionArgs,
                            null,
                            null,
                            sortOrder
                );
                break;
            }



            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    /*
        Student: Add the ability to insert Reviews to the implementation of this function.
     */
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match) {
            case MOVIE: {
                long _id = db.insert(MovieContract.MovieEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = MovieContract.MovieEntry.buildMovieUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case REVIEW_LIST: {
                long _id = db.insert(MovieContract.ReviewEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = MovieContract.ReviewEntry.buildReviewUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case TRAILER_LIST: {
                long _id = db.insert(MovieContract.TrailerEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = MovieContract.TrailerEntry.buildTrailerUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }


            case FAVORITE_LIST: {
                String id = values.getAsString("id");
                String sql = "INSERT INTO " + MovieContract.FavoriteEntry.TABLE_NAME + " SELECT * FROM " + MovieContract.MovieEntry.TABLE_NAME + " WHERE " + MovieContract.MovieEntry.COLUMN_MOVIE_KEY+ " = " + id;
                db.execSQL(sql);

                returnUri = MovieContract.FavoriteEntry.buildFavoriteUri(Long.parseLong(id));
                break;
            }



            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);


        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsDeleted;
        String id;
        String where;


        // this makes delete all rows return the number of rows deleted
        if ( null == selection ) selection = "1";
        switch (match) {
            case MOVIE:
                rowsDeleted = db.delete(
                        MovieContract.MovieEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case REVIEW_LIST:
                rowsDeleted = db.delete(
                        MovieContract.ReviewEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case TRAILER_LIST:
                rowsDeleted = db.delete(
                        MovieContract.TrailerEntry.TABLE_NAME, selection, selectionArgs);
                break;

            case FAVORITE:

                id = uri.getLastPathSegment();
                where = MovieContract.FavoriteEntry.COLUMN_MOVIE_KEY+ " = " + id;
                if (!TextUtils.isEmpty(selection)) {
                    where += " AND " + selection;
                }
                rowsDeleted = db.delete(
                        MovieContract.FavoriteEntry.TABLE_NAME,
                        where,
                        selectionArgs);
                break;


            case FAVORITE_LIST:
                rowsDeleted = db.delete(
                        MovieContract.FavoriteEntry.TABLE_NAME, selection, selectionArgs);
                break;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        // Because a null deletes all rows
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }


    @Override
    public int update(
            Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsUpdated;

        switch (match) {
            case MOVIE:
                rowsUpdated = db.update(MovieContract.MovieEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            case REVIEW_LIST:
                rowsUpdated = db.update(MovieContract.ReviewEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            case TRAILER_LIST:
                rowsUpdated = db.update(MovieContract.TrailerEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int returnCount = 0;
        switch (match) {
            case MOVIE:
                db.beginTransaction();
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(MovieContract.MovieEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            case REVIEW_LIST:
                db.beginTransaction();
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(MovieContract.ReviewEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;

            default:
                return super.bulkInsert(uri, values);
        }
    }

    // You do not need to call this method. This is a method specifically to assist the testing
    // framework in running smoothly. You can read more at:
    // http://developer.android.com/reference/android/content/ContentProvider.html#shutdown()
    @Override
    @TargetApi(11)
    public void shutdown() {
        mOpenHelper.close();
        super.shutdown();
    }
}
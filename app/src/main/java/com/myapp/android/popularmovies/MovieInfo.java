package com.myapp.android.popularmovies;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by yogi on 8/8/15.
 */
public class MovieInfo implements Parcelable  {

    String id;
    String original_title;
    String poster_image;
    String plot_synopsis;
    String user_rating;
    String release_date;
    String backdrop_path;

    public MovieInfo(String mId, String mTitle, String mImage, String mSynopsis, String mRating, String mRDate, String mBackdrop_path)
    {
        this.id = mId;
        this.original_title = mTitle;
        this.poster_image = mImage;
        this.plot_synopsis = mSynopsis;
        this.user_rating = mRating;
        this.release_date = mRDate;
        this.backdrop_path = mBackdrop_path;

    }

    private MovieInfo(Parcel in){
        id = in.readString();
        original_title = in.readString();
        poster_image = in.readString();
        plot_synopsis = in.readString();
        user_rating = in.readString();
        release_date = in.readString();
        backdrop_path = in.readString();

    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(original_title);
        parcel.writeString(poster_image);
        parcel.writeString(plot_synopsis);
        parcel.writeString(user_rating);
        parcel.writeString(release_date);
        parcel.writeString(backdrop_path);

    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Parcelable.Creator<MovieInfo> CREATOR = new Parcelable.Creator<MovieInfo>() {
        @Override
        public MovieInfo createFromParcel(Parcel parcel) {
            return new MovieInfo(parcel);
        }

        @Override
        public MovieInfo[] newArray(int i) {
            return new MovieInfo[i];
        }

    };




}

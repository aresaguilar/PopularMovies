package com.example.android.popularmovies.model;


import android.os.Parcel;
import android.os.Parcelable;

public class Movie implements Parcelable {

    private static final String POSTER_BASE_URL = "http://image.tmdb.org/t/p/";
    public static final String POSTER_SIZE_MOBILE = "w185";

    private int id;

    private String title;
    private String original_title;
    private String overview;
    private String release_date;

    private String poster_path;
    private String backdrop_path;

    private double popularity;
    private  int vote_average;
    private int vote_count;

    public class MovieReview {
        private String mAuthor;
        private String mReview;
        private String mId;

        public MovieReview(String author, String review, String id) {
            mAuthor = author;
            mReview = review;
            mId = id;
        }

        public String getAuthor() {
            return mAuthor;
        }

        public String getReview() {
            return mReview;
        }

        public String getId() {
            return mId;
        }
    }

    public class MovieVideo {
        private String mSite;
        private String mKey;
        private String mType;

        public MovieVideo(String mSite, String mKey, String mType) {
            this.mSite = mSite;
            this.mKey = mKey;
            this.mType = mType;
        }

        public String getSite() {
            return mSite;
        }

        public String getKey() {
            return mKey;
        }

        public String getType() {
            return mType;
        }
    }

    public int getVote_count() {
        return vote_count;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getOriginal_title() {
        return original_title;
    }

    public String getOverview() {
        return overview;
    }

    public String getRelease_date() {
        return release_date;
    }

    public String getPoster_path() {
        return poster_path;
    }

    public String getBackdrop_path() {
        return backdrop_path;
    }

    public double getPopularity() {
        return popularity;
    }

    public int getVote_average() {
        return vote_average;
    }

    public Movie () {}

    public Movie (Parcel in) {
        this.id = in.readInt();
        this.title = in.readString();
        this.original_title = in.readString();
        this.overview = in.readString();
        this.release_date = in.readString();
        this.poster_path = in.readString();
        this.backdrop_path = in.readString();
        this.popularity = in.readDouble();
        this.vote_average = in.readInt();
        this.vote_count = in.readInt();
    }

    public Movie(int id, String title, String original_title,
                 String overview, String release_date, String poster_path,
                 String backdrop_path, double popularity, int vote_average, int vote_count) {
        this.id = id;
        this.title = title;
        this.original_title = original_title;
        this.overview = overview;
        this.release_date = release_date;
        this.poster_path = poster_path;
        this.backdrop_path = backdrop_path;
        this.popularity = popularity;
        this.vote_average = vote_average;
        this.vote_count = vote_count;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(title);
        dest.writeString(original_title);
        dest.writeString(overview);
        dest.writeString(release_date);
        dest.writeString(poster_path);
        dest.writeString(backdrop_path);
        dest.writeDouble(popularity);
        dest.writeInt(vote_average);
        dest.writeInt(vote_count);
    }

    public static final Parcelable.Creator<Movie> CREATOR
            = new Parcelable.Creator<Movie>() {
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };
}

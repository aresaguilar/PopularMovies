package com.example.android.popularmovies;


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

    public String getPosterURL(String size) {
        return POSTER_BASE_URL + size + this.poster_path;
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
}

package com.example.android.popularmovies;


public class Movie {

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
}

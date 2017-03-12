package com.example.android.popularmovies.data;

import android.net.Uri;
import android.provider.BaseColumns;

public final class MoviesContract {

    public static final String AUTHORITY = "com.example.android.popularmovies";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    public static final String PATH_MOVIES = "movies";
    public static final String PATH_POPULAR = "popular";
    public static final String PATH_TOP_RATED = "top_rated";
    public static final String PATH_FAVORITES = "favorites";
    public static final String PATH_REVIEWS = "reviews";
    public static final String PATH_VIDEOS = "videos";

    // We make the constructor private to prevent from accidental instantiations.
    private MoviesContract() {}

    private interface MovieColumns {
        String COLUMN_NAME_MOVIE_ID = "movie_id";
    }

    public static final class MoviesEntry implements MovieColumns, BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIES).build();

        public static final String TABLE_NAME = "movies";
        public static final String COLUMN_NAME_TITLE = "title";
        public static final String COLUMN_NAME_POSTER = "poster";
        public static final String COLUMN_NAME_DATE = "date";
        public static final String COLUMN_NAME_RATING = "rating";
        public static final String COLUMN_NAME_OVERVIEW = "overview";
    }

    public static final class MovieReviewsEntry implements MovieColumns, BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_REVIEWS).build();

        public static final String TABLE_NAME = "movie_details";
        public static final String COLUMN_NAME_AUTHOR = "author";
        public static final String COLUMN_NAME_CONTENT = "content";
    }

    public static final class MovieVideosEntry implements MovieColumns, BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_VIDEOS).build();

        public static final String TABLE_NAME = "movie_videos";
        public static final String COLUMN_NAME_SITE = "site";
        public static final String COLUMN_NAME_KEY = "key";
        public static final String COLUMN_NAME_TYPE = "type";
    }

    public static final class PopularMoviesEntry implements MovieColumns, BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_POPULAR).build();

        public static final String TABLE_NAME = "popular_movies";
    }

    public static final class TopRatedMoviesEntry implements MovieColumns, BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_TOP_RATED).build();

        public static final String TABLE_NAME = "top_rated_movies";
    }

    public static final class FavoriteMoviesEntry implements MovieColumns, BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_FAVORITES).build();

        public static final String TABLE_NAME = "favorite_movies";
    }
}

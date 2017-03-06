package com.example.android.popularmovies;

import android.provider.BaseColumns;

public final class MoviesContract {

    // We make the constructor private to prevent from accidental instantiations.
    private MoviesContract() {}

    public static final class MoviesEntry implements BaseColumns {
        public static final String TABLE_NAME = "movies";
        public static final String COLUMN_NAME_ID = "id";
        public static final String COLUMN_NAME_TITLE = "title";
        public static final String COLUMN_NAME_POSTER = "poster";
        public static final String COLUMN_NAME_DATE = "date";
        public static final String COLUMN_NAME_RATING = "rating";
        public static final String COLUMN_NAME_OVERVIEW = "overview";
        public static final String COLUMN_NAME_FAVORITE = "favorite";
        public static final String COLUMN_NAME_POPULAR = "popular";
        public static final String COLUMN_NAME_TOP_RATED = "top_rated";
    }
}

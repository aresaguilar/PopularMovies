package com.example.android.popularmovies;

import android.provider.BaseColumns;

public final class FavoriteMoviesContract {

    // We make the constructor private to prevent from accidental instantiations.
    private FavoriteMoviesContract() {}

    public static final class FavoriteMoviesEntry implements BaseColumns {
        public static final String TABLE_NAME = "favorites";
        public static final String COLUMN_NAME_ID = "id";
        public static final String COLUMN_NAME_TITLE = "title";

        // TODO Add all info to columns
    }
}

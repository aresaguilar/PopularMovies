package com.example.android.popularmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MoviesDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "movies.db";

    private static final int DATABASE_VERSION = 2;

    public MoviesDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_MOVIES_TABLE = "CREATE TABLE " +
                MoviesContract.MoviesEntry.TABLE_NAME + " (" +
                MoviesContract.MoviesEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                MoviesContract.MoviesEntry.COLUMN_NAME_MOVIE_ID + " TEXT NOT NULL, " +
                MoviesContract.MoviesEntry.COLUMN_NAME_TITLE + " TEXT NOT NULL, " +
                MoviesContract.MoviesEntry.COLUMN_NAME_POSTER + " TEXT NOT NULL, " +
                MoviesContract.MoviesEntry.COLUMN_NAME_DATE + " TEXT NOT NULL, " +
                MoviesContract.MoviesEntry.COLUMN_NAME_RATING + " TEXT NOT NULL, " +
                MoviesContract.MoviesEntry.COLUMN_NAME_OVERVIEW + " TEXT NOT NULL, " +
                MoviesContract.MoviesEntry.COLUMN_NAME_FAVORITE + " INTEGER DEFAULT 0, " +
                MoviesContract.MoviesEntry.COLUMN_NAME_POPULAR + " INTEGER DEFAULT 0, " +
                MoviesContract.MoviesEntry.COLUMN_NAME_TOP_RATED + " INTEGER DEFAULT 0" +
                ");";
        final String SQL_CREATE_MOVIE_REVIEWS_TABLE = "CREATE TABLE " +
                MoviesContract.MovieReviewsEntry.TABLE_NAME + " (" +
                MoviesContract.MovieReviewsEntry.COLUMN_NAME_AUTHOR + " TEXT NOT NULL, " +
                MoviesContract.MovieReviewsEntry.COLUMN_NAME_CONTENT + " TEXT NOT NULL, " +
                MoviesContract.MovieReviewsEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                MoviesContract.MovieReviewsEntry.COLUMN_NAME_MOVIE_ID + " INTEGER FOREIGN KEY (" +
                MoviesContract.MovieReviewsEntry.COLUMN_NAME_MOVIE_ID + ") REFERENCES " +
                MoviesContract.MoviesEntry.TABLE_NAME + " ("
                + MoviesContract.MoviesEntry.COLUMN_NAME_MOVIE_ID + ")" +
                ");";
        final String SQL_CREATE_MOVIE_VIDEOS_TABLE = "CREATE TABLE " +
                MoviesContract.MovieVideosEntry.TABLE_NAME + " (" +
                MoviesContract.MovieVideosEntry.COLUMN_NAME_SITE + " TEXT NOT NULL, " +
                MoviesContract.MovieVideosEntry.COLUMN_NAME_KEY + " TEXT NOT NULL, " +
                MoviesContract.MovieVideosEntry.COLUMN_NAME_TYPE + " TEXT NOT NULL, " +
                MoviesContract.MovieVideosEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                MoviesContract.MovieVideosEntry.COLUMN_NAME_MOVIE_ID + " INTEGER FOREIGN KEY (" +
                MoviesContract.MovieVideosEntry.COLUMN_NAME_MOVIE_ID + ") REFERENCES " +
                MoviesContract.MoviesEntry.TABLE_NAME + " ("
                + MoviesContract.MoviesEntry.COLUMN_NAME_MOVIE_ID + ")" +
                ");";
        final String SQL_CREATE_POPULAR_MOVIES_TABLE = "CREATE TABLE " +
                MoviesContract.PopularMoviesEntry.TABLE_NAME + " (" +
                MoviesContract.PopularMoviesEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                MoviesContract.PopularMoviesEntry.COLUMN_NAME_MOVIE_ID + " INTEGER FOREIGN KEY (" +
                MoviesContract.PopularMoviesEntry.COLUMN_NAME_MOVIE_ID + ") REFERENCES " +
                MoviesContract.MoviesEntry.TABLE_NAME + " ("
                + MoviesContract.MoviesEntry.COLUMN_NAME_MOVIE_ID + ")" +
                ");";
        final String SQL_CREATE_TOP_RATED_MOVIES_TABLE = "CREATE TABLE " +
                MoviesContract.TopRatedMoviesEntry.TABLE_NAME + " (" +
                MoviesContract.TopRatedMoviesEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                MoviesContract.TopRatedMoviesEntry.COLUMN_NAME_MOVIE_ID + " INTEGER FOREIGN KEY (" +
                MoviesContract.TopRatedMoviesEntry.COLUMN_NAME_MOVIE_ID + ") REFERENCES " +
                MoviesContract.MoviesEntry.TABLE_NAME + " ("
                + MoviesContract.MoviesEntry.COLUMN_NAME_MOVIE_ID + ")" +
                ");";
        final String SQL_CREATE_FAVORITE_MOVIES_TABLE = "CREATE TABLE " +
                MoviesContract.FavoriteMoviesEntry.TABLE_NAME + " (" +
                MoviesContract.FavoriteMoviesEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                MoviesContract.FavoriteMoviesEntry.COLUMN_NAME_MOVIE_ID + " INTEGER FOREIGN KEY (" +
                MoviesContract.FavoriteMoviesEntry.COLUMN_NAME_MOVIE_ID + ") REFERENCES " +
                MoviesContract.MoviesEntry.TABLE_NAME + " ("
                + MoviesContract.MoviesEntry.COLUMN_NAME_MOVIE_ID + ")" +
                ");";
        db.execSQL(SQL_CREATE_MOVIES_TABLE);
        db.execSQL(SQL_CREATE_MOVIE_REVIEWS_TABLE);
        db.execSQL(SQL_CREATE_MOVIE_VIDEOS_TABLE);
        db.execSQL(SQL_CREATE_POPULAR_MOVIES_TABLE);
        db.execSQL(SQL_CREATE_TOP_RATED_MOVIES_TABLE);
        db.execSQL(SQL_CREATE_FAVORITE_MOVIES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + MoviesContract.MoviesEntry.TABLE_NAME);
        onCreate(db);
    }
}

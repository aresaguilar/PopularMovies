package com.example.android.popularmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MoviesDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "movies.db";

    private static final int DATABASE_VERSION = 1;

    public MoviesDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_MOVIES_TABLE = "CREATE TABLE " +
                MoviesContract.MoviesEntry.TABLE_NAME + " (" +
                MoviesContract.MoviesEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                MoviesContract.MoviesEntry.COLUMN_NAME_ID + " TEXT NOT NULL, " +
                MoviesContract.MoviesEntry.COLUMN_NAME_TITLE + " TEXT NOT NULL, " +
                MoviesContract.MoviesEntry.COLUMN_NAME_POSTER + " TEXT NOT NULL, " +
                MoviesContract.MoviesEntry.COLUMN_NAME_DATE + " TEXT NOT NULL, " +
                MoviesContract.MoviesEntry.COLUMN_NAME_RATING + " TEXT NOT NULL, " +
                MoviesContract.MoviesEntry.COLUMN_NAME_OVERVIEW + " TEXT NOT NULL, " +
                MoviesContract.MoviesEntry.COLUMN_NAME_FAVORITE + " INTEGER DEFAULT 0, " +
                MoviesContract.MoviesEntry.COLUMN_NAME_POPULAR + " INTEGER DEFAULT 0, " +
                MoviesContract.MoviesEntry.COLUMN_NAME_TOP_RATED + " INTEGER DEFAULT 0" +
                ");";
        db.execSQL(SQL_CREATE_MOVIES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + MoviesContract.MoviesEntry.TABLE_NAME);
        onCreate(db);
    }
}

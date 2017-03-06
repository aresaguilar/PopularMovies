package com.example.android.popularmovies;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.android.popularmovies.MoviesContract.MoviesEntry;

public class MoviesDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "movies.db";

    private static final int DATABASE_VERSION = 1;

    public MoviesDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_MOVIES_TABLE = "CREATE TABLE " +
                MoviesEntry.TABLE_NAME + " (" +
                MoviesEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                MoviesEntry.COLUMN_NAME_ID + " TEXT NOT NULL, " +
                MoviesEntry.COLUMN_NAME_TITLE + " TEXT NOT NULL, " +
                MoviesEntry.COLUMN_NAME_POSTER + " TEXT NOT NULL, " +
                MoviesEntry.COLUMN_NAME_DATE + " TEXT NOT NULL, " +
                MoviesEntry.COLUMN_NAME_RATING + " TEXT NOT NULL, " +
                MoviesEntry.COLUMN_NAME_OVERVIEW + " TEXT NOT NULL, " +
                MoviesEntry.COLUMN_NAME_FAVORITE + " INTEGER DEFAULT 0, " +
                MoviesEntry.COLUMN_NAME_POPULAR + " INTEGER DEFAULT 0, " +
                MoviesEntry.COLUMN_NAME_TOP_RATED + " INTEGER DEFAULT 0" +
                ");";
        db.execSQL(SQL_CREATE_MOVIES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + MoviesEntry.TABLE_NAME);
        onCreate(db);
    }
}

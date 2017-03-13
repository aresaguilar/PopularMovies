package com.example.android.popularmovies.data;


import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import static com.example.android.popularmovies.data.MoviesContract.FavoriteMoviesEntry;
import static com.example.android.popularmovies.data.MoviesContract.MovieReviewsEntry;
import static com.example.android.popularmovies.data.MoviesContract.MovieVideosEntry;
import static com.example.android.popularmovies.data.MoviesContract.MoviesEntry;
import static com.example.android.popularmovies.data.MoviesContract.PopularMoviesEntry;
import static com.example.android.popularmovies.data.MoviesContract.TopRatedMoviesEntry;

public class MoviesContentProvider extends ContentProvider {

    private MoviesDbHelper mMoviesDbHelper;

    private static final int MOVIES = 100;
    private static final int MOVIE_WITH_ID = 101;
    private static final int MOVIE_REVIEWS = 200;
    private static final int MOVIE_REVIEWS_WITH_ID = 201;
    private static final int MOVIE_VIDEOS = 300;
    private static final int MOVIE_VIDEOS_WITH_ID = 301;
    private static final int POPULAR_MOVIES = 400;
    private static final int TOP_RATED_MOVIES = 500;
    private static final int FAVORITE_MOVIES = 600;

    private static final UriMatcher sUriMatcher = buildUriMatcher();

    public static UriMatcher buildUriMatcher() {
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        uriMatcher.addURI(MoviesContract.AUTHORITY, MoviesContract.PATH_MOVIES, MOVIES);
        uriMatcher.addURI(MoviesContract.AUTHORITY, MoviesContract.PATH_MOVIES + "/#", MOVIE_WITH_ID);
        uriMatcher.addURI(MoviesContract.AUTHORITY, MoviesContract.PATH_REVIEWS, MOVIE_REVIEWS);
        uriMatcher.addURI(MoviesContract.AUTHORITY, MoviesContract.PATH_REVIEWS + "/#", MOVIE_REVIEWS_WITH_ID);
        uriMatcher.addURI(MoviesContract.AUTHORITY, MoviesContract.PATH_VIDEOS, MOVIE_VIDEOS);
        uriMatcher.addURI(MoviesContract.AUTHORITY, MoviesContract.PATH_VIDEOS + "/#", MOVIE_VIDEOS_WITH_ID);
        uriMatcher.addURI(MoviesContract.AUTHORITY, MoviesContract.PATH_POPULAR, POPULAR_MOVIES);
        uriMatcher.addURI(MoviesContract.AUTHORITY, MoviesContract.PATH_TOP_RATED, TOP_RATED_MOVIES);
        uriMatcher.addURI(MoviesContract.AUTHORITY, MoviesContract.PATH_FAVORITES, FAVORITE_MOVIES);

        return uriMatcher;
    }

    @Override
    public boolean onCreate() {
        mMoviesDbHelper = new MoviesDbHelper(getContext());

        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        return null;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Override
    public int bulkInsert(@NonNull Uri uri, @NonNull ContentValues[] values) {
        // TODO Implement
        return super.bulkInsert(uri, values);
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {

        final SQLiteDatabase db = mMoviesDbHelper.getWritableDatabase();

        int match = sUriMatcher.match(uri);

        Uri returnUri;
        long id;

        switch (match) {
            case MOVIES:
                id = db.insert(MoviesEntry.TABLE_NAME, null, values);
                if (id > 0) {
                    returnUri = ContentUris.withAppendedId(MoviesEntry.CONTENT_URI, id);
                } else {
                    throw new SQLiteException("Failed to insert row into " + uri);
                }
                break;
            case MOVIE_REVIEWS:
                id = db.insert(MovieReviewsEntry.TABLE_NAME, null, values);
                if (id > 0) {
                    returnUri = ContentUris.withAppendedId(MovieReviewsEntry.CONTENT_URI, id);
                } else {
                    throw new SQLiteException("Failed to insert row into " + uri);
                }
                break;
            case MOVIE_VIDEOS:
                id = db.insert(MovieVideosEntry.TABLE_NAME, null, values);
                if (id > 0) {
                    returnUri = ContentUris.withAppendedId(MovieVideosEntry.CONTENT_URI, id);
                } else {
                    throw new SQLiteException("Failed to insert row into " + uri);
                }
                break;
            case POPULAR_MOVIES:
                id = db.insert(MoviesEntry.TABLE_NAME, null, values);
                if (id > 0) {
                    returnUri = ContentUris.withAppendedId(MoviesEntry.CONTENT_URI, id);
                    String movieId = values.getAsString(MoviesEntry.COLUMN_NAME_MOVIE_ID);
                    ContentValues cv = new ContentValues();
                    cv.put(PopularMoviesEntry.COLUMN_NAME_MOVIE_ID, movieId);
                    if (db.insert(PopularMoviesEntry.TABLE_NAME, null, cv) <= 0) {
                        throw new SQLiteException("Failed to insert row into popular");
                    }
                } else {
                    throw new SQLiteException("Failed to insert row into " + uri);
                }
                break;
            case TOP_RATED_MOVIES:
                id = db.insert(MoviesEntry.TABLE_NAME, null, values);
                if (id > 0) {
                    returnUri = ContentUris.withAppendedId(MoviesEntry.CONTENT_URI, id);
                    String movieId = values.getAsString(MoviesEntry.COLUMN_NAME_MOVIE_ID);
                    ContentValues cv = new ContentValues();
                    cv.put(TopRatedMoviesEntry.COLUMN_NAME_MOVIE_ID, movieId);
                    if (db.insert(TopRatedMoviesEntry.TABLE_NAME, null, cv) <= 0) {
                        throw new SQLiteException("Failed to insert row into top rated");
                    }
                } else {
                    throw new SQLiteException("Failed to insert row into " + uri);
                }
                break;
            case FAVORITE_MOVIES:
                id = db.insert(FavoriteMoviesEntry.TABLE_NAME, null, values);
                if (id > 0) {
                    returnUri = ContentUris.withAppendedId(FavoriteMoviesEntry.CONTENT_URI, id);
                } else {
                    throw new SQLiteException("Failed to insert row into " + uri);
                }
                break;
            default:
                throw new UnsupportedOperationException("Unknown Uri: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }
}

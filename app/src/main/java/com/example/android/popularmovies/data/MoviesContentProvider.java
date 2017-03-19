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
    private static final int FAVORITE_MOVIE_WITH_ID = 601;

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
        uriMatcher.addURI(MoviesContract.AUTHORITY, MoviesContract.PATH_FAVORITES + "/#", FAVORITE_MOVIE_WITH_ID);

        return uriMatcher;
    }

    public static String buildInnerJoin(String table1, String table2,
                                        String table1Id, String table2Id) {
        return table1 + " INNER JOIN " + table2 + " ON " +
                table1 + "." + table1Id + "=" + table2 + "." + table2Id;
    }

    @Override
    public boolean onCreate() {
        mMoviesDbHelper = new MoviesDbHelper(getContext());

        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection,
                        @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        final SQLiteDatabase db = mMoviesDbHelper.getReadableDatabase();
        int match = sUriMatcher.match(uri);
        Cursor retCursor;
        String id;
        String mSelection;
        String[] mSelectionArgs;

        switch (match) {
            case MOVIES:
                retCursor = db.query(MoviesEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case POPULAR_MOVIES:
                retCursor = db.query(
                        buildInnerJoin(
                                MoviesEntry.TABLE_NAME,
                                PopularMoviesEntry.TABLE_NAME,
                                MoviesEntry.COLUMN_NAME_MOVIE_ID,
                                PopularMoviesEntry.COLUMN_NAME_MOVIE_ID),
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case TOP_RATED_MOVIES:
                retCursor = db.query(
                        buildInnerJoin(
                                MoviesEntry.TABLE_NAME,
                                TopRatedMoviesEntry.TABLE_NAME,
                                MoviesEntry.COLUMN_NAME_MOVIE_ID,
                                TopRatedMoviesEntry.COLUMN_NAME_MOVIE_ID),
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case FAVORITE_MOVIES:
                retCursor = db.query(
                        buildInnerJoin(
                                MoviesEntry.TABLE_NAME,
                                FavoriteMoviesEntry.TABLE_NAME,
                                MoviesEntry.COLUMN_NAME_MOVIE_ID,
                                FavoriteMoviesEntry.COLUMN_NAME_MOVIE_ID),
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case MOVIE_WITH_ID:
                id = uri.getPathSegments().get(1);
                mSelection = MoviesEntry.COLUMN_NAME_MOVIE_ID + "=?";
                mSelectionArgs = new String[]{id};

                retCursor = db.query(MoviesEntry.TABLE_NAME,
                        projection,
                        mSelection,
                        mSelectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case MOVIE_REVIEWS_WITH_ID:
                id = uri.getPathSegments().get(1);
                mSelection = MovieReviewsEntry.COLUMN_NAME_MOVIE_ID + "=?";
                mSelectionArgs = new String[]{id};

                retCursor = db.query(MovieReviewsEntry.TABLE_NAME,
                        projection,
                        mSelection,
                        mSelectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case MOVIE_VIDEOS_WITH_ID:
                id = uri.getPathSegments().get(1);
                mSelection = MovieVideosEntry.COLUMN_NAME_MOVIE_ID + "=?";
                mSelectionArgs = new String[]{id};

                retCursor = db.query(MovieVideosEntry.TABLE_NAME,
                        projection,
                        mSelection,
                        mSelectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case FAVORITE_MOVIE_WITH_ID:
                id = uri.getPathSegments().get(1);
                mSelection = MovieVideosEntry.COLUMN_NAME_MOVIE_ID + "=?";
                mSelectionArgs = new String[]{id};

                retCursor = db.query(FavoriteMoviesEntry.TABLE_NAME,
                        projection,
                        mSelection,
                        mSelectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri " + uri);
        }

        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public int bulkInsert(@NonNull Uri uri, @NonNull ContentValues[] values) {

        final SQLiteDatabase db = mMoviesDbHelper.getWritableDatabase();

        int match = sUriMatcher.match(uri);
        int rowsInserted;

        switch (match) {
            case MOVIES:
                db.beginTransaction();
                rowsInserted = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insertWithOnConflict(MoviesEntry.TABLE_NAME, null, value, SQLiteDatabase.CONFLICT_IGNORE);
                        if (_id != -1) {
                            rowsInserted++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                break;
            case POPULAR_MOVIES:
                db.beginTransaction();
                rowsInserted = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insertWithOnConflict(MoviesEntry.TABLE_NAME, null, value, SQLiteDatabase.CONFLICT_IGNORE);
                        if (_id != -1) {
                            String movieId = value.getAsString(MoviesEntry.COLUMN_NAME_MOVIE_ID);
                            ContentValues cv = new ContentValues();
                            cv.put(PopularMoviesEntry.COLUMN_NAME_MOVIE_ID, movieId);
                            if (db.insertWithOnConflict(PopularMoviesEntry.TABLE_NAME, null, cv, SQLiteDatabase.CONFLICT_IGNORE) != -1) {
                                rowsInserted++;
                            }
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                break;
            case TOP_RATED_MOVIES:
                db.beginTransaction();
                rowsInserted = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insertWithOnConflict(MoviesEntry.TABLE_NAME, null, value, SQLiteDatabase.CONFLICT_IGNORE);
                        if (_id != -1) {
                            String movieId = value.getAsString(MoviesEntry.COLUMN_NAME_MOVIE_ID);
                            ContentValues cv = new ContentValues();
                            cv.put(TopRatedMoviesEntry.COLUMN_NAME_MOVIE_ID, movieId);
                            if (db.insertWithOnConflict(TopRatedMoviesEntry.TABLE_NAME, null, cv, SQLiteDatabase.CONFLICT_IGNORE) != -1) {
                                rowsInserted++;
                            }
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                break;
            case MOVIE_REVIEWS:
                db.beginTransaction();
                rowsInserted = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insertWithOnConflict(MovieReviewsEntry.TABLE_NAME, null, value, SQLiteDatabase.CONFLICT_IGNORE);
                        if (_id != -1) {
                            rowsInserted++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                break;
            case MOVIE_VIDEOS:
                db.beginTransaction();
                rowsInserted = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insertWithOnConflict(MovieVideosEntry.TABLE_NAME, null, value, SQLiteDatabase.CONFLICT_IGNORE);
                        if (_id != -1) {
                            rowsInserted++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                break;
            case MOVIE_REVIEWS_WITH_ID:
            case MOVIE_VIDEOS_WITH_ID:
            default:
                return super.bulkInsert(uri, values);
        }

        if (rowsInserted > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsInserted;
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
                id = db.insertWithOnConflict(MoviesEntry.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_IGNORE);
                if (id > 0) {
                    returnUri = ContentUris.withAppendedId(MoviesEntry.CONTENT_URI, id);
                } else {
                    throw new SQLiteException("Failed to insert row into " + uri);
                }
                break;
            case MOVIE_REVIEWS:
                id = db.insertWithOnConflict(MovieReviewsEntry.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_IGNORE);
                if (id > 0) {
                    returnUri = ContentUris.withAppendedId(MovieReviewsEntry.CONTENT_URI, id);
                } else {
                    throw new SQLiteException("Failed to insert row into " + uri);
                }
                break;
            case MOVIE_VIDEOS:
                id = db.insertWithOnConflict(MovieVideosEntry.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_IGNORE);
                if (id > 0) {
                    returnUri = ContentUris.withAppendedId(MovieVideosEntry.CONTENT_URI, id);
                } else {
                    throw new SQLiteException("Failed to insert row into " + uri);
                }
                break;
            case POPULAR_MOVIES:
                id = db.insertWithOnConflict(MoviesEntry.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_IGNORE);
                if (id > 0) {
                    returnUri = ContentUris.withAppendedId(MoviesEntry.CONTENT_URI, id);
                    String movieId = values.getAsString(MoviesEntry.COLUMN_NAME_MOVIE_ID);
                    ContentValues cv = new ContentValues();
                    cv.put(PopularMoviesEntry.COLUMN_NAME_MOVIE_ID, movieId);
                    if (db.insertWithOnConflict(PopularMoviesEntry.TABLE_NAME, null, cv, SQLiteDatabase.CONFLICT_IGNORE) <= 0) {
                        throw new SQLiteException("Failed to insert row into popular");
                    }
                } else {
                    throw new SQLiteException("Failed to insert row into " + uri);
                }
                break;
            case TOP_RATED_MOVIES:
                id = db.insertWithOnConflict(MoviesEntry.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_IGNORE);
                if (id > 0) {
                    returnUri = ContentUris.withAppendedId(MoviesEntry.CONTENT_URI, id);
                    String movieId = values.getAsString(MoviesEntry.COLUMN_NAME_MOVIE_ID);
                    ContentValues cv = new ContentValues();
                    cv.put(TopRatedMoviesEntry.COLUMN_NAME_MOVIE_ID, movieId);
                    if (db.insertWithOnConflict(TopRatedMoviesEntry.TABLE_NAME, null, cv, SQLiteDatabase.CONFLICT_IGNORE) <= 0) {
                        throw new SQLiteException("Failed to insert row into top rated");
                    }
                } else {
                    throw new SQLiteException("Failed to insert row into " + uri);
                }
                break;
            case FAVORITE_MOVIES:
                id = db.insertWithOnConflict(FavoriteMoviesEntry.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_IGNORE);
                if (id > 0) {
                    returnUri = ContentUris.withAppendedId(FavoriteMoviesEntry.CONTENT_URI, id);
                } else {
                    throw new SQLiteException("Failed to insert row into " + uri);
                }
                break;
            case FAVORITE_MOVIE_WITH_ID:
                String movieId =  uri.getPathSegments().get(1);
                ContentValues cv = new ContentValues();
                cv.put(FavoriteMoviesEntry.COLUMN_NAME_MOVIE_ID, movieId);
                id = db.insertWithOnConflict(FavoriteMoviesEntry.TABLE_NAME, null, cv, SQLiteDatabase.CONFLICT_IGNORE);
                returnUri = ContentUris.withAppendedId(FavoriteMoviesEntry.CONTENT_URI, id);
                break;
            default:
                throw new UnsupportedOperationException("Unknown Uri: " + uri);
        }

        getContext().getContentResolver().notifyChange(MoviesContract.BASE_CONTENT_URI, null);
        return returnUri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        final SQLiteDatabase db = mMoviesDbHelper.getWritableDatabase();

        int match = sUriMatcher.match(uri);
        String id;
        String mSelection;
        String[] mSelectionArgs;
        int numberOfItemsDeleted;

        switch (match) {
            case MOVIES:
                numberOfItemsDeleted = db.delete(
                        MoviesEntry.TABLE_NAME,
                        null,
                        null);
                break;
            case POPULAR_MOVIES:
                numberOfItemsDeleted = db.delete(
                        PopularMoviesEntry.TABLE_NAME,
                        null,
                        null);
                break;
            case TOP_RATED_MOVIES:
                numberOfItemsDeleted = db.delete(
                        TopRatedMoviesEntry.TABLE_NAME,
                        null,
                        null);
                break;
            case FAVORITE_MOVIES:
                numberOfItemsDeleted = db.delete(
                        FavoriteMoviesEntry.TABLE_NAME,
                        null,
                        null);
                break;
            case MOVIE_REVIEWS:
                numberOfItemsDeleted = db.delete(
                        MovieReviewsEntry.TABLE_NAME,
                        null,
                        null);
                break;
            case MOVIE_VIDEOS:
                numberOfItemsDeleted = db.delete(
                        MovieVideosEntry.TABLE_NAME,
                        null,
                        null);
                break;
            case MOVIE_WITH_ID:
                id = uri.getPathSegments().get(1);
                mSelection = MoviesEntry.COLUMN_NAME_MOVIE_ID + "=?";
                mSelectionArgs = new String[]{id};
                numberOfItemsDeleted = db.delete(
                        MoviesEntry.TABLE_NAME,
                        mSelection,
                        mSelectionArgs);
                break;
            case MOVIE_REVIEWS_WITH_ID:
                id = uri.getPathSegments().get(1);
                mSelection = MovieReviewsEntry.COLUMN_NAME_MOVIE_ID + "=?";
                mSelectionArgs = new String[]{id};
                numberOfItemsDeleted = db.delete(
                        MovieReviewsEntry.TABLE_NAME,
                        mSelection,
                        mSelectionArgs);
                break;
            case MOVIE_VIDEOS_WITH_ID:
                id = uri.getPathSegments().get(1);
                mSelection = MovieVideosEntry.COLUMN_NAME_MOVIE_ID + "=?";
                mSelectionArgs = new String[]{id};
                numberOfItemsDeleted = db.delete(
                        MovieVideosEntry.TABLE_NAME,
                        mSelection,
                        mSelectionArgs);
                break;
            case FAVORITE_MOVIE_WITH_ID:
                id = uri.getPathSegments().get(1);
                mSelection = FavoriteMoviesEntry.COLUMN_NAME_MOVIE_ID + "=?";
                mSelectionArgs = new String[]{id};
                numberOfItemsDeleted = db.delete(
                        FavoriteMoviesEntry.TABLE_NAME,
                        mSelection,
                        mSelectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown Uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);

        return numberOfItemsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        throw new UnsupportedOperationException("Update not implemented");
    }
}

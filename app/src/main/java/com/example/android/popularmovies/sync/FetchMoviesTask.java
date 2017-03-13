package com.example.android.popularmovies.sync;


import android.content.ContentValues;
import android.content.Context;
import android.util.Log;

import com.example.android.popularmovies.BuildConfig;
import com.example.android.popularmovies.data.MoviesContract;
import com.example.android.popularmovies.model.Movie;
import com.example.android.popularmovies.model.MovieUtils;
import com.example.android.popularmovies.model.NetworkUtils;

import java.io.IOException;
import java.net.URL;
import java.util.List;

public class FetchMoviesTask {

    public static final String ACTION_FETCH_POPULAR_MOVIES_TASK = "fetch_popular_movies";
    public static final String ACTION_FETCH_TOP_RATED_MOVIES_TASK = "fetch_top_rated_movies";

    private static final String TAG = FetchMoviesTask.class.getSimpleName();

    public static void executeTask(Context context, String action) {
        String moviesQueryUrlString;

        /* Create URL */
        if (ACTION_FETCH_POPULAR_MOVIES_TASK.equals(action)) {
            moviesQueryUrlString =
                    String.valueOf(NetworkUtils.buildUrl(NetworkUtils.POPULAR_EP, BuildConfig.THEMOVIEDB_API_KEY));
        } else if (ACTION_FETCH_TOP_RATED_MOVIES_TASK.equals(action)) {
            moviesQueryUrlString =
                    String.valueOf(NetworkUtils.buildUrl(NetworkUtils.TOPRATED_EP, BuildConfig.THEMOVIEDB_API_KEY));
        } else {
            throw new UnsupportedOperationException("Action not supported " + action);
        }

        /* Fetch data */
        List<Movie> movieArrayList;
        try {
            URL moviesUrl = new URL(moviesQueryUrlString);
            movieArrayList =
                    MovieUtils.parseMoviesJSON(NetworkUtils.getResponseFromHttpUrl(moviesUrl));
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "Error getting response");
            return;
        }

        ContentValues[] contentValues = MovieUtils.movieListToCvArray(movieArrayList);
        if (ACTION_FETCH_POPULAR_MOVIES_TASK.equals(action)) {
            context.getContentResolver().bulkInsert(MoviesContract.PopularMoviesEntry.CONTENT_URI, contentValues);
        } else if (ACTION_FETCH_TOP_RATED_MOVIES_TASK.equals(action)) {
            context.getContentResolver().bulkInsert(MoviesContract.TopRatedMoviesEntry.CONTENT_URI, contentValues);
        }

        
    }
}

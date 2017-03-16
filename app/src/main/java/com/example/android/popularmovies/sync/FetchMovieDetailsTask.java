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

public class FetchMovieDetailsTask {

    private static final String TAG = FetchMovieDetailsTask.class.getSimpleName();

    public static void executeTask(Context context, String movieId) {
        String movieReviewsUrlString = String.valueOf(NetworkUtils.buildUrl(
                movieId + NetworkUtils.REVIEWS_EP,
                BuildConfig.THEMOVIEDB_API_KEY));
        String movieVideosUrlString = String.valueOf(NetworkUtils.buildUrl(
                movieId + NetworkUtils.VIDEOS_EP,
                BuildConfig.THEMOVIEDB_API_KEY));

        List<Movie.MovieReview> movieReviewList;
        List<Movie.MovieVideo> movieVideoList;

        try {
            URL reviewsUrl = new URL(movieReviewsUrlString);
            URL videosUrl = new URL(movieVideosUrlString);

            movieReviewList =
                    MovieUtils.parseReviewsJSON(NetworkUtils.getResponseFromHttpUrl(reviewsUrl));
            movieVideoList =
                    MovieUtils.parseVideosJSON(NetworkUtils.getResponseFromHttpUrl(videosUrl));
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "Error getting response");
            return;
        }

        ContentValues[] reviewContentValues =
                MovieUtils.movieReviewListToCvArray(movieId, movieReviewList);
        ContentValues[] videoContentValues =
                MovieUtils.movieVideoListToCvArray(movieId, movieVideoList);
        context.getContentResolver().bulkInsert(MoviesContract.MovieReviewsEntry.CONTENT_URI, reviewContentValues);
        context.getContentResolver().bulkInsert(MoviesContract.MovieVideosEntry.CONTENT_URI, videoContentValues);
    }
}

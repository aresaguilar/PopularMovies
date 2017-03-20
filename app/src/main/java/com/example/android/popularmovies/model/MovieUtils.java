package com.example.android.popularmovies.model;

import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;

import com.example.android.popularmovies.data.MoviesContract;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MovieUtils {

    private static final String TAG = MovieUtils.class.getSimpleName();

    private static final String POSTER_BASE_URL = "http://image.tmdb.org/t/p/";
    public static final String POSTER_SIZE_MOBILE = "w185";

    public static String getBestTrailerYouTubeKey(Cursor trailers) {
        String key = null;

        trailers.moveToFirst();
        while (trailers.moveToNext()) {
            if (!"YouTube".equals(trailers.getString(trailers.getColumnIndex(MoviesContract.MovieVideosEntry.COLUMN_NAME_SITE))))
                continue;
            key = trailers.getString(trailers.getColumnIndex(MoviesContract.MovieVideosEntry.COLUMN_NAME_KEY));
            if ("Trailer".equals(trailers.getString(trailers.getColumnIndex(MoviesContract.MovieVideosEntry.COLUMN_NAME_TYPE))))
                return key;
        }
        return key;
    }

    public static List<Movie> parseMoviesJSON(String jsonData) {
        ArrayList<Movie> movieArrayList = new ArrayList<>();

        try {
            JSONObject mainObject = new JSONObject(jsonData);

            JSONArray resultsArray = mainObject.getJSONArray("results");
            for (int i = 0; i < resultsArray.length(); i++) {
                JSONObject indexObject = resultsArray.getJSONObject(i);
                Movie indexMovie = new Movie(
                        indexObject.getInt("id"),
                        indexObject.getString("title"),
                        indexObject.getString("original_title"),
                        indexObject.getString("overview"),
                        indexObject.getString("release_date"),
                        indexObject.getString("poster_path"),
                        indexObject.getString("backdrop_path"),
                        indexObject.getDouble("popularity"),
                        indexObject.getInt("vote_average"),
                        indexObject.getInt("vote_count"));

                movieArrayList.add(indexMovie);
            }
            Log.d(TAG, "Parsed " + resultsArray.length() + " results");
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e(TAG, "JSON Error: " + jsonData);
        }

        return movieArrayList;
    }

    public static List<Movie.MovieReview> parseReviewsJSON (String jsonData) {
        ArrayList<Movie.MovieReview> movieReviewArrayList = new ArrayList<>();

        try {
            JSONObject mainObject = new JSONObject(jsonData);

            JSONArray resultsArray = mainObject.getJSONArray("results");
            Movie m = new Movie();
            for (int i = 0; i < resultsArray.length(); i++) {
                JSONObject indexObject = resultsArray.getJSONObject(i);
                Movie.MovieReview indexMovieReview = m.new MovieReview(
                        indexObject.getString("author"),
                        indexObject.getString("content"),
                        indexObject.getString("id"));
                movieReviewArrayList.add(indexMovieReview);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e(TAG, "JSON Error: " + jsonData);
        }

        return movieReviewArrayList;
    }

    public static List<Movie.MovieVideo> parseVideosJSON (String jsonData) {
        ArrayList<Movie.MovieVideo> movieVideoArrayList = new ArrayList<>();

        try {
            JSONObject mainObject = new JSONObject(jsonData);

            JSONArray resultsArray = mainObject.getJSONArray("results");
            Movie m = new Movie();
            for (int i = 0; i < resultsArray.length(); i++) {
                JSONObject indexObject = resultsArray.getJSONObject(i);
                Movie.MovieVideo indexMovieReview = m.new MovieVideo(
                        indexObject.getString("site"),
                        indexObject.getString("key"),
                        indexObject.getString("type"));
                movieVideoArrayList.add(indexMovieReview);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e(TAG, "JSON Error: " + jsonData);
        }

        return movieVideoArrayList;
    }

    public static String getPosterUrl(String poster_path, String size) {
        return POSTER_BASE_URL + size + poster_path;
    }

    public static ContentValues movieToCv(Movie movie) {
        ContentValues cv = new ContentValues();

        cv.put(MoviesContract.MoviesEntry.COLUMN_NAME_MOVIE_ID, movie.getId());
        cv.put(MoviesContract.MoviesEntry.COLUMN_NAME_TITLE, movie.getTitle());
        cv.put(MoviesContract.MoviesEntry.COLUMN_NAME_POSTER, movie.getPoster_path());
        cv.put(MoviesContract.MoviesEntry.COLUMN_NAME_DATE, movie.getRelease_date());
        cv.put(MoviesContract.MoviesEntry.COLUMN_NAME_RATING, movie.getVote_average());
        cv.put(MoviesContract.MoviesEntry.COLUMN_NAME_OVERVIEW, movie.getOverview());

        return cv;
    }

    public static ContentValues movieReviewToCv(String movieId, Movie.MovieReview review) {
        ContentValues cv = new ContentValues();

        cv.put(MoviesContract.MovieReviewsEntry.COLUMN_NAME_MOVIE_ID, movieId);
        cv.put(MoviesContract.MovieReviewsEntry.COLUMN_NAME_ID, review.getId());
        cv.put(MoviesContract.MovieReviewsEntry.COLUMN_NAME_AUTHOR, review.getAuthor());
        cv.put(MoviesContract.MovieReviewsEntry.COLUMN_NAME_CONTENT, review.getReview());

        return cv;
    }

    public static  ContentValues movieVideoToCv(String movieId, Movie.MovieVideo video) {
        ContentValues cv = new ContentValues();

        cv.put(MoviesContract.MovieVideosEntry.COLUMN_NAME_MOVIE_ID, movieId);
        cv.put(MoviesContract.MovieVideosEntry.COLUMN_NAME_KEY, video.getKey());
        cv.put(MoviesContract.MovieVideosEntry.COLUMN_NAME_SITE, video.getSite());
        cv.put(MoviesContract.MovieVideosEntry.COLUMN_NAME_TYPE, video.getType());

        return cv;
    }

    public static ContentValues[] movieListToCvArray(List<Movie> movieList) {
        ContentValues[] cvArray;
        List<ContentValues> cvList = new ArrayList<>();

        for (Movie movie : movieList) {
            ContentValues cv = movieToCv(movie);
            cvList.add(cv);
        }

        cvArray = new ContentValues[cvList.size()];
        cvList.toArray(cvArray);
        return cvArray;
    }

    public static ContentValues[] movieReviewListToCvArray(String movieId, List<Movie.MovieReview> movieReviewList) {
        ContentValues[] cvArray;
        List<ContentValues> cvList = new ArrayList<>();

        for (Movie.MovieReview review : movieReviewList) {
            ContentValues cv = movieReviewToCv(movieId, review);
            cvList.add(cv);
        }

        cvArray = new ContentValues[cvList.size()];
        cvList.toArray(cvArray);
        return cvArray;
    }

    public static ContentValues[] movieVideoListToCvArray(String movieId, List<Movie.MovieVideo> movieVideoList) {
        ContentValues[] cvArray;
        List<ContentValues> cvList = new ArrayList<>();

        for (Movie.MovieVideo video : movieVideoList) {
            ContentValues cv = movieVideoToCv(movieId, video);
            cvList.add(cv);
        }

        cvArray = new ContentValues[cvList.size()];
        cvList.toArray(cvArray);
        return cvArray;
    }
}

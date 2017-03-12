package com.example.android.popularmovies.model;

import android.content.ContentValues;
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

    public static ArrayList<Movie> parseMoviesJSON(String jsonData) {
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
            Log.e(TAG, "JSON Error");
        }

        return movieArrayList;
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
}

package com.example.android.popularmovies;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MovieUtils {

    private static final String TAG = MovieUtils.class.getSimpleName();

    private static final String POSTER_BASE_URL = "http://image.tmdb.org/t/p/";
    public static final String POSTER_SIZE_MOBILE = "w185";

    public static void parseJSON(String jsonData, ArrayList<Movie> movieArrayList) {
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
    }

    public static String getPosterUrl(String poster_path, String size) {
        return POSTER_BASE_URL + size + poster_path;
    }
}

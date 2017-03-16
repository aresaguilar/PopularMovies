package com.example.android.popularmovies.model;


import android.net.Uri;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

public class NetworkUtils {

    private static final String TAG = NetworkUtils.class.getSimpleName();

    private static final String TMDB_BASE_URL = "http://api.themoviedb.org/3/movie/";

    public static final String POPULAR_EP = "popular/";
    public static final String TOPRATED_EP = "top_rated/";
    public static final String VIDEOS_EP = "/videos";
    public static final String REVIEWS_EP = "/reviews";

    private static final String API_PARAM = "api_key";

    public static URL buildUrl(String end_point, String api_key) {
        Uri builtUri = Uri.parse(TMDB_BASE_URL + end_point).buildUpon()
                .appendQueryParameter(API_PARAM, api_key)
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        Log.v(TAG, "Built URI " + url);

        return url;
    }

    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }
}

package com.example.android.popularmovies.sync;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.Log;

public class FetchMovieDetailsIntentService extends IntentService {

    private static final String TAG = FetchMovieDetailsIntentService.class.getSimpleName();

    public FetchMovieDetailsIntentService() {
        super("FetchMovieDetailsIntentService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        String movieId = intent.getAction();
        Log.d(TAG, "Starting task with id " + movieId);
        FetchMovieDetailsTask.executeTask(this, movieId);
        Log.d(TAG, "FetchMovieDetailsTask finished");
    }
}

package com.example.android.popularmovies.sync;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.Log;


public class FetchMoviesIntentService extends IntentService {

    private static final String TAG = FetchMoviesIntentService.class.getSimpleName();

    public FetchMoviesIntentService() {
        super("FetchMoviesIntentService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        String action = intent.getAction();
        Log.d(TAG, "Starting task with action " + action);
        FetchMoviesTask.executeTask(this, action);
        Log.d(TAG, "FetchMoviesTask finished");
    }
}

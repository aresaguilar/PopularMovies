package com.example.android.popularmovies.sync;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;


public class FetchMoviesIntentService extends IntentService {

    public FetchMoviesIntentService() {
        super("FetchMoviesIntentService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        String action = intent.getAction();
        FetchMoviesTask.executeTask(this, action);
    }
}

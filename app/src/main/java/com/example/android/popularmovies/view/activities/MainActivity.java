package com.example.android.popularmovies.view.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.android.popularmovies.R;
import com.example.android.popularmovies.data.MoviesContract;
import com.example.android.popularmovies.sync.FetchMoviesIntentService;
import com.example.android.popularmovies.sync.FetchMoviesTask;
import com.example.android.popularmovies.view.adapters.MovieAdapter;

public class MainActivity extends AppCompatActivity
        implements MovieAdapter.ListItemClickListener,
            LoaderManager.LoaderCallbacks<Cursor>, SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    /* Static IDs */
    private static final int MOVIES_LOADER_ID = 66;
    public static final String MOVIE_EXTRA = "movie";

    /* View related variables */
    private RecyclerView mMoviesList;
    private int mPosition = RecyclerView.NO_POSITION;
    private MovieAdapter mAdapter;
    private ProgressBar mLoadingProgressBar;
    private Toast mToast;

    /* Preferences */
    private String sort_option;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /* Get references to layout elements */
        mMoviesList = (RecyclerView) findViewById(R.id.rv_movies);
        mLoadingProgressBar = (ProgressBar) findViewById(R.id.pb_loading_indicator);

        /* Create a layout manager using spanCount = 2 */
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        mMoviesList.setLayoutManager(gridLayoutManager);
        mMoviesList.setHasFixedSize(true);

        /* Load movies from database */
        mAdapter = new MovieAdapter(this, this);
        mMoviesList.setAdapter(mAdapter);

        setupSharedPreferences();

        showLoading();

        startMoviesIntent();

        getSupportLoaderManager().initLoader(MOVIES_LOADER_ID, null, this);
    }

    private void setupSharedPreferences() {
        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(this);

        sort_option = sharedPreferences.getString(getString(R.string.pref_sort_key),
                getString(R.string.pref_sort_popular_value));

        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
    }

    private void startMoviesIntent() {
        Intent fetchMoviesIntent = new Intent(this, FetchMoviesIntentService.class);

        if (sort_option.equals(getString(R.string.pref_sort_popular_value))) {
            fetchMoviesIntent.setAction(FetchMoviesTask.ACTION_FETCH_POPULAR_MOVIES_TASK);
        } else if (sort_option.equals(getString(R.string.pref_sort_top_value))) {
            fetchMoviesIntent.setAction(FetchMoviesTask.ACTION_FETCH_TOP_RATED_MOVIES_TASK);
        } else {
            return;
        }

        startService(fetchMoviesIntent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemThatWasClickedId = item.getItemId();
        if (itemThatWasClickedId == R.id.action_settings) {
            Intent startSettingsActivity = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(startSettingsActivity);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showRecyclerView() {
        this.mLoadingProgressBar.setVisibility(View.INVISIBLE);
        this.mMoviesList.setVisibility(View.VISIBLE);
    }

    private void showLoading() {
        this.mLoadingProgressBar.setVisibility(View.VISIBLE);
        this.mMoviesList.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onListItemClick(String id) {
        Intent intent = new Intent(MainActivity.this, MovieActivity.class);
        intent.putExtra(MOVIE_EXTRA, id);
        Log.d(TAG, "Launching activity with movie " + id);
        startActivity(intent);
    }

    @Override
    public void onListItemStar(String id) {
        if (mToast != null) {
            mToast.cancel();
        }
        mToast = Toast.makeText(this,
                id +" "+ getString(R.string.added_to_favorites_action),
                Toast.LENGTH_LONG);
        mToast.show();

        Uri uri = MoviesContract.FavoriteMoviesEntry.CONTENT_URI.buildUpon().appendPath(id).build();
        getContentResolver().insert(uri, null);
    }

    @Override
    public void onListItemUnstar(String id) {
        if (mToast != null) {
            mToast.cancel();
        }
        mToast = Toast.makeText(this,
                id +" "+ getString(R.string.removed_from_favorites_action),
                Toast.LENGTH_LONG);
        mToast.show();

        Uri uri = MoviesContract.FavoriteMoviesEntry.CONTENT_URI.buildUpon().appendPath(id).build();
        getContentResolver().delete(uri, null, null);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PreferenceManager.getDefaultSharedPreferences(this)
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case MOVIES_LOADER_ID:
                Uri moviesQueryUri;
                if (sort_option.equals(getString(R.string.pref_sort_popular_value)))
                    moviesQueryUri = MoviesContract.PopularMoviesEntry.CONTENT_URI;
                else if (sort_option.equals(getString(R.string.pref_sort_top_value)))
                    moviesQueryUri = MoviesContract.TopRatedMoviesEntry.CONTENT_URI;
                else if (sort_option.equals(getString(R.string.pref_sort_favorites_value)))
                    moviesQueryUri = MoviesContract.FavoriteMoviesEntry.CONTENT_URI;
                else
                    throw new RuntimeException("Loader Not Implemented for Preference: " + sort_option);
                return new CursorLoader(this,
                        moviesQueryUri,
                        null,
                        null,
                        null,
                        null);
            default:
                throw new RuntimeException("Loader Not Implemented: " + id);
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAdapter.swapCursor(data);
        //if (mPosition == RecyclerView.NO_POSITION) mPosition = 0;
        //mMoviesList.smoothScrollToPosition(mPosition);
        if (data.getCount() != 0) showRecyclerView();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(getString(R.string.pref_sort_key))) {
            sort_option = sharedPreferences.getString(key, getString(R.string.pref_sort_popular_value));
            startMoviesIntent();
            getSupportLoaderManager().restartLoader(MOVIES_LOADER_ID, null, MainActivity.this);
            mMoviesList.smoothScrollToPosition(0);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        Log.d(TAG, "Loader reset");
        mAdapter.swapCursor(null);
    }
}

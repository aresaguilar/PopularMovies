package com.example.android.popularmovies.view.activities;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.popularmovies.BuildConfig;
import com.example.android.popularmovies.R;
import com.example.android.popularmovies.data.MoviesContract.MoviesEntry;
import com.example.android.popularmovies.data.MoviesDbHelper;
import com.example.android.popularmovies.model.NetworkUtils;
import com.example.android.popularmovies.sync.FetchMoviesIntentService;
import com.example.android.popularmovies.sync.FetchMoviesTask;
import com.example.android.popularmovies.view.adapters.MovieAdapter;

import java.net.URL;

public class MainActivity extends AppCompatActivity
        implements MovieAdapter.ListItemClickListener,
            SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    /* Static IDs */
    private static final int POPULAR_MOVIES_LOADER_ID = 66;
    private static final int TOP_RATED_MOVIES_LOADER_ID = 77;
    private static final String MOVIES_QUERY_URL_EXTRA = "url";
    public static final String MOVIE_EXTRA = "movie";

    /* View related variables */
    private RecyclerView mMoviesList;
    private MovieAdapter mAdapter;
    private TextView mErrorTextView;
    private ProgressBar mLoadingProgressBar;
    private Toast mToast;

    /* Data */
    private SQLiteDatabase mDb;

    /* Preferences */
    private String sort_option;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /* Get references to layout elements */
        mMoviesList = (RecyclerView) findViewById(R.id.rv_movies);
        mErrorTextView = (TextView) findViewById(R.id.tv_error_message_display);
        mLoadingProgressBar = (ProgressBar) findViewById(R.id.pb_loading_indicator);

        /* Create a layout manager using spanCount = 2 */
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        mMoviesList.setLayoutManager(gridLayoutManager);
        mMoviesList.setHasFixedSize(true);

        /* Setup Shared Preferences */
        setupSharedPreferences();

        /* Get the database */
        MoviesDbHelper dbHelper = new MoviesDbHelper(this);
        mDb = dbHelper.getWritableDatabase();

        /* Load movies from database */
        Cursor cursor = getAllMovies();
        if (cursor.getCount() > 0) {
            createOrUpdateAdapter(cursor);
        } else {
            mLoadingProgressBar.setVisibility(View.VISIBLE);
        }

        makeMoviesQuery();
    }

    private void setupSharedPreferences() {
        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(this);

        sort_option = sharedPreferences.getString(getString(R.string.pref_sort_key),
                getString(R.string.pref_sort_popular_value));

        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
    }

    private Cursor getAllMovies() {
        if (sort_option.equals(getString(R.string.pref_sort_popular_value))) {
            return getAllPopularMovies();
        } else if (sort_option.equals(getString(R.string.pref_sort_top_value))) {
            return getAllTopRatedMovies();
        } else if (sort_option.equals(getString(R.string.pref_sort_favorites_value))) {
            return getAllFavoriteMovies();
        }
        return null;
    }

    private Cursor getAllFavoriteMovies() {
        return mDb.query(MoviesEntry.TABLE_NAME,
                null,
                MoviesEntry.COLUMN_NAME_FAVORITE + " = 1",
                null,
                null,
                null,
                null);
    }

    private Cursor getAllPopularMovies() {
        return mDb.query(MoviesEntry.TABLE_NAME,
                null,
                MoviesEntry.COLUMN_NAME_POPULAR + " = 1",
                null,
                null,
                null,
                null);
    }

    private Cursor getAllTopRatedMovies() {
        return mDb.query(MoviesEntry.TABLE_NAME,
                null,
                MoviesEntry.COLUMN_NAME_TOP_RATED + " = 1",
                null,
                null,
                null,
                null);
    }

    // TODO Cambiar moviesQuery por moviesIntent
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

    private void makeMoviesQuery() {
        URL mTopRatedMoviesUrl = null;
        URL mPopularMoviesUrl = null;

        mPopularMoviesUrl = NetworkUtils.buildUrl(NetworkUtils.POPULAR_EP, BuildConfig.THEMOVIEDB_API_KEY);
        mTopRatedMoviesUrl = NetworkUtils.buildUrl(NetworkUtils.TOPRATED_EP, BuildConfig.THEMOVIEDB_API_KEY);

        Bundle popularMoviesBundle = new Bundle();
        popularMoviesBundle.putString(MOVIES_QUERY_URL_EXTRA, String.valueOf(mPopularMoviesUrl));
        Bundle topRatedMoviesBundle = new Bundle();
        topRatedMoviesBundle.putString(MOVIES_QUERY_URL_EXTRA, String.valueOf(mTopRatedMoviesUrl));

        getSupportLoaderManager().restartLoader(POPULAR_MOVIES_LOADER_ID, popularMoviesBundle, this);
        getSupportLoaderManager().restartLoader(TOP_RATED_MOVIES_LOADER_ID, topRatedMoviesBundle, this);
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
        this.mErrorTextView.setVisibility(View.INVISIBLE);
        this.mMoviesList.setVisibility(View.VISIBLE);
    }

    private void showErrorTextView() {
        this.mErrorTextView.setVisibility(View.VISIBLE);
        this.mMoviesList.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onListItemClick(long id) {
        Intent intent = new Intent(MainActivity.this, MovieActivity.class);
        intent.putExtra(MOVIE_EXTRA, id);
        Log.d(TAG, "Launching activity with movie " + id);
        startActivity(intent);
    }

    @Override
    public void onListItemStar(long id) {
        ContentValues cv = new ContentValues();
        cv.put(MoviesEntry.COLUMN_NAME_FAVORITE, 1);
        mDb.update(MoviesEntry.TABLE_NAME, cv, MoviesEntry._ID + " = " + id, null);

        if (mToast != null) {
            mToast.cancel();
        }
        mToast = Toast.makeText(this,
                id +" "+ getString(R.string.added_to_favorites_action),
                Toast.LENGTH_LONG);
        mToast.show();
    }

    @Override
    public void onListItemUnstar(long id) {
        ContentValues cv = new ContentValues();
        cv.put(MoviesEntry.COLUMN_NAME_FAVORITE, 0);
        mDb.update(MoviesEntry.TABLE_NAME, cv, MoviesEntry._ID + " = " + id, null);

        if (mToast != null) {
            mToast.cancel();
        }
        mToast = Toast.makeText(this,
                id +" "+ getString(R.string.removed_from_favorites_action),
                Toast.LENGTH_LONG);
        mToast.show();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(getString(R.string.pref_sort_key))) {
            sort_option = sharedPreferences.getString(key, getString(R.string.pref_sort_popular_value));
            createOrUpdateAdapter(getAllMovies());
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PreferenceManager.getDefaultSharedPreferences(this)
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    public void createOrUpdateAdapter(Cursor cursor) {
        if (mAdapter == null) {
            mAdapter = new MovieAdapter(cursor, MainActivity.this);
            mMoviesList.setAdapter(mAdapter);
        } else {
            mAdapter.changeCursor(cursor);
        }
        showRecyclerView();
    }

    @Override
    public void onLoaderReset(Loader<String> loader) {

    }
}

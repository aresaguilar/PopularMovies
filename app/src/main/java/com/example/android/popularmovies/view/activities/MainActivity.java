package com.example.android.popularmovies.view.activities;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
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
import com.example.android.popularmovies.model.Movie;
import com.example.android.popularmovies.model.MovieUtils;
import com.example.android.popularmovies.model.NetworkUtils;
import com.example.android.popularmovies.view.adapters.MovieAdapter;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
        implements MovieAdapter.ListItemClickListener,
            SharedPreferences.OnSharedPreferenceChangeListener,
            LoaderManager.LoaderCallbacks<String> {

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

    private long addNewMovie(Movie movie, boolean popular, boolean topRated) {
        ContentValues cv = new ContentValues();
        cv.put(MoviesEntry.COLUMN_NAME_ID, movie.getId());
        cv.put(MoviesEntry.COLUMN_NAME_TITLE, movie.getTitle());
        cv.put(MoviesEntry.COLUMN_NAME_POSTER, movie.getPoster_path());
        cv.put(MoviesEntry.COLUMN_NAME_DATE, movie.getRelease_date());
        cv.put(MoviesEntry.COLUMN_NAME_RATING, movie.getVote_average());
        cv.put(MoviesEntry.COLUMN_NAME_OVERVIEW, movie.getOverview());
        if (popular)
            cv.put(MoviesEntry.COLUMN_NAME_POPULAR, 1);
        if (topRated)
            cv.put(MoviesEntry.COLUMN_NAME_TOP_RATED, 1);

        return mDb.insert(MoviesEntry.TABLE_NAME, null, cv);
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

    @Override
    public Loader<String> onCreateLoader(int id, final Bundle args) {
        return new AsyncTaskLoader<String>(this) {

            private String mMoviesJson;

            @Override
            protected void onStartLoading() {
                super.onStartLoading();
                if (args == null) {
                    return;
                }
                mLoadingProgressBar.setVisibility(View.VISIBLE);

                if (mMoviesJson != null) {
                    deliverResult(mMoviesJson);
                } else {
                    forceLoad();
                }
            }

            @Override
            public String loadInBackground() {
                String moviesQueryUrlString = args.getString(MOVIES_QUERY_URL_EXTRA);
                if (moviesQueryUrlString == null || TextUtils.isEmpty(moviesQueryUrlString)) {
                    return null;
                }
                try {
                    URL moviesUrl = new URL(moviesQueryUrlString);
                    Log.e(TAG, "Generated URL: " + moviesUrl);
                    return NetworkUtils.getResponseFromHttpUrl(moviesUrl);
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e(TAG, "Error getting response");
                    return null;
                }
            }

            @Override
            public void deliverResult(String data) {
                mLoadingProgressBar.setVisibility(View.INVISIBLE);
                mMoviesJson = data;
                super.deliverResult(data);
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<String> loader, String data) {
        if (data == null) {
            showErrorTextView();
            return;
        }

        ArrayList<Movie> mMoviesArray = new ArrayList<>();
        MovieUtils.parseJSON(data, mMoviesArray);

        if (loader.getId() == POPULAR_MOVIES_LOADER_ID) {
            deleteAllMovies(true, false, false);
            for (Movie movie : mMoviesArray)
                addNewMovie(movie, true, false);

            if (sort_option.equals(getString(R.string.pref_sort_popular_value))) {
                mLoadingProgressBar.setVisibility(View.INVISIBLE);
                createOrUpdateAdapter(getAllPopularMovies());
            }

        } else if (loader.getId() == TOP_RATED_MOVIES_LOADER_ID) {
            deleteAllMovies(false, true, false);
            for (Movie movie : mMoviesArray)
                addNewMovie(movie, false, true);

            if (sort_option.equals(getString(R.string.pref_sort_top_value))) {
                mLoadingProgressBar.setVisibility(View.INVISIBLE);
                createOrUpdateAdapter(getAllTopRatedMovies());
            }
        }
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

    public long deleteAllMovies(boolean popular, boolean top_rated, boolean favorites) {
        String whPopular = MoviesEntry.COLUMN_NAME_POPULAR + (popular ? " = 1" : " = 0");
        String whTopRated = MoviesEntry.COLUMN_NAME_TOP_RATED + (top_rated ? " = 1" : " = 0");
        String whFavorites = MoviesEntry.COLUMN_NAME_FAVORITE + (favorites ? " = 1" : " = 0");

        return mDb.delete(MoviesEntry.TABLE_NAME, whPopular + " AND " + whTopRated + " AND " + whFavorites, null);
    }

    @Override
    public void onLoaderReset(Loader<String> loader) {

    }
}

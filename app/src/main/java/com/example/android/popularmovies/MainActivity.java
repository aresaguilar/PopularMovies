package com.example.android.popularmovies;

import android.content.Intent;
import android.content.SharedPreferences;
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

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
        implements MovieAdapter.ListItemClickListener,
            SharedPreferences.OnSharedPreferenceChangeListener,
            LoaderManager.LoaderCallbacks<String> {

    private static final String TAG = MainActivity.class.getSimpleName();

    private static final int MOVIES_LOADER_ID = 6;
    private static final String MOVIES_QUERY_URL_EXTRA = "url";
    public static final String MOVIE_EXTRA = "movie";

    private RecyclerView mMoviesList;
    private MovieAdapter mAdapter;
    private TextView mErrorTextView;
    private ProgressBar mLoadingProgressBar;

    private ArrayList<Movie> mMoviesArray;

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

        makeMoviesQuery();
    }

    private void setupSharedPreferences() {
        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(this);

        sort_option = sharedPreferences.getString(getString(R.string.pref_sort_key),
                getString(R.string.pref_sort_popular_value));

        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
    }

    private void makeMoviesQuery() {
        URL mMoviesUrl = null;

        if (sort_option.equals(getString(R.string.pref_sort_popular_value))) {
            mMoviesUrl = NetworkUtils.buildUrl(NetworkUtils.POPULAR_EP, BuildConfig.THEMOVIEDB_API_KEY);
        } else if (sort_option.equals(getString(R.string.pref_sort_top_value))) {
            mMoviesUrl = NetworkUtils.buildUrl(NetworkUtils.TOPRATED_EP, BuildConfig.THEMOVIEDB_API_KEY);
        }

        Bundle bundle = new Bundle();
        bundle.putString(MOVIES_QUERY_URL_EXTRA, String.valueOf(mMoviesUrl));

        getSupportLoaderManager().restartLoader(MOVIES_LOADER_ID, bundle, this);
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
    public void onListItemClick(Movie movieClicked) {
        Intent intent = new Intent(MainActivity.this, MovieActivity.class);
        intent.putExtra(MOVIE_EXTRA, movieClicked);
        Log.d(TAG, "Launching activity with movie " + movieClicked.getTitle());
        startActivity(intent);
    }

    @Override
    public void onListItemStar(Movie movieClicked) {
        // TODO Add to favorites
        Toast.makeText(this,
                movieClicked.getTitle() + getString(R.string.added_to_favorites_action),
                Toast.LENGTH_LONG).show();
    }

    @Override
    public void onListItemUnstar(Movie movieClicked) {
        // TODO Remove from favorites
        Toast.makeText(this,
                movieClicked.getTitle() + getString(R.string.removed_from_favorites_action),
                Toast.LENGTH_LONG).show();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(getString(R.string.pref_sort_key))) {
            sort_option = sharedPreferences.getString(key, getString(R.string.pref_sort_popular_value));
            makeMoviesQuery();
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
        mLoadingProgressBar.setVisibility(View.INVISIBLE);
        if (data != null) {
            mMoviesArray = new ArrayList<>();
            MovieUtils.parseJSON(data, mMoviesArray);
            mAdapter = new MovieAdapter(mMoviesArray, MainActivity.this);
            mMoviesList.setAdapter(mAdapter);
            showRecyclerView();
            Log.d(TAG, "Updated data and adapter");
        } else {
            showErrorTextView();
            Log.d(TAG, "Error fetching content");
        }
    }

    @Override
    public void onLoaderReset(Loader<String> loader) {

    }
}

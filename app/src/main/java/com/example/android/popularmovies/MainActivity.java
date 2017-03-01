package com.example.android.popularmovies;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
        implements MovieAdapter.ListItemClickListener,
            SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String TAG = MainActivity.class.getSimpleName();

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

        new FetchMoviesTask(this).execute();
    }

    private void setupSharedPreferences() {
        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(this);

        sort_option = sharedPreferences.getString(getString(R.string.pref_sort_key),
                getString(R.string.pref_sort_popular_value));

        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
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
        intent.putExtra("movie", movieClicked);
        Log.d(TAG, "Launching activity with movie " + movieClicked.getTitle());
        startActivity(intent);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(getString(R.string.pref_sort_key))) {
            sort_option = sharedPreferences.getString(key, getString(R.string.pref_sort_popular_value));
            new FetchMoviesTask(this).execute();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PreferenceManager.getDefaultSharedPreferences(this)
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    public class FetchMoviesTask extends AsyncTask<Void, Void, Void> {

        private final MovieAdapter.ListItemClickListener listener;

        public FetchMoviesTask(MovieAdapter.ListItemClickListener listener) {
            this.listener = listener;
        }

        @Override
        protected Void doInBackground(Void... params) {

            URL mMoviesUrl;

            if (sort_option.equals(getString(R.string.pref_sort_popular_value))) {
                mMoviesUrl = NetworkUtils.buildUrl(NetworkUtils.POPULAR_EP, BuildConfig.THEMOVIEDB_API_KEY);
            } else {//if (sort_option.equals(getString(R.string.pref_sort_top_value))) {
                mMoviesUrl = NetworkUtils.buildUrl(NetworkUtils.TOPRATED_EP, BuildConfig.THEMOVIEDB_API_KEY);
            }

            try {
                String mMoviesQuery = NetworkUtils.getResponseFromHttpUrl(mMoviesUrl);

                mMoviesArray = new ArrayList<>();

                MovieUtils.parseJSON(mMoviesQuery, mMoviesArray);

            } catch (IOException e) {
                e.printStackTrace();
                Log.e(TAG, "Error getting response");
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mLoadingProgressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(Void  s) {
            super.onPostExecute(s);

            mLoadingProgressBar.setVisibility(View.INVISIBLE);

            if (mMoviesArray != null) {
                loadMovieAdapter(mMoviesArray);
                showRecyclerView();
                Log.d(TAG, "Updated data and adapter");
            } else {
                showErrorTextView();
                Log.d(TAG, "Error fetching content");
            }
        }

        protected void loadMovieAdapter(ArrayList<Movie> array) {
            mAdapter = new MovieAdapter(array, listener);
            mMoviesList.setAdapter(mAdapter);
        }
    }
}

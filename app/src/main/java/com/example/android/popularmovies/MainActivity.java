package com.example.android.popularmovies;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
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

public class MainActivity extends AppCompatActivity implements MovieAdapter.ListItemClickListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    private RecyclerView mMoviesList;
    private MovieAdapter mAdapter;
    private TextView mErrorTextView;
    private ProgressBar mLoadingProgressBar;

    private ArrayList<Movie> mTopRatedList;
    private ArrayList<Movie> mPopularList;

    private boolean SORT_POPULAR = true;

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

        new FetchMoviesTask(this).execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);

        if (!SORT_POPULAR) {
            menu.findItem(R.id.maction_sort_popular).setVisible(true);
            menu.findItem(R.id.maction_sort_top_rated).setVisible(false);
        } else {
            menu.findItem(R.id.maction_sort_popular).setVisible(false);
            menu.findItem(R.id.maction_sort_top_rated).setVisible(true);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemThatWasClickedId = item.getItemId();
        if (itemThatWasClickedId == R.id.maction_sort_popular) {
            SORT_POPULAR = true;
            invalidateOptionsMenu();
            if (mPopularList != null && mTopRatedList != null) {
                if (mAdapter != null) {
                    mAdapter.changeData(mPopularList);
                }
            }
            return true;
        } else if (itemThatWasClickedId == R.id.maction_sort_top_rated) {
            SORT_POPULAR = false;
            invalidateOptionsMenu();
            if (mPopularList != null && mTopRatedList != null) {
                if (mAdapter != null) {
                    mAdapter.changeData(mTopRatedList);
                }
            }
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


    public class FetchMoviesTask extends AsyncTask<Void, Void, Void> {

        private final MovieAdapter.ListItemClickListener listener;

        public FetchMoviesTask(MovieAdapter.ListItemClickListener listener) {
            this.listener = listener;
        }

        @Override
        protected Void doInBackground(Void... params) {

            URL mTopRatedUrl = NetworkUtils.buildUrl(NetworkUtils.TOPRATED_EP, BuildConfig.THEMOVIEDB_API_KEY);
            URL mPopularUrl = NetworkUtils.buildUrl(NetworkUtils.POPULAR_EP, BuildConfig.THEMOVIEDB_API_KEY);

            try {
                String mTopRatedQuery = NetworkUtils.getResponseFromHttpUrl(mTopRatedUrl);
                String mPopularQuery = NetworkUtils.getResponseFromHttpUrl(mPopularUrl);

                mTopRatedList = new ArrayList<>();
                mPopularList = new ArrayList<>();

                MovieUtils.parseJSON(mTopRatedQuery, mTopRatedList);
                MovieUtils.parseJSON(mPopularQuery, mPopularList);

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

            if (mPopularList != null && mTopRatedList != null) {
                if (SORT_POPULAR)
                    loadMovieAdapter(mPopularList);
                else
                    loadMovieAdapter(mTopRatedList);
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

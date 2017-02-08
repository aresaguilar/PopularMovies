package com.example.android.popularmovies;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private RecyclerView mMoviesList;
    private MovieAdapter mAdapter;
    private TextView mErrorTextView;
    private ProgressBar mLoadingProgressBar;

    private ArrayList<Movie> mTopRatedList;
    private ArrayList<Movie> mPopularList;

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

        new FetchMoviesTask().execute();
        //mAdapter = new MovieAdapter(new ArrayList<Movie>());
        //mMoviesList.setAdapter(mAdapter);
    }

    private void showRecyclerView() {
        this.mErrorTextView.setVisibility(View.INVISIBLE);
        this.mMoviesList.setVisibility(View.VISIBLE);
    }

    private void showErrorTextView() {
        this.mErrorTextView.setVisibility(View.VISIBLE);
        this.mMoviesList.setVisibility(View.INVISIBLE);
    }


    public class FetchMoviesTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {

            URL mTopRatedUrl = NetworkUtils.buildUrl(NetworkUtils.TOPRATED_EP, getString(R.string.themoviedb_api_key));
            URL mPopularUrl = NetworkUtils.buildUrl(NetworkUtils.POPULAR_EP, getString(R.string.themoviedb_api_key));

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
            // TODO Usar preferencias

            if (mPopularList != null && mTopRatedList != null) {
                loadMovieAdapter(mPopularList);
                showRecyclerView();
                Log.d(TAG, "Updated data and adapter");
            } else {
                showErrorTextView();
                Log.d(TAG, "Error fetching content");
            }
        }

        protected void loadMovieAdapter(ArrayList<Movie> array) {
            mAdapter = new MovieAdapter(array);
            mMoviesList.setAdapter(mAdapter);
        }
    }
}

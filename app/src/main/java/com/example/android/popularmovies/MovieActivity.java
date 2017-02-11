package com.example.android.popularmovies;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;


public class MovieActivity extends AppCompatActivity {

    private static final String TAG = MovieActivity.class.getSimpleName();

    TextView mTitleTextView;
    TextView mDateTextView;
    TextView mRatingTextView;
    TextView mOverviewTextView;
    ImageView mPosterImageView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie);

        /* Get references to layout elements */
        mTitleTextView = (TextView) findViewById(R.id.tv_title);
        mDateTextView = (TextView) findViewById(R.id.tv_date);
        mRatingTextView = (TextView) findViewById(R.id.tv_rating);
        mOverviewTextView = (TextView) findViewById(R.id.tv_overview);
        mPosterImageView = (ImageView) findViewById(R.id.iv_poster);

        Movie mMovie = getIntent().getParcelableExtra("movie");

        Log.d(TAG, "Launched with movie " + mMovie.getTitle());

        if (mMovie != null) {
            mTitleTextView.setText(mMovie.getTitle());
            mDateTextView.setText("Release date\n" + mMovie.getRelease_date() + "\n\n");
            mRatingTextView.setText("Vote average\n" + mMovie.getVote_average() + "\n\n");
            mOverviewTextView.setText(mMovie.getOverview());
            Picasso.with(this)
                    .load(mMovie.getPosterURL(Movie.POSTER_SIZE_MOBILE))
                    .into(mPosterImageView);
            Log.d(TAG, "Loaded film " + mMovie.getTitle());
        } else {
            Log.w(TAG, "Error. No movie in intent");
        }
    }
}

package com.example.android.popularmovies;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.util.Map;
import java.util.WeakHashMap;


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
        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        /* Get references to layout elements */
        mTitleTextView = (TextView) findViewById(R.id.tv_title);
        mDateTextView = (TextView) findViewById(R.id.tv_date);
        mRatingTextView = (TextView) findViewById(R.id.tv_rating);
        mOverviewTextView = (TextView) findViewById(R.id.tv_overview);
        mPosterImageView = (ImageView) findViewById(R.id.iv_poster);

        Movie mMovie = getIntent().getParcelableExtra(MainActivity.MOVIE_EXTRA);

        Log.d(TAG, "Launched with movie " + mMovie.getTitle());

        if (mMovie != null) {
            mTitleTextView.setText(mMovie.getTitle());
            mDateTextView.setText("Release date\n" + mMovie.getRelease_date() + "\n\n");
            mRatingTextView.setText("Vote average\n" + mMovie.getVote_average() + "\n\n");
            mOverviewTextView.setText(mMovie.getOverview());

            int posterWidth = (int) getResources().getDimension(R.dimen.movieactivity_poster_width);
            int posterHeight = (int) getResources().getDimension(R.dimen.movieactivity_poster_height);
            final int colorAccent = getResources().getColor(R.color.colorAccent);
            Picasso.with(this)
                    .load(mMovie.getPosterURL(Movie.POSTER_SIZE_MOBILE))
                    .resize(posterWidth, posterHeight)
                    .centerCrop()
                    .transform(PaletteTransformation.instance())
                    .into(mPosterImageView, new Callback.EmptyCallback() {
                        @Override public void onSuccess() {
                            Bitmap bitmap = ((BitmapDrawable) mPosterImageView.getDrawable()).getBitmap(); // Ew!
                            Palette palette = PaletteTransformation.getPalette(bitmap);
                            Palette.Swatch textSwatch = palette.getVibrantSwatch();
                            if (textSwatch == null) {
                                Log.d(TAG, "Null vibrant swatch");
                                textSwatch = palette.getDominantSwatch();
                                if (textSwatch == null) {
                                    Log.d(TAG, "Null dominant swatch");
                                    mTitleTextView.setBackgroundColor(colorAccent);
                                    return;
                                }
                            }
                            mTitleTextView.setBackgroundColor(textSwatch.getRgb());
                            mTitleTextView.setTextColor(textSwatch.getTitleTextColor());
                        }
                    });
            Log.d(TAG, "Loaded film " + mMovie.getTitle());
        } else {
            Log.w(TAG, "Error. No movie in intent");
        }
    }

    public static final class PaletteTransformation implements Transformation {
        private static final PaletteTransformation INSTANCE = new PaletteTransformation();
        private static final Map<Bitmap, Palette> CACHE = new WeakHashMap<>();

        public static PaletteTransformation instance() {
            return INSTANCE;
        }

        public static Palette getPalette(Bitmap bitmap) {
            return CACHE.get(bitmap);
        }

        private PaletteTransformation() {}

        @Override public Bitmap transform(Bitmap source) {
            Palette palette = Palette.generate(source);
            CACHE.put(source, palette);
            return source;
        }

        @Override public String key() {
            return ""; // Stable key for all requests. An unfortunate requirement.
        }
    }
}

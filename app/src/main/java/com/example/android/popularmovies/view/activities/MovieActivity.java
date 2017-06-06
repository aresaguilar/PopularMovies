package com.example.android.popularmovies.view.activities;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.popularmovies.R;
import com.example.android.popularmovies.data.MoviesContract;
import com.example.android.popularmovies.model.MovieUtils;
import com.example.android.popularmovies.sync.FetchMovieDetailsIntentService;
import com.example.android.popularmovies.view.adapters.ReviewAdapter;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.util.Map;
import java.util.WeakHashMap;


public class MovieActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor>, View.OnClickListener {

    private static final String TAG = MovieActivity.class.getSimpleName();

    private static final int MOVIE_DETAILS_LOADER_ID = 77;
    private static final int MOVIE_FAVORITES_LOADER_ID = 78;
    private static final int MOVIE_REVIEWS_LOADER_ID = 88;
    private static final int MOVIE_VIDEOS_LOADER_ID = 99;

    private String movieId;
    private boolean isFavorite;

    TextView mTitleTextView;
    TextView mDateTextView;
    TextView mRatingTextView;
    TextView mOverviewTextView;
    ImageView mPosterImageView;
    RecyclerView mReviewsRecyclerView;
    Button mFavoriteButton;
    Button mTrailerButton;

    ReviewAdapter mReviewsAdapter;

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
        mReviewsRecyclerView = (RecyclerView) findViewById(R.id.rv_reviews);
        mFavoriteButton = (Button) findViewById(R.id.btn_favorite);
        mTrailerButton = (Button) findViewById(R.id.btn_trailer);

        /* Set up click listeners */
        mFavoriteButton.setOnClickListener(this);
        mTrailerButton.setOnClickListener(this);

        /* Set up RecyclerViews */
        mReviewsRecyclerView.setClickable(false);
        /* Set up adapters */
        LinearLayoutManager reviewsLayoutManager = new LinearLayoutManager(this);
        mReviewsRecyclerView.setLayoutManager(reviewsLayoutManager);
        mReviewsAdapter = new ReviewAdapter(this);
        /* Add adapters to ListViews */
        mReviewsRecyclerView.setAdapter(mReviewsAdapter);

        String movieId = getIntent().getStringExtra(MainActivity.MOVIE_EXTRA);

        Log.d(TAG, "Launched with movie " + movieId);

        if (movieId != null) {
            this.movieId = movieId;

            startMovieDetailsIntent(movieId);

            getSupportLoaderManager().initLoader(MOVIE_DETAILS_LOADER_ID, null, this);
            getSupportLoaderManager().initLoader(MOVIE_FAVORITES_LOADER_ID, null, this);
            getSupportLoaderManager().initLoader(MOVIE_REVIEWS_LOADER_ID, null, this);
            getSupportLoaderManager().initLoader(MOVIE_VIDEOS_LOADER_ID, null, this);

        } else {
            Log.w(TAG, "Error. No movie in intent");
        }
    }

    private void startMovieDetailsIntent(String movieId) {
        Intent fetchMovieDetailsIntent = new Intent(this, FetchMovieDetailsIntentService.class);
        fetchMovieDetailsIntent.setAction(movieId);
        startService(fetchMovieDetailsIntent);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri movieDetailsUri;
        switch (id) {
            case MOVIE_DETAILS_LOADER_ID:
                movieDetailsUri =
                        MoviesContract.MoviesEntry.CONTENT_URI.buildUpon().appendPath(movieId).build();
                break;
            case MOVIE_FAVORITES_LOADER_ID:
                movieDetailsUri =
                        MoviesContract.FavoriteMoviesEntry.CONTENT_URI.buildUpon().appendPath(movieId).build();
                break;
            case MOVIE_REVIEWS_LOADER_ID:
                movieDetailsUri =
                        MoviesContract.MovieReviewsEntry.CONTENT_URI.buildUpon().appendPath(movieId).build();
                break;
            case MOVIE_VIDEOS_LOADER_ID:
                movieDetailsUri =
                        MoviesContract.MovieVideosEntry.CONTENT_URI.buildUpon().appendPath(movieId).build();
                break;
            default:
                throw new RuntimeException("Loader Not Implemented: " + id);
        }
        return new CursorLoader(this, movieDetailsUri, null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data.getCount() < 1) {
            switch (loader.getId()) {
                case MOVIE_REVIEWS_LOADER_ID:
                    return;
                case MOVIE_FAVORITES_LOADER_ID:
                    isFavorite = false;
                    mFavoriteButton.setText(getString(R.string.btn_favorite_add));
                    mFavoriteButton.setVisibility(View.VISIBLE);
                    return;
                case MOVIE_VIDEOS_LOADER_ID:
                    return;
                default:
                    return;
            }
        }

        switch (loader.getId()) {
            case MOVIE_DETAILS_LOADER_ID:
                data.moveToFirst();
                mTitleTextView.setText(data.getString(
                        data.getColumnIndex(MoviesContract.MoviesEntry.COLUMN_NAME_TITLE)));
                mDateTextView.setText("Release date\n" + data.getString(
                        data.getColumnIndex(MoviesContract.MoviesEntry.COLUMN_NAME_DATE)) + "\n\n");
                mRatingTextView.setText("Vote average\n" + data.getString(
                        data.getColumnIndex(MoviesContract.MoviesEntry.COLUMN_NAME_RATING)) + "/10\n\n");
                mOverviewTextView.setText(data.getString(
                        data.getColumnIndex(MoviesContract.MoviesEntry.COLUMN_NAME_OVERVIEW)));
                int posterWidth = (int) getResources().getDimension(R.dimen.movieactivity_poster_width);
                int posterHeight = (int) getResources().getDimension(R.dimen.movieactivity_poster_height);
                final int colorAccent = getResources().getColor(R.color.colorAccent);
                Picasso.with(this)
                        .load(MovieUtils.getPosterUrl(data.getString(data.getColumnIndex(MoviesContract.MoviesEntry.COLUMN_NAME_POSTER)),
                                MovieUtils.POSTER_SIZE_MOBILE))
                        .resize(posterWidth, posterHeight)
                        .centerCrop()
                        .transform(PaletteTransformation.instance())
                        .into(mPosterImageView, new Callback.EmptyCallback() {
                            @Override public void onSuccess() {
                                Bitmap bitmap = ((BitmapDrawable) mPosterImageView.getDrawable()).getBitmap();
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
                break;
            case MOVIE_FAVORITES_LOADER_ID:
                isFavorite = true;
                mFavoriteButton.setText(getString(R.string.btn_favorite_remove));
                mFavoriteButton.setVisibility(View.VISIBLE);
                break;
            case MOVIE_REVIEWS_LOADER_ID:
                mReviewsAdapter.swapCursor(data);
                break;
            case MOVIE_VIDEOS_LOADER_ID:
                String key = MovieUtils.getBestTrailerYouTubeKey(data);
                if (null != key) {
                    mTrailerButton.setTag(key);
                    mTrailerButton.setVisibility(View.VISIBLE);
                    Log.d(TAG, "Youtube key: " + key);
                }
                break;
            default:
                throw new RuntimeException("Loader Not Implemented: " + loader.getId());
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        switch (loader.getId()) {
            case MOVIE_DETAILS_LOADER_ID:
            case MOVIE_FAVORITES_LOADER_ID:
                break;
            case MOVIE_REVIEWS_LOADER_ID:
                mReviewsAdapter.swapCursor(null);
                break;
            case MOVIE_VIDEOS_LOADER_ID:
                mTrailerButton.setTag(null);
                mTrailerButton.setVisibility(View.INVISIBLE);
                break;
            default:
                throw new RuntimeException("Loader Not Implemented: " + loader.getId());
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_favorite:
                if (isFavorite) {
                    Uri uri = MoviesContract.FavoriteMoviesEntry.CONTENT_URI.buildUpon().appendPath(movieId).build();
                    if (getContentResolver().delete(uri, null, null) > 0) {
                        isFavorite = false;
                        mFavoriteButton.setText(getString(R.string.btn_favorite_add));
                    }

                } else {
                    Uri uri = MoviesContract.FavoriteMoviesEntry.CONTENT_URI.buildUpon().appendPath(movieId).build();
                    if (getContentResolver().insert(uri, null) != null) {
                        isFavorite = true;
                        mFavoriteButton.setText(getString(R.string.btn_favorite_remove));
                    }
                }
                break;
            case R.id.btn_trailer:
                String key = mTrailerButton.getTag().toString();
                Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + key));
                Intent webIntent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse("http://www.youtube.com/watch?v=" + key));
                try {
                    startActivity(appIntent);
                } catch (ActivityNotFoundException ex) {
                    startActivity(webIntent);
                }
                break;
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

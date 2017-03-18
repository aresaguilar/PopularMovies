package com.example.android.popularmovies.view.activities;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.android.popularmovies.R;
import com.example.android.popularmovies.data.MoviesContract;
import com.example.android.popularmovies.model.MovieUtils;
import com.example.android.popularmovies.sync.FetchMovieDetailsIntentService;
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
    ListView mReviewsListView;
    ListView mVideosListView;
    Button mFavoriteButton;

    SimpleCursorAdapter mReviewsAdapter;
    SimpleCursorAdapter mVideosAdapter;
    ProgressBar progressBarReviews;
    ProgressBar progressBarVideos;

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
        mReviewsListView = (ListView) findViewById(R.id.lv_reviews);
        mVideosListView = (ListView) findViewById(R.id.lv_videos);
        mFavoriteButton = (Button) findViewById(R.id.btn_favorite);

        mFavoriteButton.setOnClickListener(this);

        /* Set up ListViews */
        mReviewsListView.setClickable(false);
        /* Set up progressBars */
        progressBarReviews = new ProgressBar(this);
        progressBarReviews.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT, Gravity.CENTER));
        progressBarReviews.setIndeterminate(true);
        mReviewsListView.setEmptyView(progressBarReviews);
        ViewGroup root = (ViewGroup) findViewById(R.id.ll_movie_details);
        root.addView(progressBarReviews);
        progressBarVideos = new ProgressBar(this);
        progressBarVideos.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT, Gravity.CENTER));
        progressBarVideos.setIndeterminate(true);
        mVideosListView.setEmptyView(progressBarVideos);
        root.addView(progressBarVideos);
        /* Set up adapters */
        String[] fromColumnsReview = {MoviesContract.MovieReviewsEntry.COLUMN_NAME_AUTHOR,
                MoviesContract.MovieReviewsEntry.COLUMN_NAME_CONTENT};
        int[] toViewsReview = {R.id.tv_item_author, R.id.tv_item_content};
        String[] fromColumnsVideo = {MoviesContract.MovieVideosEntry.COLUMN_NAME_TYPE};
        int[] toViewsVideo ={R.id.tv_item_video};
        mReviewsAdapter = new SimpleCursorAdapter(this,
                R.layout.review_list_item,
                null,
                fromColumnsReview,
                toViewsReview,
                0);
        mVideosAdapter = new SimpleCursorAdapter(this,
                R.layout.video_list_item,
                null,
                fromColumnsVideo,
                toViewsVideo,
                0);
        /* Add adapters to ListViews */
        mReviewsListView.setAdapter(mReviewsAdapter);
        mVideosListView.setAdapter(mVideosAdapter);

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
                    progressBarReviews.setVisibility(View.GONE);
                    return;
                case MOVIE_FAVORITES_LOADER_ID:
                    isFavorite = false;
                    mFavoriteButton.setText(getString(R.string.btn_favorite_add));
                    mFavoriteButton.setVisibility(View.VISIBLE);
                    return;
                case MOVIE_VIDEOS_LOADER_ID:
                    progressBarVideos.setVisibility(View.GONE);
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
                mVideosAdapter.swapCursor(data);
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
                mVideosAdapter.swapCursor(null);
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

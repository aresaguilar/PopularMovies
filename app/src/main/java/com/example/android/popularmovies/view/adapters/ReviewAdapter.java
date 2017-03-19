package com.example.android.popularmovies.view.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.popularmovies.R;
import com.example.android.popularmovies.data.MoviesContract;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder> {

    private static final String TAG = ReviewAdapter.class.getSimpleName();

    private Context mContext;
    private Cursor mCursor;

    public ReviewAdapter (Context context) {
        this.mContext = context;
    }

    @Override
    public ReviewViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from(mContext)
                .inflate(R.layout.review_list_item, parent, false);

        return new ReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ReviewViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        if (null == mCursor)
            return 0;
        return mCursor.getCount();
    }

    public void swapCursor(Cursor newCursor) {
        mCursor = newCursor;
        notifyDataSetChanged();
    }


    class ReviewViewHolder extends RecyclerView.ViewHolder {

        TextView mAuthorTextView;
        TextView mContentTextView;

        public ReviewViewHolder(View itemView) {
            super(itemView);

            mAuthorTextView = (TextView) itemView.findViewById(R.id.tv_item_author);
            mContentTextView = (TextView) itemView.findViewById(R.id.tv_item_content);
        }

        void bind (int position) {
            if (!mCursor.moveToPosition(position)) {
                Log.e(TAG, "Error moving cursor to position " + position);
                return;
            }
            Log.d(TAG, "Bound review to position " + position);

            String author =
                    mCursor.getString(mCursor.getColumnIndex(MoviesContract.MovieReviewsEntry.COLUMN_NAME_AUTHOR));
            String content =
                    mCursor.getString(mCursor.getColumnIndex(MoviesContract.MovieReviewsEntry.COLUMN_NAME_CONTENT));

            mAuthorTextView.setText(author);
            mContentTextView.setText(content);
        }
    }
}

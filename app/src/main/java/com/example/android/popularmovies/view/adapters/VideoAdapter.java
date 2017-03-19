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

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.VideoViewHolder> {

    private static final String TAG = MovieAdapter.class.getSimpleName();

    private Context mContext;
    private Cursor mCursor;

    public VideoAdapter (Context context) {
        this.mContext = context;
    }

    @Override
    public VideoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from(mContext)
                .inflate(R.layout.video_list_item, parent, false);

        return new VideoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(VideoViewHolder holder, int position) {
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

    class VideoViewHolder extends RecyclerView.ViewHolder {

        TextView mTitleTextView;
        String mSite;
        String mKey;

        public VideoViewHolder (View itemView) {
            super(itemView);

            mTitleTextView = (TextView) itemView.findViewById(R.id.tv_item_video);
        }

        void bind (int position) {
            if (!mCursor.moveToPosition(position)) {
                Log.e(TAG, "Error moving cursor to position " + position);
                return;
            }

            mSite = mCursor.getString(mCursor.getColumnIndex(MoviesContract.MovieVideosEntry.COLUMN_NAME_SITE));
            mKey = mCursor.getString(mCursor.getColumnIndex(MoviesContract.MovieVideosEntry.COLUMN_NAME_KEY));
            String type =
                    mCursor.getString(mCursor.getColumnIndex(MoviesContract.MovieVideosEntry.COLUMN_NAME_TYPE));
            mTitleTextView.setText(type);
        }
    }
}

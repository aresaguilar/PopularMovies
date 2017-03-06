package com.example.android.popularmovies;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.android.popularmovies.MoviesContract.MoviesEntry;
import com.github.ivbaranov.mfb.MaterialFavoriteButton;
import com.squareup.picasso.Picasso;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {

    private static final String TAG = MovieAdapter.class.getSimpleName();

    private Cursor mCursor;
    private ListItemClickListener mListener;


    public MovieAdapter (Cursor cursor, ListItemClickListener listener) {
        this.mCursor = cursor;
        this.mListener = listener;
    }

    public interface ListItemClickListener {
        void onListItemClick(long id);
        void onListItemStar(long id);
        void onListItemUnstar(long id);
    }

    @Override
    public MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutIdForListItem = R.layout.film_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(layoutIdForListItem, parent, false);
        MovieViewHolder viewHolder = new MovieViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MovieViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return mCursor.getCount();
    }

    public void changeCursor(Cursor cursor) {
        mCursor.close();
        mCursor = cursor;
        notifyDataSetChanged();
    }

    class MovieViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        View itemView;
        ImageView listItemImageView;
        MaterialFavoriteButton listItemFavoriteButton;

        public MovieViewHolder(View itemView) {
            super(itemView);

            this.itemView = itemView;

            listItemImageView = (ImageView) itemView.findViewById(R.id.iv_item_poster);
            listItemImageView.setOnClickListener(this);

            listItemFavoriteButton = (MaterialFavoriteButton) itemView.findViewById(R.id.btn_item_star);
            listItemFavoriteButton.setOnFavoriteChangeListener(
                    new MaterialFavoriteButton.OnFavoriteChangeListener() {
                        @Override
                        public void onFavoriteChanged(MaterialFavoriteButton buttonView, boolean favorite) {
                            long movieId = (long) buttonView.getTag();
                            if (favorite) {
                                mListener.onListItemStar(movieId);
                            } else {
                                mListener.onListItemUnstar(movieId);
                            }
                        }
                    });
        }

        void bind(int position) {
            /* Move cursor to my position */
            if (!mCursor.move(position))
                return;

            /* Get needed info from database */
            String poster_path = mCursor.getString(mCursor.getColumnIndex(MoviesEntry.COLUMN_NAME_POSTER));
            long movieId = mCursor.getLong(mCursor.getColumnIndex(MoviesEntry._ID));

            /* Update view */
            itemView.setTag(movieId);
            listItemFavoriteButton.setTag(movieId);

            Picasso.with(listItemImageView.getContext())
                    .load(MovieUtils.getPosterUrl(poster_path, MovieUtils.POSTER_SIZE_MOBILE))
                    .into(listItemImageView);
        }

        @Override
        public void onClick(View v) {
            long movieId = (long) itemView.getTag();

            switch (v.getId()) {
                case R.id.iv_item_poster:
                    mListener.onListItemClick(movieId);
                    break;
                default:
                    break;
            }

        }
    }
}

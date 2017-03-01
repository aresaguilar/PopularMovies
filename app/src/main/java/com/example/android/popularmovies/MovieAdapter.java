package com.example.android.popularmovies;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.github.ivbaranov.mfb.MaterialFavoriteButton;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {

    private static final String TAG = MovieAdapter.class.getSimpleName();

    private ArrayList<Movie> mMovieArrayList;
    private ListItemClickListener mListener;
    private static int viewHolderCount;

    public MovieAdapter (ArrayList<Movie> movieArrayList, ListItemClickListener listener) {
        this.mMovieArrayList = movieArrayList;
        this.mListener = listener;
        viewHolderCount = 0;
    }

    public interface ListItemClickListener {
        void onListItemClick(Movie movieClicked);
        void onListItemStar(Movie movieClicked);
        void onListItemUnstar(Movie movieClicked);
    }

    public void changeData(ArrayList<Movie> movieArrayList) {
        this.mMovieArrayList = movieArrayList;
        notifyDataSetChanged();
    }

    @Override
    public MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutIdForListItem = R.layout.film_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(layoutIdForListItem, parent, false);
        MovieViewHolder viewHolder = new MovieViewHolder(view);

        viewHolderCount++;
        Log.d(TAG, "onCreateViewHolder: number of ViewHolders created: "
                + viewHolderCount);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MovieViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return mMovieArrayList.size();
    }

    class MovieViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView listItemImageView;
        MaterialFavoriteButton listItemFavoriteButton;
        Movie mMovie;

        public MovieViewHolder(View itemView) {
            super(itemView);

            listItemImageView = (ImageView) itemView.findViewById(R.id.iv_item_poster);
            listItemImageView.setOnClickListener(this);

            listItemFavoriteButton = (MaterialFavoriteButton) itemView.findViewById(R.id.btn_item_star);
            listItemFavoriteButton.setOnFavoriteChangeListener(
                    new MaterialFavoriteButton.OnFavoriteChangeListener() {
                        @Override
                        public void onFavoriteChanged(MaterialFavoriteButton buttonView, boolean favorite) {
                            if (favorite) {
                                mListener.onListItemStar(mMovieArrayList.get(getAdapterPosition()));
                            } else {
                                mListener.onListItemUnstar(mMovieArrayList.get(getAdapterPosition()));
                            }
                        }
                    });
        }

        void bind(int position) {
            mMovie = mMovieArrayList.get(position);
            Picasso.with(listItemImageView.getContext())
                    .load(mMovie.getPosterURL(Movie.POSTER_SIZE_MOBILE))
                    .into(listItemImageView);
        }

        @Override
        public void onClick(View v) {
            int clickedPosition = getAdapterPosition();

            switch (v.getId()) {
                case R.id.iv_item_poster:
                    mListener.onListItemClick(mMovieArrayList.get(clickedPosition));
                    break;
                default:
                    break;
            }

        }
    }
}

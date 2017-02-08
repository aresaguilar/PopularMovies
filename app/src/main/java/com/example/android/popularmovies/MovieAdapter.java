package com.example.android.popularmovies;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {

    private static final String TAG = MovieAdapter.class.getSimpleName();

    private ArrayList<Movie> mMovieArrayList;

    private static int viewHolderCount;

    public MovieAdapter (ArrayList<Movie> movieArrayList) {
        this.mMovieArrayList = movieArrayList;
        viewHolderCount = 0;
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
        Log.d(TAG, "#" + position);
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return mMovieArrayList.size();
    }

    class MovieViewHolder extends RecyclerView.ViewHolder {

        ImageView listItemImageView;

        public MovieViewHolder(View itemView) {
            super(itemView);

            listItemImageView = (ImageView) itemView.findViewById(R.id.iv_item_poster);
        }

        void bind(int position) {
            Movie movie = mMovieArrayList.get(position);
            Picasso.with(listItemImageView.getContext())
                    .load(movie.getPosterURL(Movie.POSTER_SIZE_MOBILE))
                    .into(listItemImageView);
        }

    }
}

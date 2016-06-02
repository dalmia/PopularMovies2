package com.passenger.popularmovies.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.passenger.popularmovies.R;
import com.passenger.popularmovies.fragments.MovieDetailFragment;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Custom adapter to load reviews of the movie
 */
public class ReviewAdapter extends CursorRecyclerViewAdapter<ReviewAdapter.ViewHolder> {


    Context mContext;

    public class ViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.review_author)
        TextView author;
        @Bind(R.id.review_content)
        TextView contentView;
        @Bind(R.id.review_url)
        TextView urlView;


        public ViewHolder(View view) {
            super(view);
            //Using butterknife to bind the views to their ids directly
            ButterKnife.bind(this, view);
        }
    }

    public ReviewAdapter(Context mContext, Cursor cursor) {

        super(mContext, cursor);
        this.mContext = mContext;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent,
                                         int viewType) {
        View view;
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_review_movie, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, Cursor cursor) {

        cursor.moveToFirst();
        final String authorName = cursor.getString(MovieDetailFragment.COL_REVIEW_AUTHOR);
        final String content = cursor.getString(MovieDetailFragment.COL_REVIEW_CONTENT);
        final String url = cursor.getString(MovieDetailFragment.COL_REVIEW_URL);
        holder.author.setText("Author:  " + authorName);
        holder.contentView.setText(content);
        holder.urlView.setText("For more:  " + url);

    }

}


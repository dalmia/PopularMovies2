package com.passenger.popularmovies.adapters;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.passenger.popularmovies.R;
import com.passenger.popularmovies.fragments.MovieDetailFragment;
import com.squareup.picasso.Picasso;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Custom adapter to load trailers of the movie
 */
public class TrailerAdapter extends CursorRecyclerViewAdapter<TrailerAdapter.ViewHolder> {


    Context mContext;
    private final View.OnClickListener mOnClickListener = new RecyclerViewOnClickListener();
    String trailerUrl;


    public class ViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.title)
        TextView title;
        @Bind(R.id.thumbnail)
        ImageView thumbnail;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    public TrailerAdapter(Context mContext, Cursor cursor) {
        super(mContext, cursor);
        this.mContext = mContext;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent,
                                         int viewType) {
        View view;
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_trailer_movie, parent, false);
        view.setOnClickListener(mOnClickListener);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, Cursor cursor) {
        cursor.moveToFirst();
        final String trailerName = cursor.getString(MovieDetailFragment.COL_TRAILER_NAME);
        final String source = cursor.getString(MovieDetailFragment.COL_TRAILER_SOURCE);
        holder.title.setText(trailerName);
        final String BASE_URL = mContext.getString(R.string.youtube_thumbnail_base_url);
        final String url = BASE_URL + source + "/0.jpg";
        Picasso
                .with(mContext)
                .load(url)
                .fit()
                .centerCrop()
                .into(holder.thumbnail);

        trailerUrl = mContext.getString(R.string.youtube_base_url) + source;


    }

    public class RecyclerViewOnClickListener implements View.OnClickListener {

        /**
         * Called when a view has been clicked.
         *
         * @param v The view that was clicked.
         */
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(trailerUrl));
            if (intent.resolveActivity(mContext.getPackageManager()) != null) {
                mContext.startActivity(intent);
            }
        }
    }

}
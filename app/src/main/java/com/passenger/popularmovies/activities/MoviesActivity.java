package com.passenger.popularmovies.activities;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.passenger.popularmovies.R;
import com.passenger.popularmovies.adapters.CursorRecyclerViewAdapter;
import com.passenger.popularmovies.app.AppController;
import com.passenger.popularmovies.app.Constants;
import com.passenger.popularmovies.data.MoviesContract;
import com.passenger.popularmovies.data.MoviesContract.FavouritesEntry;
import com.passenger.popularmovies.data.MoviesContract.MoviesEntry;
import com.passenger.popularmovies.fragments.MovieDetailFragment;
import com.passenger.popularmovies.model.Movie;
import com.passenger.popularmovies.utils.Utility;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Vector;

import butterknife.Bind;
import butterknife.ButterKnife;

import static com.passenger.popularmovies.app.Constants.MOVIES_KEY;
import static com.passenger.popularmovies.app.Constants.MOVIE_ADULT;
import static com.passenger.popularmovies.app.Constants.MOVIE_BACKDROP_PATH;
import static com.passenger.popularmovies.app.Constants.MOVIE_ID;
import static com.passenger.popularmovies.app.Constants.MOVIE_POPULARITY;
import static com.passenger.popularmovies.app.Constants.MOVIE_POSTER;
import static com.passenger.popularmovies.app.Constants.MOVIE_RATING;
import static com.passenger.popularmovies.app.Constants.MOVIE_RELEASE_DATE;
import static com.passenger.popularmovies.app.Constants.MOVIE_RESULTS;
import static com.passenger.popularmovies.app.Constants.MOVIE_SUMMARY;
import static com.passenger.popularmovies.app.Constants.MOVIE_TITLE;
import static com.passenger.popularmovies.app.Constants.MOVIE_VOTE_COUNT;
import static com.passenger.popularmovies.utils.Utility.getMovieTag;

public class MoviesActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {


    private final String TAG = MoviesActivity.class.getSimpleName();
    private final String DETAIL_FRAGMENT_TAG = "detail_fragment";
    private final int MOVIES_LOADER_ID = 1;
    private static final String[] MOVIE_COLUMNS = {
            MoviesEntry.TABLE_NAME + "." + MoviesEntry._ID,
            MoviesContract.MoviesEntry.TABLE_NAME + "." + MoviesEntry.COLUMN_TITLE_KEY,
            MoviesContract.MoviesEntry.TABLE_NAME + "." + MoviesEntry.COLUMN_POSTER_KEY,
            MoviesContract.MoviesEntry.TABLE_NAME + "." + MoviesEntry.COLUMN_SUMMARY_KEY,
            MoviesContract.MoviesEntry.TABLE_NAME + "." + MoviesEntry.COLUMN_RELEASE_DATE_KEY,
            MoviesContract.MoviesEntry.TABLE_NAME + "." + MoviesEntry.COLUMN_RATING_KEY,
            MoviesContract.MoviesEntry.TABLE_NAME + "." + MoviesEntry.COLUMN_ADULT_KEY,
            MoviesContract.MoviesEntry.TABLE_NAME + "." + MoviesEntry.COLUMN_FAVOURITE_KEY,
            MoviesContract.MoviesEntry.TABLE_NAME + "." + MoviesEntry.COLUMN_MOVIE_ID_KEY,
            MoviesContract.MoviesEntry.TABLE_NAME + "." + MoviesEntry.COLUMN_BACKDROP_PATH_KEY,
            MoviesContract.MoviesEntry.TABLE_NAME + "." + MoviesEntry.COLUMN_POPULARITY_KEY,
            MoviesContract.MoviesEntry.TABLE_NAME + "." + MoviesEntry.COLUMN_VOTE_COUNT_KEY,
    };

    private static final String[] FAVOURITE_COLUMNS = {
            FavouritesEntry.TABLE_NAME + "." + FavouritesEntry._ID,
            FavouritesEntry.TABLE_NAME + "." + FavouritesEntry.COLUMN_TITLE_KEY,
            FavouritesEntry.TABLE_NAME + "." + FavouritesEntry.COLUMN_POSTER_KEY,
            FavouritesEntry.TABLE_NAME + "." + FavouritesEntry.COLUMN_SUMMARY_KEY,
            FavouritesEntry.TABLE_NAME + "." + FavouritesEntry.COLUMN_RELEASE_DATE_KEY,
            FavouritesEntry.TABLE_NAME + "." + FavouritesEntry.COLUMN_RATING_KEY,
            FavouritesEntry.TABLE_NAME + "." + FavouritesEntry.COLUMN_ADULT_KEY,
            FavouritesEntry.TABLE_NAME + "." + FavouritesEntry.COLUMN_MOVIE_ID_KEY,
            FavouritesEntry.TABLE_NAME + "." + FavouritesEntry.COLUMN_BACKDROP_PATH_KEY,
            FavouritesEntry.TABLE_NAME + "." + FavouritesEntry.COLUMN_POPULARITY_KEY,
            FavouritesEntry.TABLE_NAME + "." + FavouritesEntry.COLUMN_VOTE_COUNT_KEY,
    };

    private static final int COL_MOVIE_TITLE = 1;
    private static final int COL_MOVIE_POSTER = 2;
    private static final int COL_MOVIE_SUMMARY = 3;
    private static final int COL_MOVIE_RELEASE_DATE = 4;
    private static final int COL_MOVIE_RATING = 5;
    private static final int COL_MOVIE_ADULT = 6;
    private static final int COL_MOVIE_FAVOURITE = 7;
    private static final int COL_MOVIE_ID = 8;
    private static final int COL_MOVIE_BACKDROP_PATH = 9;
    private static final int COL_MOVIE_POPULARITY = 10;
    private static final int COL_MOVIE_VOTE_COUNT = 11;
    private static final int COL_FAVOURITE_MOVIE_TITLE = 1;
    private static final int COL_FAVOURITE_MOVIE_POSTER = 2;
    private static final int COL_FAVOURITE_MOVIE_SUMMARY = 3;
    private static final int COL_FAVOURITE_MOVIE_RELEASE_DATE = 4;
    private static final int COL_FAVOURITE_MOVIE_RATING = 5;
    private static final int COL_FAVOURITE_MOVIE_ADULT = 6;
    private static final int COL_FAVOURITE_MOVIE_ID = 7;
    private static final int COL_FAVOURITE_MOVIE_BACKDROP_PATH = 8;
    private static final int COL_FAVOURITE_MOVIE_POPULARITY = 9;
    private static final int COL_FAVOURITE_MOVIE_VOTE_COUNT = 10;

    String sortOrder;
    Spinner sortMovies;
    boolean mTwoPane;

    @Bind(R.id.movies_list)
    RecyclerView mRecyclerView;
    @Bind(R.id.loading)
    View loadingView;
    @Bind(R.id.swipe_to_refresh)
    SwipeRefreshLayout mSwipeRefreshLayout;
    MoviesAdapter mAdapter;
    RecyclerView.LayoutManager mLayoutManager;
    //current choice to determine whether 'highest rated' is selected
    // or 'Most Popular' is selected
    int currentSortChoice = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movies);
        //Necessary addition for using Butterknife
        ButterKnife.bind(this);
        //Initialise the cursor Loader
        initializeLoader();
        //Setup the toolbar as the support action bar
        setupToolbar();
        //Setup Recycler View
        setupSpinner();
        setupRecyclerView();

        currentSortChoice = getMovieTag(this);
        if (findViewById(R.id.movie_detail) != null) {
            mTwoPane = true;
        } else {
            mTwoPane = false;
        }
        if (savedInstanceState == null || !savedInstanceState.containsKey(MOVIES_KEY)) {
            refreshMovieList();
        } else {

            //Restoring the arrayList values
            loadMoviesIntoAdapter();
        }

        //Listener for the swipe to refresh gesture
        mSwipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        mAdapter.swapCursor(null);
                        refreshMovieList();
                    }
                }
        );

    }

    public void refreshMovieList(){

        try {
            //Check internet connectivity
            if (Utility.isConnected(MoviesActivity.this)) {
                String sortOrder = MoviesEntry._ID + " ASC";
                Uri uri;
                Cursor cur;
                if (currentSortChoice == 2) {
                    uri = FavouritesEntry.buildFavouritesUri();
                    cur = getContentResolver().query(uri,
                            FAVOURITE_COLUMNS, null, null, sortOrder);
                    if (mAdapter != null) {
                        mAdapter.swapCursor(cur);
                    } else {
                        mAdapter = new MoviesAdapter(this, cur);
                    }
                } else {
                    FetchMovies();
                }

            } else {
                loadingView.setVisibility(View.GONE);
                Toast.makeText(getApplicationContext(), R.string.check_internet_connection, Toast.LENGTH_SHORT).show();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadMoviesIntoAdapter() {
        String sortOrder = MoviesEntry._ID + " ASC";
        String choice;
        Uri uri;
        Cursor cur;
        if (currentSortChoice == 2) {
            uri = FavouritesEntry.buildFavouritesUri();
            cur = getContentResolver().query(uri,
                    FAVOURITE_COLUMNS, null, null, sortOrder);
        } else {
            uri = MoviesEntry.buildMovieUri();
            if (currentSortChoice == 0) {
                choice = getString(R.string.highest_rated_url_value);

            } else {
                choice = getString(R.string.most_popular_url_value);
            }
            cur = getContentResolver().query(uri,
                    MOVIE_COLUMNS, MoviesEntry.COLUMN_TAG_KEY + " = ?", new String[]{choice}, sortOrder);
        }
        if(cur.getCount()==0){
            mSwipeRefreshLayout.setRefreshing(false);
        }
        if(mAdapter!=null){
            mAdapter.swapCursor(cur);
        }else{
            mAdapter = new MoviesAdapter(this, cur);
        }
    }

    /**
     * Initializes the cursor loader with the respective loader ID
     */
    public void initializeLoader() {
        getLoaderManager().initLoader(MOVIES_LOADER_ID, null, this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadMoviesIntoAdapter();
    }

    /**
     * Initialize the toolbar
     */
    public void setupToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_movies);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getResources().getString(R.string.app_name));
    }

    /**
     * Setting up the spinner to chose the type of movies
     * to be displayed
     */
    public void setupSpinner() {
        sortMovies = (Spinner) findViewById(R.id.sort_movies);
        //setting custom textView as spinner item
        ArrayAdapter<CharSequence> sortOrderAdapter = ArrayAdapter.createFromResource(this,
                R.array.sort_choice_list, R.layout.sort_movies_list_item);
        //setting custom textView as spinner dropdown item
        sortOrderAdapter.setDropDownViewResource(R.layout.sort_movies_dropdown_resource);
        //just to avoid warnings
        assert sortMovies != null;
        currentSortChoice = Utility.getMovieTag(this);
        sortMovies.setSelection(currentSortChoice);
        loadMoviesIntoAdapter();

        sortMovies.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //Shouldn't load if already selected one is selected again
                if (currentSortChoice != position) {
                    currentSortChoice = position;
                    try {
                        //Check internet connectivity
                        if (Utility.isConnected(MoviesActivity.this)) {
                            Utility.setMovieTag(position, MoviesActivity.this);
                            loadingView.setVisibility(View.VISIBLE);
                            if (currentSortChoice == 2) {
                                String sortOrder = FavouritesEntry._ID + " ASC";
                                Uri uri = FavouritesEntry.buildFavouritesUri();
                                Cursor cur = getContentResolver().query(uri,
                                        FAVOURITE_COLUMNS, null, null, sortOrder);
                                if(cur.getCount()==0){
                                    loadingView.setVisibility(View.GONE);
                                }
                                mAdapter.swapCursor(cur);

                            } else {
                                FetchMovies();

                            }

                        } else {
                            Toast.makeText(getApplicationContext(), getString(R.string.check_internet_connection), Toast.LENGTH_SHORT).show();

                        }

                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        sortMovies.setAdapter(sortOrderAdapter);

    }

    /**
     * Setting up the recycler view by adding the layout
     * manager and adding the adapter
     */
    public void setupRecyclerView() {
        mLayoutManager = new GridLayoutManager(this, 2);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public android.content.Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String sortOrder = MoviesEntry._ID + " ASC";
        String choice = "";

        Uri uri;

        if (currentSortChoice == 2) {
            uri = FavouritesEntry.buildFavouritesUri();
            return new android.content.CursorLoader(this,
                    uri,
                    FAVOURITE_COLUMNS,
                    null,
                    null,
                    sortOrder);
        } else {
            uri = MoviesEntry.buildMovieUri();
            if (currentSortChoice == 0) {
                choice = getString(R.string.highest_rated_url_value);
            } else if (currentSortChoice == 1) {
                choice = getString(R.string.most_popular_url_value);
            }
            return new android.content.CursorLoader(this,
                    uri,
                    MOVIE_COLUMNS,
                    MoviesEntry.COLUMN_TAG_KEY + " = ?",
                    new String[]{choice},
                    sortOrder);
        }

    }

    @Override
    public void onLoadFinished(android.content.Loader<Cursor> loader, Cursor data) {

        if (currentSortChoice != 2)
            mAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(android.content.Loader<Cursor> loader) {

        mAdapter.swapCursor(null);
    }


    /**
     * Custom adapter to load movie data
     */
    public class MoviesAdapter extends CursorRecyclerViewAdapter<MoviesAdapter.ViewHolder> {


        Context mContext;
        private final View.OnClickListener mOnClickListener = new RecyclerViewOnClickListener();
        private Cursor mCursor;

        /**
         * Provide a reference to the views for each data item
         * Complex data items may need more than one view per item, and
         * we provide access to all the views for a data item in a view holder
         */

        public class ViewHolder extends RecyclerView.ViewHolder {
            // each data item is just a string in this case
            @Bind(R.id.movies_list_item_image)
            public ImageView poster;
            @Bind(R.id.title)
            public TextView title;
            @Bind(R.id.rating)
            public TextView rating;

            public ViewHolder(View v) {
                super(v);
                ButterKnife.bind(this, v);
            }
        }

        public MoviesAdapter(Context mContext, Cursor cursor) {

            super(mContext, cursor);
            this.mContext = mContext;

        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent,
                                             int viewType) {
            View view;
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.movies_list_item, parent, false);
            view.setOnClickListener(mOnClickListener);
            ViewHolder holder = new ViewHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, Cursor cursor) {

            this.mCursor = cursor;
            String imageBaseUrl = mContext.getString(R.string.image_base_url);
            Movie movie = Movie.fromCursor(cursor);
            holder.title.setText(movie.title);
            holder.rating.setText(movie.rating + "/10");
            if (movie.poster != null) {
                Picasso.with(mContext)
                        .load(imageBaseUrl + cursor.getString(COL_MOVIE_POSTER))
                        .error(R.drawable.ic_error)
                        .into(holder.poster, new Callback() {
                            @Override
                            public void onSuccess() {
                                if (loadingView.getVisibility() == View.VISIBLE) {
                                    loadingView.setVisibility(View.GONE);
                                }
                            }

                            @Override
                            public void onError() {
                                if (loadingView.getVisibility() == View.VISIBLE) {
                                    loadingView.setVisibility(View.GONE);
                                }
                            }
                        });

            }

        }

        public class RecyclerViewOnClickListener implements View.OnClickListener {

            /**
             * Called when a view has been clicked.
             *
             * @param v The view that was clicked.
             */
            @Override
            public void onClick(View v) {
                int position = mRecyclerView.getChildAdapterPosition(v);
                mCursor.moveToPosition(position);
                String id;
                if (currentSortChoice == 2) {
                    id = mCursor.getString(COL_FAVOURITE_MOVIE_ID);
                } else {
                    id = mCursor.getString(COL_MOVIE_ID);
                }
                Uri uri = MoviesContract.MoviesEntry.buildMovieUriWithId(id);

                if (mTwoPane) {
                    Bundle arguments = new Bundle();
                    arguments.putParcelable(Constants.MOVIE_DETAILS_URI, uri);
                    MovieDetailFragment movieDetailFragment = new MovieDetailFragment();
                    movieDetailFragment.setArguments(arguments);
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.movie_detail, movieDetailFragment, DETAIL_FRAGMENT_TAG).commit();

                } else {
                    Intent intent = new Intent(mContext, MovieDetailActivity.class).setData(uri);
                    mContext.startActivity(intent);
                }

            }
        }

    }


    /**
     * Function to return url to be called to fetch the movies
     *
     * @return
     * @throws MalformedURLException
     */
    public String setUrl() throws MalformedURLException {

        final String MOVIES_BASE_URL = getApplicationContext().getResources().
                getString(R.string.movies_base_url);
        final String API_PARAM = getString(R.string.api_param_key);
        final String api_key = getApplicationContext().getResources().
                getString(R.string.api_key);

        if (currentSortChoice == 0) {
            sortOrder = getString(R.string.highest_rated_url_value);
        } else {
            sortOrder = getString(R.string.most_popular_url_value);
        }

        Uri builtUri = Uri.parse(MOVIES_BASE_URL).buildUpon()
                .appendPath(sortOrder)
                .appendQueryParameter(API_PARAM, api_key)
                .build();
        URL url = new URL(builtUri.toString());
        return url.toString();
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
    }

    /**
     * Function to fetch the movies list.
     *
     * @throws MalformedURLException
     */
    public void FetchMovies() throws MalformedURLException {

        String url = setUrl();
        String tag_string_req = getString(R.string.fetch_movies_tag);
        JSONObject credentialsJSON = new JSONObject();
        Log.d(TAG, credentialsJSON.toString());
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                url,
                credentialsJSON,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            //add the Movie objects to the adapter

                            getMoviesList(response);

                        } catch (Exception e) {
                            loadingView.setVisibility(View.GONE);
                            mSwipeRefreshLayout.setRefreshing(false);
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mSwipeRefreshLayout.setRefreshing(false);
                loadingView.setVisibility(View.GONE);
                error.printStackTrace();

            }
        });

        AppController.getInstance().addToRequestQueue(jsonObjReq, tag_string_req);

    }

    public void getMoviesList(JSONObject moviesJSON) throws JSONException {


        JSONArray movieArray = moviesJSON.getJSONArray(MOVIE_RESULTS);
        if(movieArray.length()==0){
            loadingView.setVisibility(View.GONE);
        }
        Vector<ContentValues> cVVector = new Vector<ContentValues>(movieArray.length());
        for (int i = 0; i < movieArray.length(); i++) {

            JSONObject movieData = movieArray.getJSONObject(i);
            ContentValues movieValues = new ContentValues();
            String poster = movieData.getString(MOVIE_POSTER);
            String title = movieData.getString(MOVIE_TITLE);
            String summary = movieData.getString(MOVIE_SUMMARY);
            String rating = movieData.getString(MOVIE_RATING);
            String adult = movieData.getString(MOVIE_ADULT);
            String movieId = movieData.getString(MOVIE_ID);
            String backdropPath = movieData.getString(MOVIE_BACKDROP_PATH);
            String popularity = movieData.getString(MOVIE_POPULARITY);
            String voteCount = movieData.getString(MOVIE_VOTE_COUNT);
            String releaseDateValue = movieData.getString(MOVIE_RELEASE_DATE);
            if (!releaseDateValue.equals("")) {
                String[] dateParameters;
                dateParameters = releaseDateValue.split("-");

                //Creating a new instance of calendar
                Calendar calendar = Calendar.getInstance();//Setting the calendar time to the one received as releaseDate
                if (dateParameters.length > 0) {
                    calendar.set(Integer.valueOf(dateParameters[0]), Integer.valueOf(dateParameters[1]), Integer.valueOf(dateParameters[2]));
                    Date date = new Date(calendar.getTimeInMillis());
                    //Used to format the date as required
                    SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy", Locale.US);
                    releaseDateValue = sdf.format(date);
                }
                movieValues.put(MoviesEntry.COLUMN_TITLE_KEY, title);
                movieValues.put(MoviesEntry.COLUMN_POSTER_KEY, poster.substring(1, poster.length()));
                movieValues.put(MoviesEntry.COLUMN_SUMMARY_KEY, summary);
                movieValues.put(MoviesEntry.COLUMN_RELEASE_DATE_KEY, releaseDateValue);
                movieValues.put(MoviesEntry.COLUMN_RATING_KEY, rating);
                movieValues.put(MoviesEntry.COLUMN_ADULT_KEY, adult);
                movieValues.put(MoviesEntry.COLUMN_MOVIE_ID_KEY, movieId);
                movieValues.put(MoviesEntry.COLUMN_BACKDROP_PATH_KEY, backdropPath);
                movieValues.put(MoviesEntry.COLUMN_POPULARITY_KEY, popularity);
                movieValues.put(MoviesEntry.COLUMN_VOTE_COUNT_KEY, voteCount);
                movieValues.put(MoviesEntry.COLUMN_TAG_KEY, sortOrder);
                cVVector.add(movieValues);

                Log.d(TAG, "Movies :" + i + "\n" + movieValues.toString());
            }
            // add to database
            if (cVVector.size() > 0) {
                ContentValues[] cvArray = new ContentValues[cVVector.size()];
                cVVector.toArray(cvArray);
                int inserted = this.getContentResolver().bulkInsert(MoviesEntry.CONTENT_URI, cvArray);
                getLoaderManager().restartLoader(MOVIES_LOADER_ID, null, this);
            }
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }
}

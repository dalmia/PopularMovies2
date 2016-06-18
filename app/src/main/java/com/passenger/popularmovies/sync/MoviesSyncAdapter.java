package com.passenger.popularmovies.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SyncRequest;
import android.content.SyncResult;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.passenger.popularmovies.R;
import com.passenger.popularmovies.app.AppController;
import com.passenger.popularmovies.data.MoviesContract;
import com.passenger.popularmovies.utils.Utility;

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


public class MoviesSyncAdapter extends AbstractThreadedSyncAdapter {
    public final String TAG = MoviesSyncAdapter.class.getSimpleName();
    String sortOrder = "";
    public static final int SYNC_INTERVAL = 60;
    public static final int SYNC_FLEXTIME = SYNC_INTERVAL/3;

    public MoviesSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {

        try {
            String url = setUrl();
            if(url!=null) {
                String tag_string_req = getContext().getString(R.string.fetch_movies_tag);
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
                                    Log.d(TAG, response.toString());
                                    getMoviesList(response);

                                } catch (Exception e) {
//                                    loadingView.setVisibility(View.GONE);
//                                    mSwipeRefreshLayout.setRefreshing(false);
                                    e.printStackTrace();
                                }
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();

                    }
                });

                AppController.getInstance().addToRequestQueue(jsonObjReq, tag_string_req);
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

    }

    /**
     * Helper method to have the sync adapter sync immediately
     * @param context The context used to access the account service
     */
    public static void syncImmediately(Context context) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        ContentResolver.requestSync(getSyncAccount(context),
                context.getString(R.string.content_authority), bundle);
    }

    /**
     * Helper method to get the fake account to be used with SyncAdapter, or make a new one
     * if the fake account doesn't exist yet.  If we make a new account, we call the
     * onAccountCreated method so we can initialize things.
     *
     * @param context The context used to access the account service
     * @return a fake account.
     */
    public static Account getSyncAccount(Context context) {
        // Get an instance of the Android account manager
        AccountManager accountManager =
                (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);

        // Create the account type and default account
        Account newAccount = new Account(
                context.getString(R.string.app_name), context.getString(R.string.sync_account_type));

        // If the password doesn't exist, the account doesn't exist
        if ( null == accountManager.getPassword(newAccount) ) {

        /*
         * Add the account and account type, no password or user data
         * If successful, return the Account object, otherwise report an error.
         */
            if (!accountManager.addAccountExplicitly(newAccount, "", null)) {
                return null;
            }
            /*
             * If you don't set android:syncable="true" in
             * in your <provider> element in the manifest,
             * then call ContentResolver.setIsSyncable(account, AUTHORITY, 1)
             * here.
             */
            onAccountCreated(newAccount, context);

        }
        return newAccount;
    }

    /**
     * Function to return url to be called to fetch the movies
     *
     * @return
     * @throws MalformedURLException
     */
    public String setUrl() throws MalformedURLException {

        int currentSortChoice = Utility.getMovieTag(getContext());
        final String MOVIES_BASE_URL = getContext().getApplicationContext().getResources().
                getString(R.string.movies_base_url);
        final String API_PARAM = getContext().getString(R.string.api_param_key);
        final String api_key = getContext().getApplicationContext().getResources().
                getString(R.string.api_key);

        if (currentSortChoice == 0) {
            sortOrder = getContext().getString(R.string.highest_rated_url_value);
        } else if(currentSortChoice == 1){
            sortOrder = getContext().getString(R.string.most_popular_url_value);
        }
        if(!sortOrder.equals("")) {
            Uri builtUri = Uri.parse(MOVIES_BASE_URL).buildUpon()
                    .appendPath(sortOrder)
                    .appendQueryParameter(API_PARAM, api_key)
                    .build();
            URL url = new URL(builtUri.toString());
            return url.toString();
        }
        return null;
    }

    public void getMoviesList(JSONObject moviesJSON) throws JSONException {


        JSONArray movieArray = moviesJSON.getJSONArray(MOVIE_RESULTS);
//        if(movieArray.length()==0){
//            loadingView.setVisibility(View.GONE);
//        }
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
                movieValues.put(MoviesContract.MoviesEntry.COLUMN_TITLE_KEY, title);
                movieValues.put(MoviesContract.MoviesEntry.COLUMN_POSTER_KEY, poster.substring(1, poster.length()));
                movieValues.put(MoviesContract.MoviesEntry.COLUMN_SUMMARY_KEY, summary);
                movieValues.put(MoviesContract.MoviesEntry.COLUMN_RELEASE_DATE_KEY, releaseDateValue);
                movieValues.put(MoviesContract.MoviesEntry.COLUMN_RATING_KEY, rating);
                movieValues.put(MoviesContract.MoviesEntry.COLUMN_ADULT_KEY, adult);
                movieValues.put(MoviesContract.MoviesEntry.COLUMN_MOVIE_ID_KEY, movieId);
                movieValues.put(MoviesContract.MoviesEntry.COLUMN_BACKDROP_PATH_KEY, backdropPath);
                movieValues.put(MoviesContract.MoviesEntry.COLUMN_POPULARITY_KEY, popularity);
                movieValues.put(MoviesContract.MoviesEntry.COLUMN_VOTE_COUNT_KEY, voteCount);
                movieValues.put(MoviesContract.MoviesEntry.COLUMN_TAG_KEY, sortOrder);
                cVVector.add(movieValues);

                Log.d(TAG, "Movies :" + i + "\n" + movieValues.toString());
            }
            // add to database
            if (cVVector.size() > 0) {
                ContentValues[] cvArray = new ContentValues[cVVector.size()];
                cVVector.toArray(cvArray);
                getContext().getContentResolver().bulkInsert(MoviesContract.MoviesEntry.CONTENT_URI, cvArray);
                //getLoaderManager().restartLoader(MOVIES_LOADER_ID, null, this);
            }
            //mSwipeRefreshLayout.setRefreshing(false);
        }
    }

    /**
     * Helper method to schedule the sync adapter periodic execution
     */
    public static void configurePeriodicSync(Context context, int syncInterval, int flexTime) {
        Account account = getSyncAccount(context);
        String authority = context.getString(R.string.content_authority);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // we can enable inexact timers in our periodic sync
            SyncRequest request = new SyncRequest.Builder().
                    syncPeriodic(syncInterval, flexTime).
                    setSyncAdapter(account, authority).
                    setExtras(new Bundle()).build();
            ContentResolver.requestSync(request);
        } else {
            ContentResolver.addPeriodicSync(account,
                    authority, new Bundle(), syncInterval);
        }
    }


    private static void onAccountCreated(Account newAccount, Context context) {
        /*
         * Since we've created an account
         */
        MoviesSyncAdapter.configurePeriodicSync(context, SYNC_INTERVAL, SYNC_FLEXTIME);

        /*
         * Without calling setSyncAutomatically, our periodic sync will not be enabled.
         */
        ContentResolver.setSyncAutomatically(newAccount, context.getString(R.string.content_authority), true);

        /*
         * Finally, let's do a sync to get things started
         */
        syncImmediately(context);
    }

    public static void initializeSyncAdapter(Context context) {
        getSyncAccount(context);
    }



}
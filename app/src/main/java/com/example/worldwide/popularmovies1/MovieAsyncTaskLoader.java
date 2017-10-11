package com.example.worldwide.popularmovies1;

import android.content.Context;

import java.util.ArrayList;


class MovieAsyncTaskLoader extends android.support.v4.content.AsyncTaskLoader<ArrayList<Movie>> {

    private String mHowToSort;

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        forceLoad(); // Force an asynchronous load
    }

    MovieAsyncTaskLoader(Context context, String howToSort) {
        super(context);

        mHowToSort = howToSort; //used to determine on what the query url will perform
    }

    @Override
    public ArrayList<Movie> loadInBackground() {

        //start fetching the data from the internet in a background thread
        return NetworkUtils.fetchData(mHowToSort);
    }
}

package com.example.worldwide.popularmovies1;

import android.content.Context;

import java.util.List;


class MovieAsyncTaskLoader extends android.support.v4.content.AsyncTaskLoader<List<Movie>> {

    private String mHowToSort;

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        forceLoad(); // Force an asynchronous load
    }

    public MovieAsyncTaskLoader(Context context, String howToSort) {
        super(context);

        mHowToSort = howToSort; //used to determine on what the query url will perform
    }

    @Override
    public List<Movie> loadInBackground() {

        //start fetching the data from the internet in a background thread
        return NetworkUtils.fetchData(mHowToSort);
    }
}

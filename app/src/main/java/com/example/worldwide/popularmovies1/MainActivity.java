package com.example.worldwide.popularmovies1;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class MainActivity extends AppCompatActivity
        implements android.support.v4.app.LoaderManager.LoaderCallbacks<ArrayList<Movie>>,
        MoviesAdapter.OnItemClickListener {

    private static final String TAG = MainActivity.class.toString();
    //string to indicate which sorting mode must be performed it been default on most popular endPoint
    private static String howToSort = Constants.MOST_POPULAR_MOVIES;

    @InjectView(R.id.rv_movies)
    RecyclerView recyclerView;
    private MoviesAdapter adapter;

    //progress bar to show while the recyclerView loading
    @InjectView(R.id.pb_searching)
    ProgressBar mProgressBar;

    //list of movies to hold data returned from the loader
    private ArrayList<Movie> mListOfPopularMovies;
    private ArrayList<Movie> mListOfTopRatedMovies;


    private Boolean isHowToSortOnMostPopular() {
        Log.d(TAG, "is how to sort method return " + howToSort.equals(Constants.MOST_POPULAR_MOVIES));
        return howToSort.equals(Constants.MOST_POPULAR_MOVIES);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        if (isHowToSortOnMostPopular()) {
            outState.putParcelableArrayList(Constants.MOST_POPULAR_MOVIES, mListOfPopularMovies);
            howToSort = Constants.MOST_POPULAR_MOVIES;

        } else {
            outState.putParcelableArrayList(Constants.TOP_RATED_MOVIES, mListOfTopRatedMovies);
            howToSort = Constants.TOP_RATED_MOVIES;

        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        if (isHowToSortOnMostPopular()) {
            Log.d(TAG, " if onrestoreinstance invoked");
            mListOfPopularMovies = savedInstanceState.getParcelableArrayList(Constants.MOST_POPULAR_MOVIES);
            adapter = new MoviesAdapter(this, mListOfPopularMovies, this);
            recyclerView.setAdapter(adapter);

        } else {
            Log.d(TAG, "if else is invoked");
            mListOfTopRatedMovies = savedInstanceState.getParcelableArrayList(Constants.TOP_RATED_MOVIES);
            adapter = new MoviesAdapter(this, mListOfTopRatedMovies, this);
            recyclerView.setAdapter(adapter);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //binding the butterKnife library
        ButterKnife.inject(this);

        //check if there internet connection and show a toast to the user if not
        initTheLoaderIfThereConnection();

        //recyclerView setup
        recyclerView.setHasFixedSize(true);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(layoutManager);


        mProgressBar.setVisibility(View.VISIBLE);//progressBar find and set the visibility to visible until loader finishes

    }

    private void initTheLoaderIfThereConnection() {
        if (isOnline()) {
            //init of the loader obviously :D
            getSupportLoaderManager().initLoader(Constants.MOVIES_LOADER, null, this);
        } else {
            Toast.makeText(this, R.string.internet_required, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public Loader<ArrayList<Movie>> onCreateLoader(int id, Bundle args) {
        return new MovieAsyncTaskLoader(this, howToSort); // change on user demand
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<Movie>> loader, ArrayList<Movie> data) {

        //check if the ArrayList item not null and if it's not pass it to the adapter and show it
        if (data != null && !data.isEmpty()) {
            mProgressBar.setVisibility(View.INVISIBLE); // make progressBar invisible after the data is loaded
            adapter = new MoviesAdapter(this, data, this);
            recyclerView.setAdapter(adapter);

            if (isHowToSortOnMostPopular()) {
                mListOfPopularMovies = data; // then pass the list of most popular movies
            } else {
                mListOfTopRatedMovies = data; // then pass the list of top rated movies
            }
        }

    }


    @Override
    public void onLoaderReset(Loader<ArrayList<Movie>> loader) {
        //reset the adapter when no longer needed
        adapter = new MoviesAdapter(null, null, null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        //Upon click on any of this buttons it will change the sorting string and restart the loader
        switch (item.getItemId()) {

            case R.id.mb_most_popular_sort:

                howToSort = Constants.MOST_POPULAR_MOVIES;

                if (isOnline() && mListOfPopularMovies == null) {
                    Log.d(TAG, "is online ?" + String.valueOf(mListOfPopularMovies == null));
                    getSupportLoaderManager().restartLoader(Constants.MOVIES_LOADER, null, this);
                    Toast.makeText(this, R.string.most_popular_toast, Toast.LENGTH_LONG).show();
                } else if (mListOfPopularMovies != null && mListOfPopularMovies.size() > 0) {
                    Log.d(TAG, "the list not null?" + String.valueOf(mListOfPopularMovies == null) + "and size of it is " + mListOfPopularMovies.size());
                    adapter = new MoviesAdapter(this, mListOfPopularMovies, this);
                    recyclerView.setAdapter(adapter);
                } else {
                    Toast.makeText(this, R.string.internet_required, Toast.LENGTH_LONG).show();
                }
                break;

            case R.id.mb_top_rated_sort:

                howToSort = Constants.TOP_RATED_MOVIES;

                if (isOnline() && mListOfTopRatedMovies == null) {

                    Log.d(TAG, "is online ?" + String.valueOf(mListOfTopRatedMovies == null));
                    getSupportLoaderManager().restartLoader(Constants.MOVIES_LOADER, null, this);
                    Toast.makeText(this, R.string.top_rated_toast, Toast.LENGTH_LONG).show();

                } else if (mListOfTopRatedMovies != null && mListOfTopRatedMovies.size() > 0) {
                    Log.d(TAG, "the list not null?" + String.valueOf(mListOfTopRatedMovies == null));
                    adapter = new MoviesAdapter(this, mListOfTopRatedMovies, this);
                    recyclerView.setAdapter(adapter);
                } else {
                    Toast.makeText(this, R.string.internet_required, Toast.LENGTH_LONG).show();

                }

                break;
        }
        return true;
    }

    @Override
    public void onClick(int position) {

        Movie movie;

        if (isHowToSortOnMostPopular()) {
            movie = mListOfPopularMovies.get(position);
        } else {
            movie = mListOfTopRatedMovies.get(position);

        }

        //instantiating an intent to start details activity passing all the data needed as extras
        Intent intent = new Intent(this, DetailsScreen.class);
        intent.putExtra(Constants.MOVIE_OBJECT_TAG, movie);

//        intent.putExtra(Constants.ORIGINAL_TITLE, movie.getOriginalTitle());
//        intent.putExtra(Constants.USER_RATING, movie.getUserRating());
//        intent.putExtra(Constants.OVERVIEW, movie.getOverView());
//        intent.putExtra(Constants.RELEASE_DATE, movie.getReleaseDate());
//        intent.putExtra(Constants.MOVIE_POSTER_PATH, movie.getPosterPath());
        startActivity(intent);
    }

    /**
     * A method based on stackOverFlow answer to check if the internet available on the targeted
     * device or not
     */
    private boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }


}


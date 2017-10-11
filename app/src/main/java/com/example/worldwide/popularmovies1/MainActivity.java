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
        return howToSort.equals(Constants.MOST_POPULAR_MOVIES);
    }

    /**
     * Save the parcelable ArrayList of our object to be able to use it later in onRestore method
     * @param outState is the Bundle will be saving our data
     */
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

    /**
     * Restore the instance we already saved and populate the list with the data and then pass it
     * to the Adapter and show it in the recyclerView
     */
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        if (savedInstanceState != null) {
            if (isHowToSortOnMostPopular()) {
                mListOfPopularMovies = savedInstanceState.getParcelableArrayList(Constants.MOST_POPULAR_MOVIES);
                adapter = new MoviesAdapter(this, mListOfPopularMovies, this);
                recyclerView.setAdapter(adapter);

            } else {
                mListOfTopRatedMovies = savedInstanceState.getParcelableArrayList(Constants.TOP_RATED_MOVIES);
                adapter = new MoviesAdapter(this, mListOfTopRatedMovies, this);
                recyclerView.setAdapter(adapter);
            }
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

            //when most popular movies button clicked
            case R.id.mb_most_popular_sort:
                //first set the howToSort String to most popular movies constant
                howToSort = Constants.MOST_POPULAR_MOVIES;

                //check if the device is online and the list of movies is null
                if (isOnline() && mListOfPopularMovies == null) {
                    getSupportLoaderManager().restartLoader(Constants.MOVIES_LOADER, null, this);
                    Toast.makeText(this, R.string.most_popular_toast, Toast.LENGTH_LONG).show();

                    //if the list of movies not null then populate the views with it
                } else if (mListOfPopularMovies != null && mListOfPopularMovies.size() > 0) {
                    adapter = new MoviesAdapter(this, mListOfPopularMovies, this);
                    recyclerView.setAdapter(adapter);

                    //if there's no connection and the list is null show toast to tell the user that app need internet
                } else {
                    Toast.makeText(this, R.string.internet_required, Toast.LENGTH_LONG).show();
                }
                break;

            //when the top rated movies button clicked
            case R.id.mb_top_rated_sort:

                //first set the howToSort String to top rated movies constant
                howToSort = Constants.TOP_RATED_MOVIES;

                //check if the device online and the list of movies is null
                if (isOnline() && mListOfTopRatedMovies == null) {
                    getSupportLoaderManager().restartLoader(Constants.MOVIES_LOADER, null, this);
                    Toast.makeText(this, R.string.top_rated_toast, Toast.LENGTH_LONG).show();

                    //if the list of movies not null then populate the views with it
                } else if (mListOfTopRatedMovies != null && mListOfTopRatedMovies.size() > 0) {
                    adapter = new MoviesAdapter(this, mListOfTopRatedMovies, this);
                    recyclerView.setAdapter(adapter);

                    //if there's no connection and the list is null show toast to tell the user that app need internet
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

        //check which list must be populated using the howToSort boolean
        if (isHowToSortOnMostPopular()) {
            movie = mListOfPopularMovies.get(position);
        } else {
            movie = mListOfTopRatedMovies.get(position);
        }

        //instantiating an intent to start details activity passing it the parcelable Movie object
        Intent intent = new Intent(this, DetailsScreen.class);
        intent.putExtra(Constants.MOVIE_OBJECT_TAG, movie);

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


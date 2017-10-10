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

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class MainActivity extends AppCompatActivity
        implements android.support.v4.app.LoaderManager.LoaderCallbacks<List<Movie>>,
        MoviesAdapter.OnItemClickListener {

    //string to indicate which sorting mode must be performed it been default on most popular endPoint
    private String howToSort = Constants.MOST_POPULAR_MOVIES;

    @InjectView(R.id.rv_movies)
    RecyclerView recyclerView;
    private MoviesAdapter adapter;

    //list of movies to hold data returned from the loader
    private List<Movie> mMovies;

    //progress bar to show while the recyclerView loading
    @InjectView(R.id.pb_searching)
    ProgressBar mProgressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //check if there internet connection and show a toast to the user if not
        initTheLoaderIfThereConnection();

        //binding the butterKnife library
        ButterKnife.inject(this);

        //recyclerView setup
        recyclerView.setHasFixedSize(true);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(layoutManager);

        //progressBar find and set the visibility to visible until loader finishes
        mProgressBar.setVisibility(View.VISIBLE);
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
    public Loader<List<Movie>> onCreateLoader(int id, Bundle args) {
        return new MovieAsyncTaskLoader(this, howToSort); // change on user demand
    }

    @Override
    public void onLoadFinished(Loader<List<Movie>> loader, List<Movie> data) {

        //check if the list item not null and if it's not pass it to the adapter and show it
        if (data != null && !data.isEmpty()) {
            mProgressBar.setVisibility(View.INVISIBLE); // make progressBar invisible after the data is loaded
            adapter = new MoviesAdapter(this, data, this);
            recyclerView.setAdapter(adapter);
            mMovies = data; // then pass the list of movies to the member variable
        }

    }

    @Override
    public void onLoaderReset(Loader<List<Movie>> loader) {
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
                if (isOnline()) {
                    howToSort = Constants.MOST_POPULAR_MOVIES;
                    getSupportLoaderManager().restartLoader(Constants.MOVIES_LOADER, null, this);
                    Toast.makeText(this, R.string.most_popular_toast, Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(this, R.string.internet_required, Toast.LENGTH_LONG).show();

                }
                break;

            case R.id.mb_top_rated_sort:
                if (isOnline()) {
                    howToSort = Constants.TOP_RATED_MOVIES;
                    getSupportLoaderManager().restartLoader(Constants.MOVIES_LOADER, null, this);
                    Toast.makeText(this, R.string.top_rated_toast, Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(this, R.string.internet_required, Toast.LENGTH_LONG).show();

                }
                break;
        }

        return true;
    }


    @Override
    public void onClick(int position) {

        Movie movie = mMovies.get(position);

        //instantiating an intent to start details activity passing all the data needed as extras
        Intent intent = new Intent(this, DetailsScreen.class);
        intent.putExtra(Constants.ORIGINAL_TITLE, movie.getOriginalTitle());
        intent.putExtra(Constants.USER_RATING, movie.getUserRating());
        intent.putExtra(Constants.OVERVIEW, movie.getOverView());
        intent.putExtra(Constants.RELEASE_DATE, movie.getReleaseDate());
        intent.putExtra(Constants.MOVIE_POSTER_PATH, movie.getPosterPath());
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


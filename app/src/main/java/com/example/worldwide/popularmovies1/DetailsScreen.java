package com.example.worldwide.popularmovies1;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class DetailsScreen extends AppCompatActivity {

    private Intent intent;

    @InjectView(R.id.tv_original_title)
    TextView mOriginalTitle;
    @InjectView(R.id.tv_user_rating)
    TextView mUserRating;
    @InjectView(R.id.tv_overview)
    TextView mOverview;
    @InjectView(R.id.tv_release_date)
    TextView mReleaseDate;
    @InjectView(R.id.ratingBar)
    RatingBar mRatingBar;
    @InjectView(R.id.iv_details_activity_poster)
    ImageView mMoviePoster;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_details_screen);

        ButterKnife.inject(this);

        intent = getIntent();

        settingInfoOnViews();

    }

    /**
     * help setting the data associated with Intent as extras to our views
     */
    private void settingInfoOnViews() {

        String rating = intent.getStringExtra(Constants.USER_RATING);
        float fl = Float.valueOf(rating); //turning the string into float to be used in rating bar

        String date = intent.getStringExtra(Constants.RELEASE_DATE);

        mOriginalTitle.setText(intent.getStringExtra(Constants.ORIGINAL_TITLE));
        mUserRating.setText(rating);
        mRatingBar.setRating(fl / 2); //dividing the value of the rate to be the same ratio in 5 star rating bar
        mOverview.setText(intent.getStringExtra(Constants.OVERVIEW));
        mReleaseDate.setText(dateFormat(date));
        bindImage(intent.getStringExtra(Constants.MOVIE_POSTER_PATH));

        mRatingBar.setIsIndicator(true); // set the rating bar as indicator to prevent editing on it

    }


    /**
     * help formatting the date returned for the query to more readable date
     */
    private String dateFormat(String date) {

        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-dd-MM");
        Date myDate = null;
        try {
            myDate = dateFormat.parse(date);

        } catch (ParseException e) {
            e.printStackTrace();
        }

        SimpleDateFormat timeFormat = new SimpleDateFormat("dd/MM/yyyy");

        return timeFormat.format(myDate);
    }

    /**
     * Using the help of picasso library to fetch our images
     */
    private void bindImage(String imgPath) {

        String url = Constants.IMAGE_QUERY_URL + imgPath;

        Picasso.with(this)
                .load(url)
                .placeholder(R.drawable.place_holder)
                .error(R.drawable.error_loading_image)
                .into(mMoviePoster);

    }
}

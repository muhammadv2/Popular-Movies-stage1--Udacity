package com.example.worldwide.popularmovies1;


import android.net.Uri;
import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

final class NetworkUtils {

    private static OkHttpClient okHttp;
    private static Request request;
    private static Response response;

    private NetworkUtils() {
        // creating a private constructor because no one should instantiate this class
    }

    /**
     * fetchData method take the url as a String @param howToSort and do the http connection on it
     * after creating it as a URL and then read the respond and pass it to Movies list after
     * handling the Json and then
     *
     * @return List<Movie>
     */
    static List<Movie> fetchData(String howToSort) {

        URL url = null;
        try {
            url = getApiUrl(howToSort);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        if (url != null) {
            okHttp = new OkHttpClient();
            request = new Request.Builder().url(url).build();
        }
        String responseBody = null;
        try {
            response = okHttp.newCall(request).execute();
            if (response.isSuccessful() && response.body() != null) { //to check if the response returned successfully or not
                responseBody = response.body().string();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            response.body().close();// to release all resource held by the response
        }

        return handleJson(responseBody);

    }

    /**
     * build the URL and ducking the exception to be caught when called
     */
    private static URL getApiUrl(String howToSort) throws MalformedURLException {

        Uri builtUri = Uri.parse(Constants.BASE_QUERY_URL).buildUpon()
                .appendPath(howToSort)
                .appendQueryParameter("api_key", Constants.API_KEY)
                .build();

        return new URL(builtUri.toString());
    }

    /**
     * The method response of handling the json returned from the request and extracting all the
     * data needed and creating an Movie array with it and then return it
     */
    private static List<Movie> handleJson(String response) {

        if (TextUtils.isEmpty(response)) {
            return null;
        }

        List<Movie> movies = new ArrayList<>();

        try {
            JSONObject baseJson = new JSONObject(response);
            JSONArray resultArray = baseJson.getJSONArray(Constants.RESULT_TAG);

            for (int i = 0; i < resultArray.length(); i++) {

                JSONObject singleMovieObject = resultArray.getJSONObject(i);

                String originalTitle = singleMovieObject.optString(Constants.ORIGINAL_TITLE_TAG);
                String userRating = singleMovieObject.optString(Constants.VOTE_AVERAGE_TAG);
                String releaseDate = singleMovieObject.optString(Constants.RELEASE_DATE_TAG);
                String overview = singleMovieObject.optString(Constants.OVERVIEW_TAG);
                String posterPath = singleMovieObject.optString(Constants.POSTER_PATH_TAG);

                movies.add(new Movie(originalTitle, userRating, releaseDate, overview, posterPath));

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return movies;
    }
}

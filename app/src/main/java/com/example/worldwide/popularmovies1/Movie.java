package com.example.worldwide.popularmovies1;



//Todo make it extend parcable
class Movie {

    private final String posterPath;
    private final String originalTitle;
    private final String userRating;
    private final String releaseDate;
    private final String overView;


    Movie(String originalTitle, String userRating, String releaseDate, String overView, String posterPath) {

        this.posterPath = posterPath;
        this.originalTitle = originalTitle;
        this.userRating = userRating;
        this.releaseDate = releaseDate;
        this.overView = overView;

    }


    String getPosterPath() {
        return posterPath;
    }

    String getOriginalTitle() {
        return originalTitle;
    }

    String getUserRating() {
        return userRating;
    }

    String getReleaseDate() {
        return releaseDate;
    }

    String getOverView() {
        return overView;
    }

}

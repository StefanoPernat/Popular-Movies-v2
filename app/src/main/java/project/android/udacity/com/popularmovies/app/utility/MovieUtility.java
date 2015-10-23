package project.android.udacity.com.popularmovies.app.utility;

import android.content.Intent;

import java.util.ArrayList;

import project.android.udacity.com.popularmovies.app.model.Movie;
import project.android.udacity.com.popularmovies.app.model.Trailer;

/**
 * Created by stefanopernat on 22/10/15.
 */
public class MovieUtility {
    private static final String SHARE_STRING = "Hey watch this awesome trailer";
    private static final String SHARE_HASHTAG = "#PopularMovies";
    private static final String YOUTUBE_BASE_URL = "http://www.youtube.com/watch?v=";

    public static Intent createShareTrailerIntent(Movie selectedMovie){
        if(selectedMovie != null) {
            ArrayList<Trailer> trailers = selectedMovie.getTrailers();
            if(trailers.size() > 0){
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_TEXT, SHARE_STRING + " " + YOUTUBE_BASE_URL + trailers.get(0).getKey() + " " + SHARE_HASHTAG);
                return shareIntent;
            }
        }

        return null;
    }


}

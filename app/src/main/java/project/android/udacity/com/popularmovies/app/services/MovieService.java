package project.android.udacity.com.popularmovies.app.services;

import android.app.IntentService;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import project.android.udacity.com.popularmovies.app.R;
import project.android.udacity.com.popularmovies.app.model.Movie;
import project.android.udacity.com.popularmovies.app.receivers.DownloadReceiver;

/**
 * Created by stefanopernat on 14/09/15.
 *
 * MovieService is an Intent Service that make an HTTP call to moviedb API to get
 * data about Movies
 */
public class MovieService extends IntentService {
    private final String LOG_TAG = MovieService.class.getSimpleName();

    //base url
    private final String MOVIES_BASE_URL =
            "http://api.themoviedb.org/3/discover/movie?";

    private final String JSON_ARRAY = "results";
    private final String JSON_ID = "id";
    private final String JSON_LANGUAGE = "original_language";
    private final String JSON_PLOT = "overview";
    private final String JSON_RELEASE_DATE = "release_date";
    private final String JSON_POSTER = "poster_path";
    private final String JSON_POPULARITY = "popularity";
    private final String JSON_TITLE = "title";
    private final String JSON_VOTE_AVERAGE = "vote_average";
    private final String JSON_BACKDROP = "backdrop_path";

    //possible status
    public static final int STATUS_RUNNING = 1;
    public static final int STATUS_FINISHED = 2;
    public static final int STATUS_ERROR = -1;

    public MovieService(){
        super(MovieService.class.getCanonicalName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.e(LOG_TAG, "Service started....");
        /*String jsonResult = getMovieJson(R.string.api_key);
        Log.e(LOG_TAG, jsonResult);
        List<Movie> temp = parseJsonResult(jsonResult);
        Log.e(LOG_TAG, ""+temp.size());*/

        final ResultReceiver receiver = intent.getParcelableExtra(DownloadReceiver.MOVIES_EXTRA);
        final String selectedOrder = intent.getStringExtra(getString(R.string.pref_order_key));

        Bundle bundle = new Bundle();

        receiver.send(STATUS_RUNNING,Bundle.EMPTY);

        String jsonString = getMovieJson(R.string.api_key, selectedOrder);
        ArrayList<Movie> movies = parseJsonResult(jsonString);
        if(movies == null || movies.size() == 0){
            receiver.send(STATUS_ERROR, Bundle.EMPTY);
        }
        else {
            bundle.putParcelableArrayList(Intent.EXTRA_TEXT, movies);
            receiver.send(STATUS_FINISHED, bundle);
        }

        this.stopSelf();
    }

    private String getMovieJson(int apiKeyResource, String order){
        String apiKey = getString(apiKeyResource);
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        Uri builtUri = Uri.parse(MOVIES_BASE_URL).buildUpon()
                        .appendQueryParameter("api_key",apiKey)
                        .appendQueryParameter("sort_by",order)
                        .appendQueryParameter("vote_count.gte","500").build();

        //Log.e(LOG_TAG, builtUri.toString());

        try {
            URL moviesUrl = new URL(builtUri.toString());

            urlConnection = (HttpURLConnection) moviesUrl.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();

            if(inputStream == null){
                return null;
            }

            reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = reader.readLine()) != null){
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0){
                return null;
            }

            return buffer.toString();

        }
        catch (IOException e){
            //Log.e(LOG_TAG, e.getMessage(), e);
        }
        finally {
            if (urlConnection != null){
                urlConnection.disconnect();
            }

            if(reader != null){
                try {
                    reader.close();
                }
                catch (IOException e){
                    //Log.e(LOG_TAG, e.getMessage(), e);
                }

            }
        }


        return builtUri.toString();
    }


    private ArrayList<Movie> parseJsonResult(String jsonString){
        ArrayList<Movie> movies = new ArrayList<>();
        JSONObject jsonRoot = null;

        try{
            jsonRoot = new JSONObject(jsonString);
            JSONArray jsonMoviesArray = jsonRoot.getJSONArray(JSON_ARRAY);
            for (int k = 0; k < jsonMoviesArray.length(); k++){
                int id;
                double voteAverage, popularity;
                String language, plot, releaseDate, poster, title, backdrop;

                JSONObject jsonMovie = jsonMoviesArray.getJSONObject(k);

                id = jsonMovie.getInt(JSON_ID);
                voteAverage = jsonMovie.getDouble(JSON_VOTE_AVERAGE);
                popularity = jsonMovie.getDouble(JSON_POPULARITY);
                language = jsonMovie.getString(JSON_LANGUAGE);
                plot = jsonMovie.getString(JSON_PLOT);
                releaseDate = jsonMovie.getString(JSON_RELEASE_DATE);
                poster = jsonMovie.getString(JSON_POSTER).replace("/","");
                title = jsonMovie.getString(JSON_TITLE);
                backdrop = jsonMovie.getString(JSON_BACKDROP).replace("/","");

                Movie movie = new Movie(
                        id,
                        language,
                        plot,
                        releaseDate,
                        popularity,
                        voteAverage,
                        backdrop,
                        poster,
                        title
                );

                //Log.e(LOG_TAG, movie.toString());
                movies.add(movie);
            }
        }
        catch (JSONException e){
            //Log.e(LOG_TAG, e.getMessage(), e);
        }

        return movies;

    }
}

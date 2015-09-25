package project.android.udacity.com.popularmovies.app.task;

import android.app.Activity;
import android.app.ProgressDialog;
import android.net.Uri;
import android.os.AsyncTask;
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
import java.util.ArrayList;

import project.android.udacity.com.popularmovies.app.model.Movie;

/**
 * Created by stefanopernat on 25/09/15.
 */
public class FetchMovieTask extends AsyncTask<String, Void, ArrayList<Movie>> {
    private final String LOG_TAG = FetchMovieTask.class.getSimpleName();

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


    @Override
    protected ArrayList<Movie> doInBackground(String... params) {
        final String apiKey = ((params == null || params.length != 2) ? "" : params[0]);
        final String selectedOrder = ((params == null || params.length != 2) ? "" : params[1]);

        if(apiKey == "" || selectedOrder == ""){
            return null;
        }

        //Log.e(LOG_TAG, apiKey);

        String jsonString = getMovieJson(apiKey,selectedOrder);
        return parseJsonResult(jsonString);
    }

    private String getMovieJson(String apiKeyResource, String order){
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        Uri builtUri = Uri.parse(MOVIES_BASE_URL).buildUpon()
                .appendQueryParameter("api_key",apiKeyResource)
                .appendQueryParameter("sort_by",order)
                .appendQueryParameter("vote_count.gte","500").build();

        Log.e(LOG_TAG, builtUri.toString());

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
            Log.e(LOG_TAG, e.getMessage(), e);
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
                    Log.e(LOG_TAG, e.getMessage(), e);
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
            Log.e(LOG_TAG, e.getMessage(), e);
        }

        return movies;

    }
}

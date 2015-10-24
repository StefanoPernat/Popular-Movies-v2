package project.android.udacity.com.popularmovies.app.task;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import project.android.udacity.com.popularmovies.app.model.Trailer;

/**
 * Created by stefanopernat on 17/10/15.
 */
public class FetchMovieTrailersTask extends AsyncTask<String, Void, ArrayList<Trailer>> {
    private final static String LOG_TAG = FetchMovieTrailersTask.class.getSimpleName();

    private static final String MOVIE_TRAILER_BASE_URL =
            "http://api.themoviedb.org/3/movie/";

    private static final String JSON_YOUTUBE_ARRAY = "youtube";
    private static final String JSON_TRAILER_KEY = "source";
    private static final String JSON_TRAILER_NAME = "name";


    @Override
    protected ArrayList<Trailer> doInBackground(String... params) {
        if(params.length != 2){
            return new ArrayList<>();
        }

        String apiKey = params[0];
        String movieId = params[1];

        String jsonResult = getJsonResult(apiKey,movieId);
        ArrayList<Trailer> result = parseJsonResult(Long.parseLong(movieId), jsonResult);

        return result;
    }

    private String getJsonResult(String apiKey, String movieId){
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        StringBuffer buffer = new StringBuffer();

        Uri builtUri = Uri.parse(MOVIE_TRAILER_BASE_URL).buildUpon()
                        .appendPath(movieId)
                        .appendPath("trailers")
                        .appendQueryParameter("api_key", apiKey).build();
        Log.e(LOG_TAG, builtUri.toString());

        try {
            URL trailersUrl = new URL(builtUri.toString());

            urlConnection = (HttpURLConnection) trailersUrl.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            InputStream inputStream = urlConnection.getInputStream();

            if(inputStream == null){
                return null;
            }

            reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = reader.readLine()) != null){
                buffer.append(line + "\n");
            }

            if(buffer.length() == 0){
                return null;
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        finally {
            if(urlConnection != null){
                urlConnection.disconnect();
            }

            if(reader != null){
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }


        return buffer.toString();
    }

    private ArrayList<Trailer> parseJsonResult(long movieId, String jsonString){
        ArrayList<Trailer> result = new ArrayList<>();
        JSONObject jsonRoot = null;

        try {
            jsonRoot = new JSONObject(jsonString);
            JSONArray jsonTrailersArray = jsonRoot.getJSONArray(JSON_YOUTUBE_ARRAY);
            for (int k=0; k<jsonTrailersArray.length(); k++){
                String key, title;

                JSONObject jsonTrailer = jsonTrailersArray.getJSONObject(k);
                key = jsonTrailer.getString(JSON_TRAILER_KEY);
                title = jsonTrailer.getString(JSON_TRAILER_NAME);

                Trailer t = new Trailer(movieId,key,title);
                result.add(t);
                //Log.e(LOG_TAG, t.toString());
            }
        } catch (Exception e){
            result = new ArrayList<>();
            Log.e(LOG_TAG,e.getMessage(),e);
        }

        return result;
    }
}

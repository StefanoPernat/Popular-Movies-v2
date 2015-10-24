package project.android.udacity.com.popularmovies.app.task;

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

import project.android.udacity.com.popularmovies.app.model.Review;

/**
 * Created by stefanopernat on 18/10/15.
 */
public class FetchMovieReviewsTask extends AsyncTask<String,Void,ArrayList<Review>> {
    private static final String LOG_TAG = FetchMovieReviewsTask.class.getSimpleName();

    private static final String MOVIE_REVIEWS_BASE_URL =
            "http://api.themoviedb.org/3/movie/";

    private static final String JSON_RESULT_ARRAY = "results";
    private static final String JSON_REVIEW_ID = "id";
    private static final String JSON_REVIEW_AUTHOR = "author";
    private static final String JSON_REVIEW_CONTENT = "content";

    @Override
    protected ArrayList<Review> doInBackground(String... params) {
        if(params.length != 2){
            return new ArrayList<>();
        }

        String apiKey = params[0];
        String movieId = params[1];

        String json = getJsonResult(apiKey, movieId);
        ArrayList<Review> reviews = parseJsonResult(Long.parseLong(movieId),json);

        return reviews;
    }

    private String getJsonResult(String apiKey, String movieId){
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        StringBuffer buffer = new StringBuffer();

        Uri builtUri = Uri.parse(MOVIE_REVIEWS_BASE_URL).buildUpon()
                .appendPath(movieId)
                .appendPath("reviews")
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

    private ArrayList<Review> parseJsonResult (long movieId, String jsonString){
        ArrayList<Review> result = new ArrayList<>();
        JSONObject jsonRoot = null;

        try {
                jsonRoot = new JSONObject(jsonString);
                JSONArray jsonReviewsArray = jsonRoot.getJSONArray(JSON_RESULT_ARRAY);
                for (int k=0; k < jsonReviewsArray.length(); k++){
                    JSONObject jsonReview = jsonReviewsArray.getJSONObject(k);
                    String id, author, content;

                    id = jsonReview.getString(JSON_REVIEW_ID);
                    author = jsonReview.getString(JSON_REVIEW_AUTHOR);
                    content = jsonReview.getString(JSON_REVIEW_CONTENT);

                    Review review = new Review(id,movieId,author,content);
                    Log.e(LOG_TAG, review.toString());
                    result.add(review);
            }

        } catch (JSONException e){
            Log.e(LOG_TAG, e.getMessage(),e);
            result = new ArrayList<>();
        }

        return result;
    }
}

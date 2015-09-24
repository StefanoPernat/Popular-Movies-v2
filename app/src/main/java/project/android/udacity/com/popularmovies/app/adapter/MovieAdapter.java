package project.android.udacity.com.popularmovies.app.adapter;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

import project.android.udacity.com.popularmovies.app.R;
import project.android.udacity.com.popularmovies.app.model.Movie;

/**
 * Created by stefanopernat on 16/09/15.
 *
 * Custom Array adapter for Movies
 */
public class MovieAdapter extends ArrayAdapter<Movie> {

    private final String LOG_TAG = MovieAdapter.class.getSimpleName();
    private final String BASE_IMAGE_URL = "http://image.tmdb.org/t/p/";

    private Uri mPosterUri;

    public MovieAdapter(Context context, List<Movie> movieList) {
        super(context,0,movieList);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Movie movie = getItem(position);

        mPosterUri = Uri.parse(BASE_IMAGE_URL).buildUpon()
                    .appendPath("w185")
                    .appendPath(movie.getPoster()).build();

        //Log.e(LOG_TAG, mPosterUri.toString());

        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.movie_item,parent, false);
        }

        ImageView posterImage = (ImageView) convertView.findViewById(R.id.movie_item);
        posterImage.setScaleType(ImageView.ScaleType.FIT_XY);
        //posterImage.setPadding(0,0,0,0);
        Picasso.with(getContext()).load(mPosterUri).into(posterImage);

        return convertView;
    }
}

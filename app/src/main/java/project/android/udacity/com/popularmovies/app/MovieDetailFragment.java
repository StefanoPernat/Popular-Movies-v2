package project.android.udacity.com.popularmovies.app;

import android.content.ContentProviderOperation;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.RemoteException;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import project.android.udacity.com.popularmovies.app.data.FavoriteMoviesColumns;
import project.android.udacity.com.popularmovies.app.data.MovieProvider;
import project.android.udacity.com.popularmovies.app.model.Movie;

/**
 * A placeholder fragment containing a simple view.
 */
public class MovieDetailFragment extends Fragment {

    private final String LOG_TAG = MovieDetailFragment.class.getSimpleName();
    private final String MOVIE_BACKDROP_BASE_URI = "http://image.tmdb.org/t/p/";
    private Movie selectedMovie = null;

    public MovieDetailFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_movie_detail, container, false);

        Intent intent = getActivity().getIntent();
        if(intent != null && intent.hasExtra(Intent.EXTRA_TEXT)){
            selectedMovie = intent.getParcelableExtra(Intent.EXTRA_TEXT);
        }
        else {
            Bundle arguments = getArguments();
            selectedMovie = arguments.getParcelable(MainActivityFragment.MOVIE_BUNDLE_KEY);
        }

        //Toast.makeText(getActivity(), selectedMovie.toString(), Toast.LENGTH_LONG).show();

        ImageView backdropImageView = (ImageView) rootView.findViewById(R.id.backdrop_imageview);
        Picasso.with(getActivity()).load(buildBackdropPath()).into(backdropImageView);
        backdropImageView.setScaleType(ImageView.ScaleType.FIT_XY);

        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            Picasso.with(getActivity()).load(buildBackdropPathForLandscape()).into(backdropImageView);
            backdropImageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        }

        /*
            Picasso.with(getActivity()).load(buildBackdropPath()).into(backdropImageView);
            backdropImageView.setScaleType(ImageView.ScaleType.FIT_XY);
        }
        else {
            Picasso.with(getActivity()).load(buildPosterPath()).into(backdropImageView);
        }*/

        if(selectedMovie != null) {
            TextView titleTextView = (TextView) rootView.findViewById(R.id.title_textView);
            titleTextView.setText(selectedMovie.getTitle());

            TextView plotTextView = (TextView) rootView.findViewById(R.id.plot_textview);
            plotTextView.setText(selectedMovie.getPlot());

            TextView releaseDateTextView = (TextView) rootView.findViewById(R.id.release_date_textview);
            releaseDateTextView.setText(selectedMovie.getReleaseDate());

            TextView voteAverageTextView = (TextView) rootView.findViewById(R.id.vote_average_textView);
            voteAverageTextView.setText(selectedMovie.getVoteAverage() + "/10");

            /*Button button = (Button) rootView.findViewById(R.id.button_content);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getActivity(),"Content Provider test",Toast.LENGTH_SHORT).show();
                    saveAsFavorite(selectedMovie);
                }
            });*/

            String backdrop_path = buildBackdropPath();
            Log.e(LOG_TAG, backdrop_path);
        }

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();



        //ImageView backdropImageView = (ImageView) rootView.findViewById(R.id.backdrop_imageview);

        //Picasso.with(getActivity()).load(buildBackdropPath()).into(backdropImageView);
    }

    private String buildBackdropPath(){
        if(selectedMovie != null) {
            Uri builtUri = Uri.parse(MOVIE_BACKDROP_BASE_URI).buildUpon()
                    .appendPath("w500")
                    .appendPath(selectedMovie.getBackDrop()).build();
            return builtUri.toString();
        }

        return null;
    }

    private String buildBackdropPathForLandscape(){
        if(selectedMovie != null) {
            Uri builtUri = Uri.parse(MOVIE_BACKDROP_BASE_URI).buildUpon()
                    .appendPath("w500")
                    .appendPath(selectedMovie.getPoster()).build();
            return builtUri.toString();
        }

        return null;
    }

    private void saveAsFavorite(Movie movie){
        ArrayList<ContentProviderOperation> batchOperation = new ArrayList<>();

        ContentProviderOperation.Builder builder = ContentProviderOperation.newInsert(MovieProvider.Favorites.CONTENT_URI);
        builder.withValue(FavoriteMoviesColumns._ID,movie.getId());
        builder.withValue(FavoriteMoviesColumns.BACKDROP, movie.getBackDrop());
        builder.withValue(FavoriteMoviesColumns.LANGUAGE,movie.getLanguage());
        builder.withValue(FavoriteMoviesColumns.PLOT,movie.getPlot());
        builder.withValue(FavoriteMoviesColumns.POPULARITY,movie.getPopularity());
        builder.withValue(FavoriteMoviesColumns.POSTER,movie.getPoster());
        builder.withValue(FavoriteMoviesColumns.RELEASE_DATE,movie.getReleaseDate());
        builder.withValue(FavoriteMoviesColumns.TITLE,movie.getTitle());
        builder.withValue(FavoriteMoviesColumns.VOTE_AVERAGE,movie.getVoteAverage());

        batchOperation.add(builder.build());

        try
        {
            getActivity().getContentResolver().applyBatch(MovieProvider.AUTHORITY,batchOperation);
        }
        catch (RemoteException | OperationApplicationException e) {
            e.printStackTrace();
        }
    }
}

package project.android.udacity.com.popularmovies.app;

import android.content.ContentProviderOperation;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.content.res.Configuration;
import android.database.Cursor;
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

import com.github.pavlospt.CircleView;
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
            backdropImageView.setScaleType(ImageView.ScaleType.FIT_XY);
        }

        if(selectedMovie != null) {

            final ImageView imageview_favs = (ImageView) rootView.findViewById(R.id.imageview_favorite);
            if(checkIfFavorite(selectedMovie.getId())){
                imageview_favs.setImageResource(R.drawable.fav_yes);
            }
            else {
                imageview_favs.setImageResource(R.drawable.no_fav);
            }

            imageview_favs.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(!checkIfFavorite(selectedMovie.getId())){
                        saveAsFavorite(selectedMovie);
                        imageview_favs.setImageResource(R.drawable.fav_yes);
                    }
                    else {
                        int deleted  = deleteFromFavorite(selectedMovie.getId());
                        if(deleted > 0){
                            imageview_favs.setImageResource(R.drawable.no_fav);
                        }
                        else {
                            imageview_favs.setImageResource(R.drawable.fav_yes);
                        }
                    }
                }
            });


            Log.e(LOG_TAG,""+checkIfFavorite(selectedMovie.getId()));


            TextView titleTextView = (TextView) rootView.findViewById(R.id.title_textView);
            titleTextView.setText(selectedMovie.getTitle());

            TextView plotTextView = (TextView) rootView.findViewById(R.id.plot_textview);
            plotTextView.setText(selectedMovie.getPlot());

            TextView releaseDateTextView = (TextView) rootView.findViewById(R.id.release_date_textview);
            releaseDateTextView.setText(selectedMovie.getReleaseDate());

            CircleView circleViewVoteAverage = (CircleView) rootView.findViewById(R.id.vote_average_view);
            circleViewVoteAverage.setTitleText(String.valueOf(selectedMovie.getVoteAverage()));

            /*TextView voteAverageTextView = (TextView) rootView.findViewById(R.id.vote_average_textView);
            voteAverageTextView.setText(selectedMovie.getVoteAverage() + "/10");*/

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

    private boolean checkIfFavorite(long id){
        Cursor cursor = getActivity().getContentResolver().query(
                            MovieProvider.Favorites.CONTENT_URI,
                            null,
                            FavoriteMoviesColumns._ID + " = "+id,
                            null,
                            null);
        try {
            if(cursor.getCount() > 0){
                cursor.close();
                return true;
            }
            else {
                cursor.close();
                return false;
            }
        }
        catch (Exception e){
            cursor.close();
            return false;
        }
    }

    private int deleteFromFavorite(long id){
        return getActivity().getContentResolver().delete(
                MovieProvider.Favorites.CONTENT_URI,
                FavoriteMoviesColumns._ID +" = "+id,
                null
        );
    }
}

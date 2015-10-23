package project.android.udacity.com.popularmovies.app;

import android.app.FragmentTransaction;
import android.content.ContentProviderOperation;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.os.RemoteException;
import android.app.Fragment;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.pavlospt.CircleView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import project.android.udacity.com.popularmovies.app.data.FavoriteMoviesColumns;
import project.android.udacity.com.popularmovies.app.data.FavoriteMoviesReviewsColumns;
import project.android.udacity.com.popularmovies.app.data.FavoriteMoviesTrailersColumns;
import project.android.udacity.com.popularmovies.app.data.MovieProvider;
import project.android.udacity.com.popularmovies.app.model.Movie;
import project.android.udacity.com.popularmovies.app.model.Review;
import project.android.udacity.com.popularmovies.app.model.Trailer;
import project.android.udacity.com.popularmovies.app.utility.MovieUtility;

/**
 * A placeholder fragment containing a simple view.
 */
public class MovieDetailFragment extends Fragment {

    private final String LOG_TAG = MovieDetailFragment.class.getSimpleName();
    private final String MOVIE_BACKDROP_BASE_URI = "http://image.tmdb.org/t/p/";

    private final String SELECTED_MOVIE_STATE = "state.detail.selected.movie";

    private Movie mSelectedMovie = null;

    public MovieDetailFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_movie_detail, container, false);

        setHasOptionsMenu(true);

        Intent intent = getActivity().getIntent();
        if(intent != null && intent.hasExtra(Intent.EXTRA_TEXT)){
            mSelectedMovie = intent.getParcelableExtra(Intent.EXTRA_TEXT);
        }
        else {
            Bundle arguments = getArguments();
            mSelectedMovie = arguments.getParcelable(MainActivityFragment.MOVIE_BUNDLE_KEY);
        }

        Log.e(LOG_TAG, mSelectedMovie.toString());

        //Toast.makeText(getActivity(), selectedMovie.toString(), Toast.LENGTH_LONG).show();

        ImageView backdropImageView = (ImageView) rootView.findViewById(R.id.backdrop_imageview);
        Picasso.with(getActivity()).load(buildBackdropPath()).into(backdropImageView);
        backdropImageView.setScaleType(ImageView.ScaleType.FIT_XY);

        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            Picasso.with(getActivity()).load(buildBackdropPathForLandscape()).into(backdropImageView);
            backdropImageView.setScaleType(ImageView.ScaleType.FIT_XY);
        }

        if(mSelectedMovie != null) {

            final ImageView imageview_favs = (ImageView) rootView.findViewById(R.id.imageview_favorite);
            if(checkIfFavorite(mSelectedMovie.getId())){
                imageview_favs.setImageResource(R.drawable.fav_yes);
            }
            else {
                imageview_favs.setImageResource(R.drawable.no_fav);
            }

            imageview_favs.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(!checkIfFavorite(mSelectedMovie.getId())){
                        setFavoriteMovie(mSelectedMovie);
                        imageview_favs.setImageResource(R.drawable.fav_yes);
                        Toast.makeText(getActivity(), getString(R.string.favorite_yes_message),Toast.LENGTH_SHORT).show();
                        if(getActivity().findViewById(R.id.fragment_detail) != null){
                            Fragment mainFragment = getActivity().getFragmentManager().findFragmentById(R.id.fragment);
                            if(mainFragment != null){
                                FragmentTransaction ft  = getFragmentManager().beginTransaction();
                                ft.detach(mainFragment);
                                ft.attach(mainFragment);
                                ft.commit();
                            }

                        }
                    }
                    else {
                        boolean deleted  = deleteFavorite(mSelectedMovie.getId());
                        if(deleted){
                            imageview_favs.setImageResource(R.drawable.no_fav);
                            Toast.makeText(getActivity(), getString(R.string.favorite_no_message),Toast.LENGTH_SHORT).show();
                            if(getActivity().findViewById(R.id.fragment_detail) != null){
                                Fragment mainFragment = getActivity().getFragmentManager().findFragmentById(R.id.fragment);
                                if(mainFragment != null){
                                    FragmentTransaction ft  = getFragmentManager().beginTransaction();
                                    ft.detach(mainFragment);
                                    ft.attach(mainFragment);
                                    ft.commit();
                                }

                            }
                        }
                        else {
                            imageview_favs.setImageResource(R.drawable.fav_yes);
                        }
                    }
                }
            });


            //Log.e(LOG_TAG,""+checkIfFavorite(selectedMovie.getId()));


            TextView titleTextView = (TextView) rootView.findViewById(R.id.title_textView);
            titleTextView.setText(mSelectedMovie.getTitle());

            TextView plotTextView = (TextView) rootView.findViewById(R.id.plot_textview);
            plotTextView.setText(mSelectedMovie.getPlot());

            TextView releaseDateTextView = (TextView) rootView.findViewById(R.id.release_date_textview);
            releaseDateTextView.setText(mSelectedMovie.getReleaseDate());

            CircleView circleViewVoteAverage = (CircleView) rootView.findViewById(R.id.vote_average_view);
            circleViewVoteAverage.setTitleText(String.valueOf(mSelectedMovie.getVoteAverage()));

            /*TextView voteAverageTextView = (TextView) rootView.findViewById(R.id.vote_average_textView);
            voteAverageTextView.setText(selectedMovie.getVoteAverage() + "/10");*/

            //String backdrop_path = buildBackdropPath();
            //Log.e(LOG_TAG, backdrop_path);

            displayTrailer(mSelectedMovie.getTrailers(),rootView);
            displayReviews(mSelectedMovie.getReviews(),rootView);
        }

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();



        //ImageView backdropImageView = (ImageView) rootView.findViewById(R.id.backdrop_imageview);

        //Picasso.with(getActivity()).load(buildBackdropPath()).into(backdropImageView);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        if(getActivity().findViewById(R.id.fragment_detail) == null){
            inflater.inflate(R.menu.detail_fragment, menu);
            MenuItem menuItem = menu.findItem(R.id.action_share);

            if (menuItem != null) {
                ShareActionProvider shareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);
                Intent shareIntent = MovieUtility.createShareTrailerIntent(mSelectedMovie);

                if (shareIntent != null) {
                    shareActionProvider.setShareIntent(shareIntent);
                }

            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(SELECTED_MOVIE_STATE, mSelectedMovie);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if(savedInstanceState != null){
            mSelectedMovie = savedInstanceState.getParcelable(SELECTED_MOVIE_STATE);
        }
    }

    private String buildBackdropPath(){
        if(mSelectedMovie != null) {
            Uri builtUri = Uri.parse(MOVIE_BACKDROP_BASE_URI).buildUpon()
                    .appendPath("w500")
                    .appendPath(mSelectedMovie.getBackDrop()).build();
            return builtUri.toString();
        }

        return null;
    }

    private String buildBackdropPathForLandscape(){
        if(mSelectedMovie != null) {
            Uri builtUri = Uri.parse(MOVIE_BACKDROP_BASE_URI).buildUpon()
                    .appendPath("w500")
                    .appendPath(mSelectedMovie.getPoster()).build();
            return builtUri.toString();
        }

        return null;
    }

    private void setFavoriteMovie(Movie movie){
        saveAsFavorite(movie);

        ArrayList<Trailer> trailers = movie.getTrailers();
        if(trailers.size() > 0){
            saveTrailers(trailers);
        }

        ArrayList<Review> reviews = movie.getReviews();
        if(reviews.size() > 0){
            saveReviews(reviews);
        }
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
        builder.withValue(FavoriteMoviesColumns.TITLE, movie.getTitle());
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

    private void saveTrailers(ArrayList<Trailer> trailers){
        ArrayList<ContentProviderOperation> batchOperation = new ArrayList<>();

        for (Trailer trailer: trailers){
            ContentProviderOperation.Builder builder = ContentProviderOperation.newInsert(MovieProvider.Trailers.CONTENT_URI);
            builder.withValue(FavoriteMoviesTrailersColumns.MOVIE_ID, trailer.getMovieId());
            builder.withValue(FavoriteMoviesTrailersColumns.KEY, trailer.getKey());
            builder.withValue(FavoriteMoviesTrailersColumns.NAME, trailer.getName());

            batchOperation.add(builder.build());
        }

        try{
            getActivity().getContentResolver().applyBatch(MovieProvider.AUTHORITY,batchOperation);
        }
        catch (RemoteException | OperationApplicationException e){
            e.printStackTrace();
        }

    }

    private void saveReviews(ArrayList<Review> reviews){
        ArrayList<ContentProviderOperation> batchOperation = new ArrayList<>();

        for (Review review: reviews){
            ContentProviderOperation.Builder builder = ContentProviderOperation.newInsert(MovieProvider.Reviews.CONTENT_URI);
            builder.withValue(FavoriteMoviesReviewsColumns._ID, review.getId());
            builder.withValue(FavoriteMoviesReviewsColumns.MOVIE_ID, review.getMovieId());
            builder.withValue(FavoriteMoviesReviewsColumns.AUTHOR, review.getAuthor());
            builder.withValue(FavoriteMoviesReviewsColumns.CONTENT, review.getContent());

            batchOperation.add(builder.build());
        }

        try{
            getActivity().getContentResolver().applyBatch(MovieProvider.AUTHORITY,batchOperation);
        }
        catch (RemoteException | OperationApplicationException e){
            Log.e(LOG_TAG, "exception");
            e.printStackTrace();
        }

        //Log.e(LOG_TAG, "fine");
    }

    private boolean checkIfFavorite(long id){
        Cursor cursor = getActivity().getContentResolver().query(
                MovieProvider.Favorites.CONTENT_URI,
                null,
                FavoriteMoviesColumns._ID + " = " + id,
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

    private boolean deleteFavorite(long id){
        int favoriteDeleted = deleteFromFavorite(id);
        if(favoriteDeleted > 0) {
            deleteMovieTrailers(id);
            deleteMovieReviews(id);
        }

        return favoriteDeleted > 0;

    }

    private int deleteFromFavorite(long id){
        return
            getActivity().getContentResolver().delete(
                    MovieProvider.Favorites.CONTENT_URI,
                    FavoriteMoviesColumns._ID + " = " + id,
                    null
            );
    }

    private int deleteMovieTrailers(long id){
        return
            getActivity().getContentResolver().delete(
                    MovieProvider.Trailers.CONTENT_URI,
                    FavoriteMoviesTrailersColumns.MOVIE_ID + " = " + id,
                    null
            );
    }

    private int deleteMovieReviews(long id){
        return
            getActivity().getContentResolver().delete(
                    MovieProvider.Reviews.CONTENT_URI,
                    FavoriteMoviesReviewsColumns.MOVIE_ID + " = " + id,
                    null
            );
    }

    private void displayTrailer(ArrayList<Trailer> trailers, View rootView){
        LinearLayout trailerContainer = (LinearLayout) rootView.findViewById(R.id.trailer_container);
        LayoutInflater inflater = getActivity().getLayoutInflater();

        for (final Trailer trailer : trailers){
            View trailerView = inflater.inflate(R.layout.trailer_item, null);
            TextView trailerName = (TextView) trailerView.findViewById(R.id.trailer_name);
            trailerName.setText(trailer.getName());
            //trailerView.setPadding(0,0,0,2);
            trailerView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    watchYoutubeVideo(trailer.getKey());
                }
            });
            trailerContainer.addView(trailerView);
        }
    }

    private void displayReviews(ArrayList<Review> reviews, View rootView){
        LinearLayout reviewsContainer = (LinearLayout) rootView.findViewById(R.id.reviews_container);
        LayoutInflater inflater = getActivity().getLayoutInflater();

        for (Review review : reviews){
            View reviewView = inflater.inflate(R.layout.review_item, null);

            TextView reviewAuthor = (TextView) reviewView.findViewById(R.id.review_author);
            reviewAuthor.setText(review.getAuthor().toUpperCase());

            TextView reviewContent = (TextView) reviewView.findViewById(R.id.review_content);
            reviewContent.setText(review.getContent());
            //trailerView.setPadding(0,0,0,2);
            reviewsContainer.addView(reviewView);
        }
    }

    private void watchYoutubeVideo(String id){
        try {
            Intent youtubeIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + id));
            startActivity(youtubeIntent);
        } catch (Exception e){
            Log.e(LOG_TAG, e.getMessage(), e);
            Intent youtubeWebIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v="+id));
            startActivity(youtubeWebIntent);
        }
    }

    /*private Intent createShareTrailerIntent(){
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
    }*/
}

package project.android.udacity.com.popularmovies.app;

import android.app.ProgressDialog;
import android.content.ContentProviderOperation;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.os.Handler;
import android.os.RemoteException;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.util.Log;
import android.view.ActionProvider;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import project.android.udacity.com.popularmovies.app.adapter.MovieAdapter;
import project.android.udacity.com.popularmovies.app.data.FavoriteMoviesColumns;
import project.android.udacity.com.popularmovies.app.data.FavoriteMoviesReviewsColumns;
import project.android.udacity.com.popularmovies.app.data.FavoriteMoviesTrailersColumns;
import project.android.udacity.com.popularmovies.app.data.MovieProvider;
import project.android.udacity.com.popularmovies.app.model.Movie;
import project.android.udacity.com.popularmovies.app.model.Review;
import project.android.udacity.com.popularmovies.app.model.Trailer;
import project.android.udacity.com.popularmovies.app.task.FetchMovieReviewsTask;
import project.android.udacity.com.popularmovies.app.task.FetchMovieTask;
import project.android.udacity.com.popularmovies.app.task.FetchMovieTrailersTask;

import android.support.v7.widget.ShareActionProvider;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    public static final String MOVIE_BUNDLE_KEY = "detail.movie.fragment.bundle";

    private final String LOG_TAG = MainActivityFragment.class.getSimpleName();
    private final String STATE_PREFERRED_ORDER = "state.order";
    private final String STATE_MOVIES = "state.movies";
    private final String STATE_MOVIE = "state.selected.movie";

    private final String SHARE_STRING = "Hey watch this awesome trailer";
    private final String SHARE_HASHTAG = "#PopularMovies";
    private final String YOUTUBE_BASE_URL = "http://www.youtube.com/watch?v=";


    private GridView mGridView;
    private TextView mMessageTextView;
    private ArrayList<Movie> mMovies = new ArrayList<>();
    private MovieAdapter mMovieAdapter;
    private Movie mSelectedMovie;
    private ShareActionProvider mShareActionProvider = null;


    private String mSelectedOrder = "";

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        setHasOptionsMenu(true);
        //setRetainInstance(true);
        //getActivity().requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);

        //super.onCreateView(inflater,container,savedInstanceState);

        View viewRoot = inflater.inflate(R.layout.fragment_main, container, false);

        mGridView = (GridView) viewRoot.findViewById(R.id.moviesGridView);
        mMessageTextView = (TextView) viewRoot.findViewById(R.id.message_textview);
        mMessageTextView.setVisibility(View.GONE);

        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                /*
                    Check if there's the fragment or not if not Launch second activity through intent
                    else if there's a fragment I will build the bundle to pass the selected movie to the fragment
                */

                mSelectedMovie = mMovieAdapter.getItem(position);

                ArrayList<Trailer> trailersForSelectedMovie = trailersForMovie(mSelectedMovie.getId());
                if (trailersForSelectedMovie.size() == 0) {
                    try {
                        trailersForSelectedMovie.addAll(new FetchMovieTrailersTask().execute(getString(R.string.api_key), String.valueOf(mSelectedMovie.getId())).get());
                    } catch (InterruptedException | ExecutionException e) {
                        Log.e(LOG_TAG, e.getMessage(), e);
                    }

                }

                ArrayList<Review> reviewsForSelectedMovie = reviewsForMovies(mSelectedMovie.getId());
                if (reviewsForSelectedMovie.size() == 0) {
                    try {
                        reviewsForSelectedMovie.addAll(new FetchMovieReviewsTask().execute(getString(R.string.api_key), String.valueOf(mSelectedMovie.getId())).get());
                    } catch (InterruptedException | ExecutionException e) {
                        Log.e(LOG_TAG, e.getMessage(), e);
                    }

                }

                mSelectedMovie.setTrailers(trailersForSelectedMovie);
                mSelectedMovie.setReviews(reviewsForSelectedMovie);

                if (getActivity().findViewById(R.id.fragment_detail) == null) {
                    Intent movieDetailIntent = new Intent(getActivity(), MovieDetailActivity.class);
                    movieDetailIntent.putExtra(Intent.EXTRA_TEXT, mSelectedMovie);
                    startActivity(movieDetailIntent);
                } else {
                    Toast.makeText(getActivity(), "Hi Master Detail", Toast.LENGTH_LONG).show();
                    Bundle selectedMovieBundle = new Bundle();
                    selectedMovieBundle.putParcelable(MOVIE_BUNDLE_KEY, mSelectedMovie);

                    // Replace the detail fragment
                    MovieDetailFragment movieDetailFragment = new MovieDetailFragment();
                    movieDetailFragment.setArguments(selectedMovieBundle);

                    getActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_detail, movieDetailFragment)
                            .commit();

                    if (mShareActionProvider != null) {

                        Intent shareIntent = createShareTrailerIntent();

                        if (shareIntent != null) {
                            mShareActionProvider.setShareIntent(shareIntent);
                        }

                    }
                }

            }
        });

        return viewRoot;
    }

    /*
        Restore the saved instance

        - restore select order
        - restore movies arraylist
        - restore the selected movie
    */

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if(savedInstanceState != null) {
            mSelectedOrder = savedInstanceState.getString(STATE_PREFERRED_ORDER);
            mMovies = savedInstanceState.getParcelableArrayList(STATE_MOVIES);
            mSelectedMovie = savedInstanceState.getParcelable(STATE_MOVIE);
            if (mMovies == null || mMovies.size() == 0){
                mGridView.setVisibility(View.GONE);
                mMessageTextView.setVisibility(View.VISIBLE);
            }
            else {
                mGridView.setVisibility(View.VISIBLE);
                mMessageTextView.setVisibility(View.GONE);
                mMovieAdapter = new MovieAdapter(getActivity(), mMovies);
                mGridView.setAdapter(mMovieAdapter);
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(STATE_PREFERRED_ORDER, mSelectedOrder);
        outState.putParcelableArrayList(STATE_MOVIES, mMovies);
        outState.putParcelable(STATE_MOVIE, mSelectedMovie);

    }

    @Override
    public void onResume() {
        Log.e(LOG_TAG, "Resume...");
        super.onResume();

        if(getPreferredOrder().equals(getString(R.string.order_fav))){
            mSelectedOrder = getPreferredOrder();
        }

        /*if(mSelectedOrder == null || mSelectedOrder.compareTo("") == 0){
            mSelectedOrder = getPreferredOrder();

        }*/

        Log.e(LOG_TAG, "[START] selectedOrder: " + mSelectedOrder);
        Log.e(LOG_TAG, "[START] preferredOrder: " + getPreferredOrder());
        Log.e(LOG_TAG, "[START] current order = preferred order ==> " + (mSelectedOrder.equals(getPreferredOrder())));

        ArrayList<Movie> movies = new ArrayList<>();
        if(getActivity() != null){
//            Log.e(LOG_TAG, "Ho selezionato preferiti = "+(mSelectedOrder.equals(getString(R.string.order_fav))));
            if(mSelectedOrder.equals(getString(R.string.order_fav)) && mSelectedOrder.equals(getPreferredOrder())){
                movies.clear();
                movies.addAll(getFavoritesArray());
                mMovies.clear();
                mMovies.addAll(movies);
                //mSelectedOrder = getPreferredOrder();
            }
            else {
                if(!mSelectedOrder.equals(getPreferredOrder())  || mMovies.size() == 0){
                    mSelectedOrder = getPreferredOrder();
                    FetchMovieTask movieTask = new FetchMovieTask();
                    movies.clear();
                    try {
                        movies.addAll(movieTask.execute(getString(R.string.api_key), mSelectedOrder).get());
                    }
                    catch (Exception e){
                        e.printStackTrace();
                    }
                    mMovies.clear();
                    mMovies.addAll(movies);
                }
                //mSelectedOrder = getPreferredOrder();
            }

            if(mMovies == null || mMovies.size() == 0){
                mGridView.setVisibility(View.GONE);
                mMessageTextView.setVisibility(View.VISIBLE);
            }
            else {
                mMovieAdapter = new MovieAdapter(getActivity(), mMovies);
                mGridView.setAdapter(mMovieAdapter);
                mGridView.setVisibility(View.VISIBLE);
                mMessageTextView.setVisibility(View.GONE);
            }

            if(mSelectedMovie != null && getActivity().findViewById(R.id.fragment_detail) != null){

                Bundle selectedMovieBundle = new Bundle();
                selectedMovieBundle.putParcelable(MOVIE_BUNDLE_KEY,mSelectedMovie);

                // Replace the detail fragment
                MovieDetailFragment movieDetailFragment = new MovieDetailFragment();
                movieDetailFragment.setArguments(selectedMovieBundle);

                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_detail,movieDetailFragment)
                        .commit();
            }

            Log.e(LOG_TAG, "[STOP] selectedOrder: "+mSelectedOrder);
            Log.e(LOG_TAG, "[STOP] preferredOrder: "+getPreferredOrder());
            Log.e(LOG_TAG, "[STOP] current order = preferred order ==> "+(mSelectedOrder.equals(getPreferredOrder())));

        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        //super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.main_fragment, menu);

        if(getActivity().findViewById(R.id.fragment_detail) == null){
            menu.removeItem(R.id.action_share);
        }

        MenuItem menuItem = menu.findItem(R.id.action_share);
        if(menuItem != null){
            mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);
        }
    }

    public String getPreferredOrder(){
        return
                PreferenceManager.getDefaultSharedPreferences(getActivity())
                .getString(getString(R.string.pref_order_key), getString(R.string.pref_order_default));
    }


    public ArrayList<Movie> getFavoritesArray(){
        ArrayList<Movie> result = new ArrayList<>();

        Cursor cursor = getActivity().getContentResolver().query(
                        MovieProvider.Favorites.CONTENT_URI,
                        null,
                        "",
                        null,
                        ""
        );

        int favorites = cursor.getCount();

        while (cursor.moveToNext()){
            Movie movie = new Movie();
            movie.setId(cursor.getLong(cursor.getColumnIndex(FavoriteMoviesColumns._ID)));
            movie.setBackDrop(cursor.getString(cursor.getColumnIndex(FavoriteMoviesColumns.BACKDROP)));
            movie.setLanguage(cursor.getString(cursor.getColumnIndex(FavoriteMoviesColumns.LANGUAGE)));
            movie.setPlot(cursor.getString(cursor.getColumnIndex(FavoriteMoviesColumns.PLOT)));
            movie.setPopularity(cursor.getDouble(cursor.getColumnIndex(FavoriteMoviesColumns.POPULARITY)));
            movie.setPoster(cursor.getString(cursor.getColumnIndex(FavoriteMoviesColumns.POSTER)));
            movie.setReleaseDate(cursor.getString(cursor.getColumnIndex(FavoriteMoviesColumns.RELEASE_DATE)));
            movie.setTitle(cursor.getString(cursor.getColumnIndex(FavoriteMoviesColumns.TITLE)));
            movie.setVoteAverage(cursor.getDouble(cursor.getColumnIndex(FavoriteMoviesColumns.VOTE_AVERAGE)));

            result.add(movie);
        }

        if(favorites > 0){
            cursor.close();
            return result;
        }
        else {
            cursor.close();
            return new ArrayList<>();
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

    private ArrayList<Trailer> trailersForMovie(long movieId){
        ArrayList<Trailer> result = new ArrayList<>();

        Cursor cursor = getActivity().getContentResolver().query(
                MovieProvider.Trailers.withId(movieId),
                null,
                FavoriteMoviesTrailersColumns.MOVIE_ID +" = "+movieId,
                null,
                null
        );

        while (cursor.moveToNext()){
            Trailer t = new Trailer();
            t.setKey(cursor.getString(cursor.getColumnIndex(FavoriteMoviesTrailersColumns.KEY)));
            t.setName(cursor.getString(cursor.getColumnIndex(FavoriteMoviesTrailersColumns.NAME)));
            t.setMovieId(cursor.getLong(cursor.getColumnIndex(FavoriteMoviesTrailersColumns.MOVIE_ID)));

            Log.e(LOG_TAG, "[TRAILER] "+t.toString());
            result.add(t);
        }

        cursor.close();
        return result;
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

        Log.e(LOG_TAG, "fine");
    }

    private ArrayList<Review> reviewsForMovies(long movieId){
        ArrayList<Review> result = new ArrayList<>();

        Cursor cursor = getActivity().getContentResolver().query(
                MovieProvider.Reviews.withId(movieId),
                null,
                FavoriteMoviesTrailersColumns.MOVIE_ID +" = "+movieId,
                null,
                null
        );

        while (cursor.moveToNext()){
            Review review = new Review();
            review.setId(cursor.getString(cursor.getColumnIndex(FavoriteMoviesReviewsColumns._ID)));
            review.setAuthor(cursor.getString(cursor.getColumnIndex(FavoriteMoviesReviewsColumns.AUTHOR)));
            review.setContent(cursor.getString(cursor.getColumnIndex(FavoriteMoviesReviewsColumns.CONTENT)));
            review.setMovieId(cursor.getLong(cursor.getColumnIndex(FavoriteMoviesReviewsColumns.MOVIE_ID)));

            Log.e(LOG_TAG, "[REVIEWS] " + review.toString());
            result.add(review);
        }

        cursor.close();
        return result;
    }

    private Intent createShareTrailerIntent(){
        if(mSelectedMovie != null) {
            ArrayList<Trailer> trailers = mSelectedMovie.getTrailers();
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

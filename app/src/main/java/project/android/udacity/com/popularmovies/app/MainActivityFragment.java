package project.android.udacity.com.popularmovies.app;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
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
import project.android.udacity.com.popularmovies.app.data.MovieProvider;
import project.android.udacity.com.popularmovies.app.model.Movie;
import project.android.udacity.com.popularmovies.app.task.FetchMovieTask;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    public static final String MOVIE_BUNDLE_KEY = "detail.movie.fragment.bundle";

    private final String LOG_TAG = MainActivityFragment.class.getSimpleName();
    private final String STATE_PREFERRED_ORDER = "state.order";
    private final String STATE_MOVIES = "state.movies";
    private final String STATE_MOVIE = "state.selected.movie";

    private GridView mGridView;
    private TextView mMessageTextView;
    private ArrayList<Movie> mMovies = new ArrayList<>();
    private MovieAdapter mMovieAdapter;
    private Movie mSelectedMovie;


    private String mSelectedOrder;

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

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
                }

            }
        });

        return viewRoot;
    }

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
        super.onResume();

        if(mSelectedOrder == null || mSelectedOrder.compareTo("") == 0){
            mSelectedOrder = getPreferredOrder();

        }

        /*Log.e(LOG_TAG, "selectedOrder: " + mSelectedOrder);
        Log.e(LOG_TAG, "preferredOrder: " + getPreferredOrder());
        Log.e(LOG_TAG, "current order = preferred order ==> " + (mSelectedOrder.equals(getPreferredOrder())));*/

        ArrayList<Movie> movies = new ArrayList<>();
        if(getActivity() != null){
//            Log.e(LOG_TAG, "Ho selezionato preferiti = "+(mSelectedOrder.equals(getString(R.string.order_fav))));
            if(mSelectedOrder.compareTo(getString(R.string.order_fav)) == 0){
                movies.clear();
                movies.addAll(getFavoritesArray());
                mMovies.clear();
                mMovies.addAll(movies);
                //mSelectedOrder = getPreferredOrder();
            }
            else {
                if(mSelectedOrder.compareTo(getPreferredOrder()) == 0  || mMovies.size() == 0){
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

            Log.e(LOG_TAG, "selectedOrder: "+mSelectedOrder);
            Log.e(LOG_TAG, "preferredOrder: "+getPreferredOrder());
            Log.e(LOG_TAG, "current order = preferred order ==> "+(mSelectedOrder.equals(getPreferredOrder())));

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


}

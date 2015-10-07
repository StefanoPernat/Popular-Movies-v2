package project.android.udacity.com.popularmovies.app;

import android.app.ProgressDialog;
import android.content.Intent;
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
    private Movie mSelectedMovie = null;


    private String mSelectedOrder;

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
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
        outState.putString(STATE_PREFERRED_ORDER, mSelectedOrder);
        outState.putParcelableArrayList(STATE_MOVIES, mMovies);
        outState.putParcelable(STATE_MOVIE,mSelectedMovie);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onResume() {
        super.onResume();

        Log.e(LOG_TAG, "selectedOrder: " + mSelectedOrder);
        Log.e(LOG_TAG, "preferredOrder: "+getPreferredOrder());
        Log.e(LOG_TAG, "current order = preferred order ==> "+(mSelectedOrder == getPreferredOrder()));

        if(getActivity() != null){
            if((mSelectedOrder != getPreferredOrder())||(mMovies.size() == 0)){
                mSelectedOrder = getPreferredOrder();
                FetchMovieTask movieTask = new FetchMovieTask();
                try {
                    mMovies.clear();
                    ArrayList<Movie> movies = movieTask.execute(getString(R.string.api_key), mSelectedOrder).get();
                    if(movies == null || movies.size() == 0){
                        mGridView.setVisibility(View.GONE);
                        mMessageTextView.setVisibility(View.VISIBLE);
                    }
                    else {
                        mMovies.addAll(movies);
                        mMovieAdapter = new MovieAdapter(getActivity(), mMovies);
                        mGridView.setAdapter(mMovieAdapter);
                        mGridView.setVisibility(View.VISIBLE);
                        mMessageTextView.setVisibility(View.GONE);

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
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }

            }
            else {
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
            }

            Log.e(LOG_TAG, "selectedOrder: "+mSelectedOrder);
            Log.e(LOG_TAG, "preferredOrder: "+getPreferredOrder());
            Log.e(LOG_TAG, "current order = preferred order ==> "+(mSelectedOrder == getPreferredOrder()));

        }
    }

    public String getPreferredOrder(){
        return
                PreferenceManager.getDefaultSharedPreferences(getActivity())
                .getString(getString(R.string.pref_order_key), getString(R.string.pref_order_default));
    }


}

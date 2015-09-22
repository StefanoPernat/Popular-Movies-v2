package project.android.udacity.com.popularmovies.app;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.os.Bundle;
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

import project.android.udacity.com.popularmovies.app.adapter.MovieAdapter;
import project.android.udacity.com.popularmovies.app.model.Movie;
import project.android.udacity.com.popularmovies.app.receivers.DownloadReceiver;
import project.android.udacity.com.popularmovies.app.services.MovieService;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment implements DownloadReceiver.Receiver{

    private GridView mGridView;
    private TextView mMessageTextView;
    private ArrayAdapter mAdapter;
    private DownloadReceiver mReceiver;
    private ProgressDialog mProgressDialog = null;

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //getActivity().requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);

        View viewRoot = inflater.inflate(R.layout.fragment_main, container, false);

        mGridView = (GridView) viewRoot.findViewById(R.id.moviesGridView);
        mMessageTextView = (TextView) viewRoot.findViewById(R.id.message_textview);
        mMessageTextView.setVisibility(View.GONE);

        return viewRoot;
    }

    @Override
    public void onResume() {
        super.onResume();
        Toast.makeText(getActivity(),"resumed", Toast.LENGTH_SHORT).show();

        mReceiver = new DownloadReceiver(new Handler());
        mReceiver.setReceiver(MainActivityFragment.this);
        Intent movieServiceIntent =
                new Intent(Intent.ACTION_SYNC, null, getActivity(), MovieService.class);
        movieServiceIntent.putExtra(DownloadReceiver.MOVIES_EXTRA, mReceiver);
        movieServiceIntent.putExtra(getString(R.string.pref_order_key), getPreferredOrder());
        getActivity().startService(movieServiceIntent);
    }

    @Override
    public void onReceiveResult(int resultCode, Bundle resultData) {
        /*final ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage(getString(R.string.progressbar_text));
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setIndeterminate(true);*/

        switch (resultCode){
            case MovieService.STATUS_RUNNING: {
                mMessageTextView.setText("");
                mMessageTextView.setVisibility(View.GONE);
                mGridView.setVisibility(View.VISIBLE);


                Toast.makeText(getActivity(),"Running",Toast.LENGTH_SHORT).show();
                mProgressDialog = ProgressDialog.show(
                                    getActivity(),
                                    null,
                                    getString(R.string.progressbar_text),
                                    true
                );
                break;
            }
            case MovieService.STATUS_FINISHED: {
                mMessageTextView.setText("");
                mMessageTextView.setVisibility(View.GONE);
                mGridView.setVisibility(View.VISIBLE);

                Toast.makeText(getActivity(),"Finished",Toast.LENGTH_SHORT).show();
                if(!(mProgressDialog == null)){
                    mProgressDialog.dismiss();
                }
                ArrayList<Movie> movies = resultData.getParcelableArrayList(Intent.EXTRA_TEXT);
                mAdapter = new MovieAdapter(getActivity(),movies);
                //mGridView.setStretchMode(GridView.NO_STRETCH);
                mGridView.setAdapter(mAdapter);

                mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Movie selectedMovie = (Movie) mAdapter.getItem(position);

                        Intent movieDetailIntent = new Intent(getActivity(),MovieDetailActivity.class);
                        movieDetailIntent.putExtra(Intent.EXTRA_TEXT, selectedMovie);
                        startActivity(movieDetailIntent);
                    }
                });
                break;
            }
            case MovieService.STATUS_ERROR: {
                Toast.makeText(getActivity(),"Error",Toast.LENGTH_SHORT).show();
                //progressDialog.dismiss();
                if(!(mProgressDialog == null)){
                    mProgressDialog.dismiss();
                }

                mGridView.setVisibility(View.GONE);

                mMessageTextView.setText(getString(R.string.download_movies_error));
                mMessageTextView.setVisibility(View.VISIBLE);
                mMessageTextView.setGravity(Gravity.CENTER_HORIZONTAL);
                break;
            }
            default:
                break;
        }

    }

    public String getPreferredOrder(){
        return
                PreferenceManager.getDefaultSharedPreferences(getActivity())
                .getString(getString(R.string.pref_order_key), getString(R.string.pref_order_default));
    }
}

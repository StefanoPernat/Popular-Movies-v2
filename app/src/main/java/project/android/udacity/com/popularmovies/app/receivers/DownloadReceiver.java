package project.android.udacity.com.popularmovies.app.receivers;

import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;

/**
 * Created by stefanopernat on 16/09/15.
 *
 * Receiver for the downloaded JSON
 */
public class DownloadReceiver extends ResultReceiver{

    //extras names
    public static final String MOVIES_EXTRA = "moviesReceiver";

    private Receiver mReceiver;

    public DownloadReceiver(Handler handler) {
        super(handler);
    }

    public void setReceiver(Receiver receiver) {
        mReceiver = receiver;
    }

    public interface Receiver {
        public void onReceiveResult(int resultCode, Bundle resultData);

    }

    @Override
    protected void onReceiveResult(int resultCode, Bundle resultData) {
        if(mReceiver != null){
            mReceiver.onReceiveResult(resultCode, resultData);
        }
    }
}

package project.android.udacity.com.popularmovies.app;

import android.os.Bundle;
import android.preference.PreferenceFragment;

/**
 * Fragment for settings activity
 * Created by stefanopernat on 18/09/15.
 */
public class SettingsFragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_general);
    }
}

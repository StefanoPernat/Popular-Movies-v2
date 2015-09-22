package project.android.udacity.com.popularmovies.app;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by stefanopernat on 18/09/15.
 */
public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        getFragmentManager().beginTransaction()
                .replace(R.id.settings_content, new SettingsFragment())
                .commit();
    }
}

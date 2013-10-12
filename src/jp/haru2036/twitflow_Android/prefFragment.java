package jp.haru2036.twitflow_Android;

import android.os.Bundle;
import android.preference.PreferenceFragment;

public class prefFragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.pref);
    }
}

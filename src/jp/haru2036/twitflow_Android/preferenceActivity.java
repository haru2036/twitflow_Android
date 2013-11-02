package jp.haru2036.twitflow_Android;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceActivity;

public class preferenceActivity extends PreferenceActivity{
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction().replace(android.R.id.content, new prefFragment()).commit();
    }

    @Override
    public void onDestroy(){
        super.onDestroy();

        Intent intent = new Intent(this, mainActivity.class);
        startActivity(intent);
    }
}

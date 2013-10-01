package jp.haru2036.twitflow_Android;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

public class mainActivity extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getIsAuth();
        setContentView(new surfaceView_haru(this));
    }
    private void getIsAuth(){
        SharedPreferences pref = getSharedPreferences("haru2036.twitflow", MODE_PRIVATE);
        if(!pref.contains("CK")){
            SharedPreferences.Editor prefeditor = pref.edit();
            prefeditor.putString("CK","PR0e5gwPOo9SV0LFwC5QA");
            prefeditor.putString("CS","ZwPfRWCbBkSZUOA7WLYzpkLc0naLilVQBkztALE9w");
            prefeditor.commit();
        }
        pref = getSharedPreferences("haru2036.twitflow", MODE_PRIVATE);
        if(!pref.contains("AT")){
            Intent intent = new Intent(this, authenticationActivity.class);
            startActivity(intent);
        }
    }
}

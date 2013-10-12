package jp.haru2036.twitflow_Android;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.*;
import android.widget.Toast;
import android.app.ActionBar;
import twitter4j.*;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;


public class mainActivity extends Activity implements StatusInterfaceListener{
    String CK, CS, AT, AS;
    surfaceView_haru surface;
    twitter4jUser t4jusr;
    ActionBar actionBar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        setContentView(R.layout.main);
        actionBar = getActionBar();
        actionBar.hide();
        getIsAuth();
    }

    @Override
    public void onResume(){
        super.onResume();
    }

    @Override
    public void onPause(){
        super.onPause();
    }
    @Override
    public void onDestroy(){
        super.onDestroy();
        if(t4jusr!= null){
            t4jusr.cleanUp();
            t4jusr = null;
        }
        if(surface != null){
            surface.isRunning = false;
        }
    }
    @Override
    public void onConfigurationChanged(android.content.res.Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if(t4jusr != null){
            t4jusr.cleanUp();
            t4jusr = null;
        }
    }
    private void getIsAuth(){
        boolean isTokenChanged = false;
        CK = "";
        CS = "";
        SharedPreferences pref = getSharedPreferences("haru2036.twitflow", MODE_PRIVATE);
        if(!pref.contains("CK")){
            if(!pref.getString("CK", "hoge").equals(CK)){
                isTokenChanged = true;
                SharedPreferences.Editor prefeditor = pref.edit();
                //このCK/CSはミスってPushしたのでリセット済み
                prefeditor.putString("CK",CK);
                prefeditor.putString("CS",CS);
                prefeditor.commit();
            }
        }
        pref = getSharedPreferences("haru2036.twitflow", MODE_PRIVATE);
        if(!pref.contains("AT") || isTokenChanged){
            openAuthActivity(0);
        }else{
            initSurface();
            startStream();
        }
    }

    @Override
    public void onActivityResult(int requestCode,int resultCode, Intent data){
        if(resultCode == Activity.RESULT_OK){
            if(requestCode == 0){
                String token = data.getStringExtra("access_token");
                String secret = data.getStringExtra("access_secret");
                saveAT(token,secret);
                getIsAuth();
            }else if(requestCode == 1){
                Toast.makeText(this, R.string.needReboot, Toast.LENGTH_LONG).show();
            }else if(requestCode == 2){
                String token = data.getStringExtra("access_token");
                String secret = data.getStringExtra("access_secret");
                saveAT(token,secret);
                Toast.makeText(this, R.string.needReboot, Toast.LENGTH_LONG).show();
            }
        }
    }

    private void saveAT(String token, String secret){
        SharedPreferences pref = getSharedPreferences("haru2036.twitflow",MODE_PRIVATE);
        SharedPreferences.Editor peditor = pref.edit();
        peditor.putString("AT", token);
        peditor.putString("AS", secret);
        peditor.commit();
        Toast.makeText(this, R.string.savedat, Toast.LENGTH_LONG).show();
    }

    private void startStream(){
        SharedPreferences pref = getSharedPreferences("haru2036.twitflow", MODE_PRIVATE);
        CK = pref.getString("CK", null);
        CS = pref.getString("CS", null);
        AT = pref.getString("AT", null);
        AS = pref.getString("AS", null);
        ConfigurationBuilder confbuilder = new ConfigurationBuilder();
        Configuration conf = confbuilder.setOAuthConsumerKey(CK).setOAuthConsumerSecret(CS).setOAuthAccessToken(AT).setOAuthAccessTokenSecret(AS).build();
        t4jusr = new twitter4jUser(conf, this);
        t4jusr.user();

        }


    public void openAuthActivity(int reqIdToAuth){
        Intent intent = new Intent(this, authenticationActivity.class);
        startActivityForResult(intent,reqIdToAuth);
    }
    private void toggleActionBar(){
        boolean isShowing = actionBar.isShowing();
        if(isShowing){
            actionBar.hide();
        }else{
            actionBar.show();
        }
    }

    public void initSurface(){
        SurfaceView sv = (SurfaceView) findViewById(R.id.surfaceView);
        sv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleActionBar();
            }
        });
        surface = new surfaceView_haru(this, sv, loadSettingsFromPreference());

    }
    public settings loadSettingsFromPreference(){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        settings setting = new settings();

        String colTheme = preferences.getString("colorTheme", "Light");

        setting.setColorTheme(colTheme);

        String textSize = preferences.getString("textSize", String.valueOf(40));
        String userNameSize = preferences.getString("userNameSize", String.valueOf(30));

        setting.textSize = Integer.parseInt(textSize);
        setting.userNameSize = Integer.parseInt(userNameSize);

        return setting;

    }

    public void openSettingsActivity(){
        disposeSurfaceAndT4J();

        Intent intent = new Intent(this, preferenceActivity.class);
        startActivityForResult(intent, 1);
    }

    public void openAboutAppActivity(){

        Intent intent = new Intent(this, aboutAppActivity.class);
        startActivity(intent);
     }

    public void disposeSurfaceAndT4J(){
        if(t4jusr!= null){
            t4jusr.cleanUp();
            t4jusr = null;
        }
        if(surface != null){
            surface.isRunning = false;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuItem openSettings = menu.add(R.string.openSettings);
        MenuItem openReAuth = menu.add(R.string.openReAuth);
        MenuItem aboutApp = menu.add(R.string.aboutApp);

        MenuItem.OnMenuItemClickListener openSettingsListener = new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                openSettingsActivity();
                return true;
            }
        };
        MenuItem.OnMenuItemClickListener openReAuthListener = new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                openAuthActivity(2);
                return true;
            }
        };
        MenuItem.OnMenuItemClickListener openAboutApp = new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                openAboutAppActivity();
                return true;
            }
        };

        openSettings.setOnMenuItemClickListener(openSettingsListener);
        openReAuth.setOnMenuItemClickListener(openReAuthListener);
        aboutApp.setOnMenuItemClickListener(openAboutApp);

        return true;
    }


    @Override
    public void onStatus(Status status) {
        surface.onNewStatus(status);
    }

}

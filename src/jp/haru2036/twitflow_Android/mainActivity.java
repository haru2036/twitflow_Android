package jp.haru2036.twitflow_Android;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.*;
import android.widget.Toast;
import android.app.ActionBar;
import jp.haru2036.twitflow_Android.config.Tokens;
import twitter4j.*;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;


public class mainActivity extends Activity implements StatusInterfaceListener{
    String CK, CS, AT, AS;
    surfaceView_twiflo surface;
    twitter4jUser t4jusr;
    ActionBar actionBar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        setContentView(R.layout.main);
        actionBar = getActionBar();
        if(actionBar != null) {
            actionBar.hide();
        }
        checkIsAuthorized();
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

    @Override
    public void onActivityResult(int requestCode,int resultCode, Intent data){
        if(resultCode == Activity.RESULT_OK){
            if(requestCode == 0){
                String token = data.getStringExtra("access_token");
                String secret = data.getStringExtra("access_secret");
                saveAT(token,secret);
                checkIsAuthorized();
            }else if(requestCode == 1){
                disposeSurfaceAndT4J();
                checkIsAuthorized();
            }else if(requestCode == 2){
                String token = data.getStringExtra("access_token");
                String secret = data.getStringExtra("access_secret");
                saveAT(token,secret);
                Toast.makeText(this, R.string.needReboot, Toast.LENGTH_LONG).show();
            }
        }
    }

    private void checkIsAuthorized(){

        SharedPreferences settingsPreferences= PreferenceManager.getDefaultSharedPreferences(this);

        boolean useHashTag = settingsPreferences.getBoolean("useFilterStream", false);

        if(useHashTag){

            String hashTagString = settingsPreferences.getString("filterTimelineQuery", null);
            if(hashTagString != null){
                initSurface();
                startStream(hashTagString);
            }else{
                Toast.makeText(this, getString(R.string.queryIsNull), Toast.LENGTH_LONG).show();
            }
        }else{
            SharedPreferences pref = getSharedPreferences("haru2036.twitflow", MODE_PRIVATE);
            if(!pref.contains("AT") || !pref.contains("AS")){
                openAuthActivity(0);
            }else{
                initSurface();
                startStream();
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
        loadTokens();
        ConfigurationBuilder confbuilder = new ConfigurationBuilder();
        Configuration conf = confbuilder.setOAuthConsumerKey(CK).setOAuthConsumerSecret(CS).setOAuthAccessToken(AT).setOAuthAccessTokenSecret(AS).build();
        t4jusr = new twitter4jUser(conf, this);
        t4jusr.user();

        }

    private void startStream(String query){
        loadTokens();
        ConfigurationBuilder confbuilder = new ConfigurationBuilder();
        Configuration conf = confbuilder.setOAuthConsumerKey(CK).setOAuthConsumerSecret(CS).setOAuthAccessToken(AT).setOAuthAccessTokenSecret(AS).build();
        t4jusr = new twitter4jUser(conf, this);

        String[] queryArray = new String[1];
        queryArray[0] = query;
        t4jusr.filter(queryArray);
    }

    private void loadTokens(){
        SharedPreferences pref = getSharedPreferences("haru2036.twitflow",MODE_PRIVATE);
        CK = Tokens.CK;
        CS = Tokens.CS;
        AT = pref.getString("AT", null);
        AS = pref.getString("AS", null);
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
        surface = new surfaceView_twiflo(this, sv, loadSettingsFromPreference());

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
        finish();
        Intent intent = new Intent(this, preferenceActivity.class);
        startActivity(intent);
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
            surface = null;
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

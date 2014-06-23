package jp.haru2036.twitflow_Android;

import jp.haru2036.twitflow_Android.config.Tokens;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.service.dreams.DreamService;
import android.view.SurfaceView;
import android.widget.Toast;
import twitter4j.Status;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;


public class dayDreamService extends DreamService implements StatusInterfaceListener{
    String CK, CS, AT, AS;
    surfaceView_twiflo surface;
    twitter4jUser t4jusr;

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        setInteractive(false);
        setFullscreen(true);
        setContentView(R.layout.main);
        getIsAuth();
    }

    @Override
    public void onDetachedFromWindow(){
        super.onDetachedFromWindow();
        if(t4jusr != null){
        t4jusr.cleanUp();
        t4jusr = null;
        }
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
    }

    private void getIsAuth(){

        CK = Tokens.CK;
        CS = Tokens.CS;

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
            SharedPreferences pref = getSharedPreferences("haru2036.twitflow", MODE_PRIVATE);
            if (pref.contains("AT")) {
                initSurface();
                startStream();
            }
        }
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

    private void startStream(String query){
        SharedPreferences pref = getSharedPreferences("haru2036.twitflow", MODE_PRIVATE);
        CK = pref.getString("CK", null);
        CS = pref.getString("CS", null);
        AT = pref.getString("AT", null);
        AS = pref.getString("AS", null);
        ConfigurationBuilder confbuilder = new ConfigurationBuilder();
        Configuration conf = confbuilder.setOAuthConsumerKey(CK).setOAuthConsumerSecret(CS).setOAuthAccessToken(AT).setOAuthAccessTokenSecret(AS).build();
        t4jusr = new twitter4jUser(conf, this);

        String[] queryArray = new String[1];
        queryArray[0] = query;
        t4jusr.filter(queryArray);
    }

    public void initSurface(){
        SurfaceView sv = (SurfaceView) findViewById(R.id.surfaceView);
        surface = new surfaceView_twiflo(this, sv, loadSettingsFromPreference());
    }

    public settings loadSettingsFromPreference(){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        settings setting = new settings();

        String colTheme = preferences.getString("colorTheme", "Light");

        setting.setColorTheme(colTheme);

        String textSize = preferences.getString("textSize", String.valueOf(44));
        String userNameSize = preferences.getString("userNameSize", String.valueOf(44));

        setting.textSize = Integer.parseInt(textSize);
        setting.userNameSize = Integer.parseInt(userNameSize);

        return setting;

    }
    @Override
    public void onStatus(Status status) {
        surface.onNewStatus(status);
    }

}

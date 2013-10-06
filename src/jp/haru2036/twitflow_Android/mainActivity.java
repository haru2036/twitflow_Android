package jp.haru2036.twitflow_Android;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceView;
import android.widget.Toast;
import twitter4j.*;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;


public class mainActivity extends Activity {
    String CK, CS, AT, AS;
    TwitterStream twitterStream = null;
    surfaceView_haru surface;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        getIsAuth();
    }

    @Override
    public void onPause(){
        super.onPause();
        if(twitterStream !=null){
            twitterStream.cleanUp();
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
            openAuthActivity();
        }else{
            initSurface();
            startStream();
        }
    }

    @Override
    public void onActivityResult(int requestCode,int resultCode, Intent data){
        if(resultCode == Activity.RESULT_OK){
            String token = data.getStringExtra("access_token");
            String secret = data.getStringExtra("access_secret");
            saveAT(token,secret);

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
        TwitterStreamFactory twitterStreamFactory = new TwitterStreamFactory(conf);
        twitterStream = twitterStreamFactory.getInstance();
        twitterStream.addListener(new twStreamlistener());
        twitterStream.user();
    }

    public void openAuthActivity(){
        Intent intent = new Intent(this, authenticationActivity.class);
        startActivityForResult(intent,0);
    }

    public void initSurface(){
        SurfaceView sv = (SurfaceView) findViewById(R.id.surfaceView);
        surface = new surfaceView_haru(this, sv);
    }
    public class twStreamlistener extends UserStreamAdapter{
        @Override
        public void onStatus(Status status) {
            Log.d("onstatus", status.getText());
        }

        @Override
        public void onException(Exception e) {
            Log.d("exception", e.toString());
        }
    }
}

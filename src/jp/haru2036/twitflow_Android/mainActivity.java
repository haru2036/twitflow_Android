package jp.haru2036.twitflow_Android;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import twitter4j.Status;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.UserStreamAdapter;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

import java.util.ArrayList;

public class mainActivity extends Activity {
    String CK, CS, AT, AS;
    ArrayList<Status> timeline = new ArrayList<Status>();
    TwitterStream twitterStream = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getIsAuth();
        setContentView(new surfaceView_haru(this));
    }

    @Override
    public void onPause(){
        super.onPause();
        twitterStream.cleanUp();
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
            Intent intent = new Intent(this, authenticationActivity.class);
            startActivityForResult(intent,0);
        }else{
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
        StreamListener streamListener = new StreamListener();
        twitterStream.addListener(streamListener);
    }
    public class StreamListener extends UserStreamAdapter{

        @Override
        public void onStatus(Status status){
            timeline.add(status);
            if (timeline.size()>100){
                timeline.remove(101);
            }
        }

        @Override
        public void onException(Exception ex){
            Log.d("exeption", ex.toString());
        }
    }
}

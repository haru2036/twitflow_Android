package jp.haru2036.twitflow_Android;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import twitter4j.TwitterException;
import twitter4j.auth.OAuthAuthorization;
import twitter4j.auth.RequestToken;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationContext;

/**
 * Created with IntelliJ IDEA.
 * User: haruka
 * Date: 13/10/02
 * Time: 0:05
 * To change this template use File | Settings | File Templates.
 */
public class authenticationActivity extends Activity{

    Button authbtn;
    OAuthAuthorization oauthobj = null;
    RequestToken reqtoken = null;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.authenticationactivity);
        authbtn = (Button)findViewById(R.id.button);
        authbtn.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v){
                doOAuth();
            }

        });
    }
    //ToDo:android.os.NetworkOnMainThreadException吐かないようにする
    public void doOAuth(){
        Configuration conf = ConfigurationContext.getInstance();
        oauthobj = new OAuthAuthorization(conf);
        SharedPreferences pref = getSharedPreferences("haru2036.twitflow", MODE_PRIVATE);
        String conskey = pref.getString("CK", null);
        String conssec = pref.getString("CS", null);
        if (conskey == null || conssec == null){
            Log.d("twitter", "consumer is null!");
            return;
        }
        oauthobj.setOAuthConsumer(conskey, conssec);
        try{
            reqtoken = oauthobj.getOAuthRequestToken("Callback://callbackActivity");
        } catch(TwitterException e){
            e.printStackTrace();
        }
        String callbackuri = reqtoken.getAuthenticationURL();
        startActivityForResult(new Intent(Intent.ACTION_VIEW , Uri.parse(callbackuri)), 0);
    }
}
package jp.haru2036.twitflow_Android;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import twitter4j.*;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;

import java.util.HashMap;
import java.util.Map;

public class authenticationActivity extends Activity{

    Button authbtn;
    Button getatbtn;
    EditText editText;
    RequestToken reqtoken = null;

    final AsyncTwitterFactory factory = new AsyncTwitterFactory();
    final AsyncTwitter twitter = factory.getInstance();

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.authenticationactivity);

        authbtn = (Button)findViewById(R.id.button);
        getatbtn = (Button)findViewById(R.id.button1);
        editText = (EditText)findViewById(R.id.editText);

        Map<String,String> consmap = getConsumer();

        twitter.addListener(listener);
        twitter.setOAuthConsumer(consmap.get("consumer_key"),consmap.get("consumer_secret"));
        twitter.getOAuthRequestTokenAsync();

        authbtn.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v){
                final Intent reqintent = new Intent(Intent.ACTION_VIEW,Uri.parse(reqtoken.getAuthorizationURL()));
                startActivity(reqintent);
            }

        });

        getatbtn.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v){
                String pin = editText.getText().toString();
                twitter.getOAuthAccessTokenAsync(reqtoken, pin);
            }
        });
    }

    private Map<String,String> getConsumer(){
        SharedPreferences pref = getSharedPreferences("haru2036.twitflow", MODE_PRIVATE);
        String conskey = pref.getString("CK", null);
        String conssec = pref.getString("CS", null);
        if (conskey == null || conssec == null){
            Log.d("twitter", "consumer is null!");
            return null;
        }
        Map<String,String> consmap = new HashMap<String, String>();
        consmap.put("consumer_key",conskey);
        consmap.put("consumer_secret",conssec);
        return consmap;
    }

    private final TwitterListener listener = new TwitterAdapter(){
        @Override
        public void gotOAuthRequestToken(RequestToken token){
            reqtoken = token;
        }

        @Override
        public void gotOAuthAccessToken(AccessToken atoken){
            final Intent intent = new Intent();
            intent.putExtra("access_token", atoken.getToken());
            intent.putExtra("access_secret", atoken.getTokenSecret());
            setResult(Activity.RESULT_OK, intent);
            finish();
        }
    };



}
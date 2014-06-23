package jp.haru2036.twitflow_Android;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import twitter4j.*;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import jp.haru2036.twitflow_Android.config.Tokens;

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


        twitter.addListener(listener);
        twitter.setOAuthConsumer(Tokens.CK, Tokens.CS);
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

    private final TwitterListener listener = new TwitterAdapter(){
        @Override
        public void gotOAuthRequestToken(RequestToken token){
            reqtoken = token;
            authbtn.setEnabled(true);
        }

        @Override
        public void gotOAuthAccessToken(AccessToken atoken){
            final Intent intent = new Intent();
            intent.putExtra("access_token", atoken.getToken());
            intent.putExtra("access_secret", atoken.getTokenSecret());
            setResult(Activity.RESULT_OK, intent);
            finish();
        }
        @Override
        public void onException (TwitterException te, TwitterMethod method) {
            Log.d("flowstream", te.toString());
        }

    };



}
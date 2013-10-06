package jp.haru2036.twitflow_Android;

import android.util.Log;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.conf.Configuration;

public class twitter4jUser {
    TwitterStream twitterStream;

    public twitter4jUser(Configuration conf){
        TwitterStreamFactory twitterStreamFactory = new TwitterStreamFactory(conf);
        twitterStream = twitterStreamFactory.getInstance();
        twitterStream.addListener(new myStatusListener());
        Log.d("twitter", "Stream Start");
        twitterStream.user();
        Log.d("twitter", "user()");
    }

    public void cleanUp(){
        twitterStream.cleanUp();
    }

}

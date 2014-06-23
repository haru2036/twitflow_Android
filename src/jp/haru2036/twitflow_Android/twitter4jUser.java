package jp.haru2036.twitflow_Android;

import android.util.Log;
import twitter4j.*;
import twitter4j.conf.Configuration;

public class twitter4jUser {
    TwitterStream twitterStream;
    private StatusInterfaceListener listener = null;

    public twitter4jUser(Configuration conf, StatusInterfaceListener arglistener){
        listener = arglistener;
        TwitterStreamFactory twitterStreamFactory = new TwitterStreamFactory(conf);
        twitterStream = twitterStreamFactory.getInstance();
        twitterStream.addListener(new UserStreamListener() {

            @Override
            public void onStatus(Status status) {
                onStatusAdapter(status);
            }

            @Override
            public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {
            }

            @Override
            public void onTrackLimitationNotice(int i) {
            }

            @Override
            public void onScrubGeo(long l, long l2) {
            }

            @Override
            public void onStallWarning(StallWarning stallWarning) {
            }

            @Override
            public void onException(Exception e) {
                Log.d("exception", e.toString());
            }

            @Override
            public void onDeletionNotice(long l, long l2) {

            }

            @Override
            public void onFriendList(long[] longs) {

            }

            @Override
            public void onFavorite(User user, User user2, Status status) {

            }

            @Override
            public void onUnfavorite(User user, User user2, Status status) {

            }

            @Override
            public void onFollow(User user, User user2) {

            }

            @Override
            public void onUnfollow(User user, User user2) {

            }

            @Override
            public void onDirectMessage(DirectMessage directMessage) {

            }

            @Override
            public void onUserListMemberAddition(User user, User user2, UserList userList) {

            }

            @Override
            public void onUserListMemberDeletion(User user, User user2, UserList userList) {

            }

            @Override
            public void onUserListSubscription(User user, User user2, UserList userList) {

            }

            @Override
            public void onUserListUnsubscription(User user, User user2, UserList userList) {

            }

            @Override
            public void onUserListCreation(User user, UserList userList) {

            }

            @Override
            public void onUserListUpdate(User user, UserList userList) {

            }

            @Override
            public void onUserListDeletion(User user, UserList userList) {

            }

            @Override
            public void onUserProfileUpdate(User user) {

            }

            @Override
            public void onBlock(User user, User user2) {

            }

            @Override
            public void onUnblock(User user, User user2) {

            }
        });
    }
    public void user(){
        twitterStream.user();
    }

    public void filter(String[] track){
        FilterQuery query = new FilterQuery();
        query.track(track);
        twitterStream.filter(query);
    }

    public void onStatusAdapter(Status status){
        listener.onStatus(status);
    }

    public void cleanUp(){
        twitterStream.cleanUp();
        twitterStream.shutdown();
    }

}

package jp.haru2036.twitflow_Android;

import twitter4j.Status;

import java.util.EventListener;

public interface StatusInterfaceListener extends EventListener{
    public void onStatus(Status status);
}

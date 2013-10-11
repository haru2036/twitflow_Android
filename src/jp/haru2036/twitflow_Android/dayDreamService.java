package jp.haru2036.twitflow_Android;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.service.dreams.DreamService;
import android.view.SurfaceView;
import android.widget.Toast;
import twitter4j.Status;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;


public class dayDreamService extends DreamService implements StatusInterfaceListener{
    String CK, CS, AT, AS;
    surfaceView_haru surface;
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
        boolean isTokenChanged = false;
        SharedPreferences pref = getSharedPreferences("haru2036.twitflow", MODE_PRIVATE);
        if(!pref.contains("CK")){
            if(!pref.getString("CK", "hoge").equals(CK)){
                isTokenChanged = true;
            }
        }
        pref = getSharedPreferences("haru2036.twitflow", MODE_PRIVATE);
        if(!pref.contains("AT") || isTokenChanged){
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setTitle(R.string.needConfigTitle);
            alertDialogBuilder.setMessage(R.string.needConfig);
            alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            });
            finish();
        }else{
            initSurface();
            startStream();
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



    public void initSurface(){
        SurfaceView sv = (SurfaceView) findViewById(R.id.surfaceView);
        surface = new surfaceView_haru(this, sv);
    }

    @Override
    public void onStatus(Status status) {
        surface.onNewStatus(status);
    }

}

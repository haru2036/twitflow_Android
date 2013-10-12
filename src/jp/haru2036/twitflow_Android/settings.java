package jp.haru2036.twitflow_Android;

import android.graphics.Color;

public class settings {
    int textSize = 44;
    int userNameSize = 33;
    int timeAndViaSize = 33;
    int iconSize = 100;
    int margin = 5;
    int perFrameMove = 5;
    int bgColor = Color.LTGRAY;
    int textColor = Color.BLACK;
    int subColor = Color.DKGRAY;

    public settings(){

    }

    public void setColorTheme(String colorTheme){
        if(colorTheme.equals("Light")){
            bgColor = Color.LTGRAY;
            textColor = Color.BLACK;
            subColor = Color.DKGRAY;
        }else if(colorTheme.equals("Dark")){
            bgColor = Color.BLACK;
            textColor = Color.LTGRAY;
            subColor = Color.GRAY;
        }
    }
}

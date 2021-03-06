package jp.haru2036.twitflow_Android;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import twitter4j.Status;
import com.loopj.android.http.*;

public class StatusBitmap {

    Status status;
    Bitmap icon = null;
    private Point wholeCoord;

    public Point getCoord(){
        return wholeCoord;
    }

    public Point offsetYCoord(int offset){
        wholeCoord.y = wholeCoord.y - offset;
        return wholeCoord;
    }

    public StatusBitmap(Status importStatus, Point defaultCoord){

        status = importStatus;
        wholeCoord = defaultCoord;
        getIcon();

    }
    private void getIcon(){
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(status.getUser().getProfileImageURL(), new BinaryHttpResponseHandler(){
            @Override
            public void onSuccess(byte[] fileData){
                BitmapFactory.Options options = new BitmapFactory.Options();
                Bitmap bitmap = BitmapFactory.decodeByteArray(fileData, 0, fileData.length, options);
                icon = bitmap;
            }
        });
    }

}

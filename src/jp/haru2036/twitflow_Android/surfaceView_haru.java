package jp.haru2036.twitflow_Android;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * Created with IntelliJ IDEA.
 * User: haruka
 * Date: 13/10/01
 * Time: 23:34
 * To change this template use File | Settings | File Templates.
 */
public class surfaceView_haru extends SurfaceView implements SurfaceHolder.Callback{
    private int pos;

    public surfaceView_haru(Context context){
        super(context);

        getHolder().addCallback(this);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        //To change body of implemented methods use File | Settings | File Templates.
        Canvas canvas = holder.lockCanvas();
        Paint paint = new Paint();
        //ToDo:Change "22" to set value in setting, fontsize
        paint.setTextSize(22);
        paint.setColor(Color.WHITE);
        canvas.drawText("text",100,100,paint);

        holder.unlockCanvasAndPost(canvas);

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
    }
}

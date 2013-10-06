package jp.haru2036.twitflow_Android;

import android.content.Context;
import android.graphics.*;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import twitter4j.Status;
import twitter4j.User;
import java.util.ArrayList;

public class surfaceView_haru extends SurfaceView implements SurfaceHolder.Callback, Runnable{

    public int textSize, userNameSize, timeAndViaSize;
    private int iconSize;
    private int perFrameMove;
    int margin = 1;
    boolean isRunning = false;
    Thread thread;

    private Point dispSize;
    private SurfaceHolder holder;
    private ArrayList<StatusBitmap> timeLine= new ArrayList<StatusBitmap>();



    public surfaceView_haru(Context context, SurfaceView sv){
        super(context);
        holder = sv.getHolder();
        holder.addCallback(this);
        textSize = 66;
        userNameSize = 44;
        timeAndViaSize = 33;
        iconSize = 100;
        margin = 1;
        perFrameMove = 10;

    }
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Rect frame = holder.getSurfaceFrame();

        dispSize = new Point(frame.width(), frame.height());
        thread = new Thread(this);
        thread.start();



    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        dispSize = new Point(width, height);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        isRunning = false;
        while(thread.isAlive());
    }



    public void renderStatus(StatusBitmap status, Canvas canvas){
        Point wholeCoord = status.wholeCoord;

        Paint iconPaint = new Paint();

        Bitmap icon = status.icon;

        int iconHeight = icon.getHeight();
        int iconWidth = icon.getWidth();

        Rect srcrect = new Rect(0, 0, iconWidth, iconHeight);
        Rect destRect = new Rect(wholeCoord.x, wholeCoord.y, wholeCoord.x + iconSize, wholeCoord.y + iconSize);
        canvas.drawBitmap(status.icon, srcrect, destRect, iconPaint);

        wholeCoord.x = wholeCoord.x + iconSize + margin;


        Paint namePaint = new Paint();
        namePaint.setColor(Color.LTGRAY);
        namePaint.setAntiAlias(true);
        namePaint.setTextSize(userNameSize);

        User user = status.status.getUser();
        String names = user.getName() + " @" + user.getScreenName();

        Rect boundRect = new Rect();
        namePaint.getTextBounds(names, 0, names.length(), boundRect);
        Paint.FontMetrics fontMetrics = namePaint.getFontMetrics();
        int fmHeight = (int)(Math.abs(fontMetrics.top) + fontMetrics.bottom);

        canvas.drawText(names, (float)wholeCoord.x, (float)wholeCoord.y, namePaint);

        wholeCoord.y = wholeCoord.y + fmHeight + margin;


        Paint textPaint = new Paint();
        textPaint.setColor(Color.WHITE);
        textPaint.setAntiAlias(true);
        textPaint.setTextSize(textSize);

        String text = status.status.getText();
        canvas.drawText(text, 0, text.length(), textPaint);
    }

    public void renderTL(Canvas canvas){
        ArrayList<StatusBitmap> tmplist = new ArrayList<StatusBitmap>();

        for(StatusBitmap status : timeLine){
            renderStatus(status, canvas);
            status.wholeCoord.y = status.wholeCoord.y - perFrameMove;
            tmplist.add(status);
        }

        timeLine = tmplist;
    }

    public void onNewStatus(Status status){
        Point coord = new Point(0, dispSize.y);
        timeLine.add(new StatusBitmap(status, coord));
        if (timeLine.size() <= 100) {
            return;
        }
        timeLine.remove(101);
    }

    @Override
    public void run(){
        while(isRunning){
            Canvas canvas = holder.lockCanvas();
            renderTL(canvas);
            holder.unlockCanvasAndPost(canvas);
        }
    }

}

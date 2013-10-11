package jp.haru2036.twitflow_Android;

import android.content.Context;
import android.graphics.*;
import android.util.Log;
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


    private int bgColor, textColor, subColor;
    private Point dispSize;
    private SurfaceHolder holder;
    private ArrayList<StatusBitmap> timeLine= new ArrayList<StatusBitmap>();



    public surfaceView_haru(Context context, SurfaceView sv){
        super(context);
        holder = sv.getHolder();
        holder.addCallback(this);
        textSize = 44;
        userNameSize = 22;
        timeAndViaSize = 33;
        iconSize = 100;
        margin = 5;
        perFrameMove = 5;
        bgColor = Color.LTGRAY;
        textColor = Color.BLACK;
        subColor = Color.GRAY;

    }
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Rect frame = holder.getSurfaceFrame();
        isRunning = true;
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
    }



    public void renderStatus(StatusBitmap status, Canvas canvas){
        Point objCoord = status.getCoord();
        int objX, objY;
        objX = objCoord.x;
        objY = objCoord.y;

        if(status.icon != null){
            Paint iconPaint = new Paint();

            Bitmap icon = status.icon;

            int iconHeight = icon.getHeight();
            int iconWidth = icon.getWidth();

            Rect srcrect = new Rect(0, 0, iconWidth, iconHeight);
            Rect destRect = new Rect(objX, objY, objX + iconSize, objY + iconSize);
            canvas.drawBitmap(status.icon, srcrect, destRect, iconPaint);

            objX = objX + iconSize + margin;
        }

        Paint namePaint = new Paint();
        namePaint.setColor(subColor);
        namePaint.setAntiAlias(true);
        namePaint.setTextSize(userNameSize);

        User user = status.status.getUser();
        String names = user.getName() + " @" + user.getScreenName();

        Rect boundRect = new Rect();
        namePaint.getTextBounds(names,0,names.length(),boundRect);

        Paint.FontMetrics nameFontMetrics = namePaint.getFontMetrics();
        int fmHeight = (int)(Math.abs(nameFontMetrics.top) + nameFontMetrics.bottom);

        canvas.drawText(names, (float)objX, (float)objY, namePaint);

        objY = objY + fmHeight + margin;


        String text = status.status.getText();

        Paint textPaint = new Paint();
        textPaint.setColor(textColor);
        textPaint.setAntiAlias(true);
        textPaint.setTextSize(textSize);

        int textWidth = dispSize.x - objX ;
        int lineBreak = textPaint.breakText(text, true, textWidth, null);
        int breakIdx = 0;
        int renderdPos = objY;

        while(lineBreak != 0){
            String measure = text.substring(breakIdx);
            lineBreak = textPaint.breakText(measure, true, textWidth, null);
            if(lineBreak != 0){
                String line = text.substring(breakIdx, breakIdx + lineBreak);
                canvas.drawText(line, (float)objX, (float)renderdPos, textPaint);
                breakIdx +=lineBreak;
                renderdPos += textSize + margin;
            }
        }
        if(lineBreak == 0){
            canvas.drawText(text, (float)objX, (float)objY, textPaint);
        }

    }


    public synchronized boolean renderTL(Canvas canvas){
        boolean isContinue = false;
        if(isRunning){
            isContinue = true;
            canvas.drawColor(bgColor);

            ArrayList<StatusBitmap> tempList = new ArrayList<StatusBitmap>();

            for(StatusBitmap status : timeLine){
                renderStatus(status, canvas);
                status.offsetYCoord(perFrameMove);
                if (status.getCoord().y > -300) {
                    tempList.add(status);
                }
            }
            timeLine = tempList;
        }
        notifyAll();
        return isContinue;
    }

    public synchronized void onNewStatus(Status status){
        Log.d("onstatus", status.getText());
        Point coord = new Point(0, dispSize.y);
        timeLine.add(new StatusBitmap(status, coord));
        notifyAll();
    }

    @Override
    public void run(){

        while(isRunning){
            Canvas canvas = holder.lockCanvas();
            boolean isContinue = renderTL(canvas);
            if(!isContinue){
                break;
            }
            holder.unlockCanvasAndPost(canvas);
        }
    }

}

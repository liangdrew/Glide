package com.andrewliang.glide;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class AboutView extends SurfaceView implements SurfaceHolder.Callback{

    private final SurfaceHolder aboutS;
    private Canvas aboutCanvas;
    private final Paint ABOUT_TEXT_PAINT = new Paint();
    private final Paint ABOUT_BACKGROUND_PAINT = new Paint();
    private final AboutActivity aboutActivity;
    private final String ABOUT_TITLE = "GLIDE";

    private int aboutViewHeight;
    private int aboutViewWidth;

    public AboutView(Context context, AboutActivity a)
    {
        super(context);
        getHolder().addCallback(this);
        setFocusable(true);
        aboutS = this.getHolder();
        this.aboutActivity = a;
    }

    public AboutActivity getAboutActivity() {return this.aboutActivity;}

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh)
    {
        super.onSizeChanged(w, h, oldw, oldh);

        aboutViewHeight = aboutActivity.getSize().y;
        aboutViewWidth = aboutActivity.getSize().x;

        ABOUT_TEXT_PAINT.setColor(Color.BLACK);
        ABOUT_TEXT_PAINT.setAntiAlias(true);
        ABOUT_TEXT_PAINT.setTextSize(aboutViewWidth / 25);  // Consider making text size constant
        ABOUT_BACKGROUND_PAINT.setColor(Color.WHITE);
    }

    private void drawAbout()
    {
        //drawing sequence
        aboutCanvas = null;                                      //clear the canvas
        aboutCanvas = aboutS.lockCanvas(null);                        //set the canvas to the canvas we can draw returned from lockCanvas()
        aboutCanvas.drawPaint(ABOUT_BACKGROUND_PAINT);

        aboutCanvas.drawText(ABOUT_TITLE, aboutViewWidth / 20, aboutViewHeight / 10, ABOUT_TEXT_PAINT);
        aboutS.unlockCanvasAndPost(aboutCanvas);                       //finish drawing - stop editing the canvas and post it to the view
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder)
    {
        if (aboutCanvas != null)
        {
            drawAbout();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {}

    @Override
    public void surfaceDestroyed(SurfaceHolder holder){}


}

package com.teraime.chesstrainer;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.PathDashPathEffect;
import android.graphics.PathEffect;
import android.os.Handler;
import android.os.Looper;

public class Progressor {


    Paint pB = new Paint();
    Paint pR = new Paint();
    Paint pL = new Paint();
    Paint pBlack = new Paint(Color.BLACK);

    int offset,width,height,centre;
    int pStartOffsetX, ballRadius;
    int ballSpacing;
    int boardMargin=5;
    int currentLevel=1;
    int scrollFactor=0;int textSize;
    final Handler animationHandler = new Handler(Looper.myLooper());
    final SurfaceHolderCallback surfer;

    public Progressor(int _offset, int _width, int _height,SurfaceHolderCallback _surfer) {
        surfer=_surfer;
        offset=_offset;
        width=_width; height = _height;
        ballRadius = height/4;
        centre=height-ballRadius-boardMargin;
        ballSpacing = ballRadius*5;
        pStartOffsetX = width/4;
        pR.setStyle(Paint.Style.STROKE);
        pR.setColor(Color.WHITE);
        pB.setStyle(Paint.Style.FILL);
        pB.setColor(Color.WHITE);
        pL.setStyle(Paint.Style.FILL);
        //PathEffect effects = new DashPathEffect(new float[]{15,8,15,8},0);
        //pL.setPathEffect(effects);
        pL.setColor(Color.WHITE);
        pL.setStrokeWidth(3);
        pBlack.setTextAlign(Paint.Align.CENTER);
        textSize = ballRadius/2;
        pBlack.setTextSize(textSize);
        pBlack.setFakeBoldText(true);
        pBlack.setAntiAlias(true);
        pBlack.setFilterBitmap(true);
        pBlack.setDither(true);


    }

    boolean scrollAnimate = false;
    int mStoplevel = 10;

    public void scrollAnimate(final int stepsPerIteration, final int stepsTot) {

        mStoplevel = stepsTot+currentLevel;

        animationHandler.post(new Runnable() {
            int lC=currentLevel;
            int levelAcc = (stepsTot<10?1:stepsTot<25?2:stepsTot<50?5:10);
            @Override
            public void run() {
                while (lC<mStoplevel) {
                    int scrollDistance = width / 4 * stepsPerIteration;
                    int scrollSteps = 5;
                    int scrollPerIteration = scrollDistance / scrollSteps;
                    scrollAnimate = true;
                    while (scrollSteps > 0) {
                        scrollFactor += scrollPerIteration;
                        invalidateView();
                        try {
                            Thread.sleep(5);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        scrollSteps--;

                    }
                    scrollFactor=0;
                    currentLevel+=levelAcc;
                    lC+=levelAcc;

                }
                currentLevel=(currentLevel>mStoplevel)?mStoplevel:currentLevel;
                scrollAnimate=false;
                scrollFactor=0;
            }
        });
    }

    private void invalidateView() {
        surfer.surfaceChanged();
    }

    public void onDraw(Canvas c) {
        c.save();
        c.translate(0,offset);
        c.drawRect(0,0,width,height,pR);
        c.drawLine(0,centre,width,centre,pL);
        for (int i = 0; i<8;i++) {
            c.drawCircle(pStartOffsetX+width/4*(i+1)-scrollFactor,centre,ballRadius,pB);
            c.drawText((i+currentLevel)+"",pStartOffsetX+width/4*(i+1)-scrollFactor,centre+textSize/2,pBlack);
        }
        c.restore();
    }
}

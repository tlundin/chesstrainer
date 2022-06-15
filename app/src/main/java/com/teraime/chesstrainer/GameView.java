package com.teraime.chesstrainer;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.View;

import androidx.annotation.NonNull;

import java.util.Random;

public class GameView implements SurfaceHolder.Callback, View.OnClickListener, View.OnTouchListener {


    private final Context context;

    public GameView(Context context) {

        this.context = context;


    }
    String TAG = "beboop";

    @Override
    public void surfaceCreated(@NonNull SurfaceHolder surfaceHolder) {
        Log.d(TAG,"Surf is good");
        tryDrawing(surfaceHolder);
    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder surfaceHolder, int i, int i1, int i2) {
        tryDrawing(surfaceHolder);
    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder surfaceHolder) {

    }

    private void tryDrawing(SurfaceHolder holder) {
        Log.i(TAG, "Trying to draw...");

        Canvas canvas = holder.lockCanvas();

        if (canvas == null) {
            Log.e(TAG, "Cannot draw onto the canvas as it's null");
        } else {
            drawMyStuff(canvas);
            holder.unlockCanvasAndPost(canvas);
        }
    }
    private Rect mRect;
    private void drawMyStuff(final Canvas canvas) {
        Random random = new Random();
        Log.i(TAG, "Drawing...");
        Paint p = new Paint();
        Rect r = new Rect(0,0,64,64);
        mRect=r;
        Bitmap bmp = BitmapFactory.decodeResource(context.getResources(),R.drawable.b_king);

        canvas.drawBitmap(bmp,null,r,p);
    }

    @Override
    public void onClick(View view) {
        Log.d(TAG,"pluck");
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
                int x = (int)motionEvent.getX();
                int y = (int)motionEvent.getY();
                if (mRect.contains(x,y))
                    Log.d(TAG,"clickclock");
                Log.d(TAG,"x: "+x+"y:"+y);
                break;
            case MotionEvent.ACTION_UP:
                view.performClick();
                break;
            default:
                break;
        }
        return false;
    }
}
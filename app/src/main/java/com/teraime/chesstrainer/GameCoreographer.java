package com.teraime.chesstrainer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.PathEffect;
import android.graphics.Rect;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.graphics.drawable.AnimationDrawable;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.annotation.NonNull;

import com.teraime.chesstrainer.databinding.ActivityFullscreenBinding;


public class GameCoreographer implements SurfaceHolder.Callback {

    private final ImageButton startButton;
    Context context;
    Handler handler;

    public GameCoreographer(Context context, ActivityFullscreenBinding binding) {
        //Get the player details.
        this.context = context;
        startButton = binding.startB;
        Bitmap off = BitmapFactory.decodeResource(context.getResources(),R.drawable.off_button);
        Bitmap pressed= BitmapFactory.decodeResource(context.getResources(),R.drawable.green_pressed);
        Bitmap on = BitmapFactory.decodeResource(context.getResources(),R.drawable.green4_button);
        //startButton.setImageBitmap(off);
        startButton.setBackgroundResource(R.drawable.buttonfade);
        startButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        final AnimationDrawable frameAnimation = (AnimationDrawable) startButton.getBackground();
                        frameAnimation.stop();
                        startButton.setImageBitmap(pressed);
                        break;
                    case MotionEvent.ACTION_UP:
                        startButton.setImageBitmap(on);
                        break;
                }
                return false;
            }
        });
        handler = new Handler();
    }

    @Override
    public void surfaceCreated(@NonNull SurfaceHolder surfaceHolder) {
        Log.d("v","getzz");
        Canvas c = surfaceHolder.lockCanvas();
        drawSplash(c);
        surfaceHolder.unlockCanvasAndPost(c);
        Tools.PersistenceHelper ph = new Tools.PersistenceHelper(context);
        String playerName = ph.getString("PLAYER");
        if (playerName.equals(Tools.PersistenceHelper.UNDEFINED)) {
            Log.d("v", "new player");
        } else
            Log.d("v","player name: "+playerName);
        //new GameView(this,createDB(),scoreT,retry);

    }

    Bitmap orb;
    private void drawSplash(Canvas c) {
        final Rect bg = new Rect(0,0,c.getWidth(),c.getHeight());
        final Rect orbR = new Rect(0,0,c.getWidth(),c.getWidth());
        orb = BitmapFactory.decodeResource(context.getResources(),R.drawable.glassball3);
        Shader mShader = new LinearGradient(0, 0, 0, c.getHeight(), new int[] {
                Color.BLACK,Color.BLACK,Color.DKGRAY },
                null, Shader.TileMode.REPEAT);  // CLAMP MIRROR REPEAT
        Paint p = new Paint();
        p.setShader(mShader);
        c.drawRect(bg,p);
        c.drawBitmap(orb,null,orbR,p);
        Paint p2 = new Paint();
        PathEffect pe = new PathEffect();
        pe.
        p2.setColor(Color.WHITE);
        p2.setTextSize(50);
        p2.setTypeface(Typeface.SERIF);
        p2.setTextAlign(Paint.Align.CENTER);
        p2.setPathEffect()
        c.drawText("The Grandmaster's Orb",c.getWidth()/2,c.getWidth()/2,p2);

        startButton.setVisibility(View.VISIBLE);
        final AnimationDrawable frameAnimation = (AnimationDrawable) startButton.getBackground();

        handler.post(new Runnable(){
            public void run(){
                frameAnimation.start();
            }
        });
    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder surfaceHolder, int i, int i1, int i2) {

    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder surfaceHolder) {

    }
}

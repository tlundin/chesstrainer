package com.teraime.chesstrainer;

import android.animation.Animator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BlendModeColorFilter;
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
import android.os.Looper;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.teraime.chesstrainer.databinding.ActivityFullscreenBinding;

import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;


public class GameCoreographer implements SurfaceHolder.Callback {

    private final ImageButton endB,startButton, easyDiffB,normalDiffB,hardDiffB;
    private final TextView easyT;
    private final Context context;
    private final Paint p2 = new Paint();Paint p = new Paint();
    AnimationDrawable frameAnimation;
    SurfaceHolder surfaceHolder;
    Bitmap newbieBmp, clubBmp, masterBmp, logo;
    final Handler handler;
    Rect bg,orbR;
    Shader mShader;
    Bitmap on,off,pressed;

    ArrayList<View> viewsToFadeIn;
    private int shrinkTarget=0,shrinkTargetX;
    private int shrinkX;
    private int textSize = 50;
    private GameView gv;
    private int orbY;
    private ImageView easyTxt,normalTxt,hardTxt;


    public GameCoreographer(Context context, ActivityFullscreenBinding binding,GameView gv) {

        this.context = context;
        this.gv = gv;
        startButton = binding.startB;
        off = BitmapFactory.decodeResource(context.getResources(),R.drawable.off_button);
        pressed= BitmapFactory.decodeResource(context.getResources(),R.drawable.green_pressed);
        on = BitmapFactory.decodeResource(context.getResources(),R.drawable.green4_button);
        logo = BitmapFactory.decodeResource(context.getResources(),R.drawable.logo);
        easyTxt = binding.easytxt;
        normalTxt = binding.normaltxt;
        hardTxt = binding.hardtxt;
        //startButton.setImageBitmap(off);
        easyDiffB = binding.diffEasyButton;
        normalDiffB = binding.diffNormalButton;
        hardDiffB = binding.diffHardButton;
        endB = binding.endB;
        easyT = binding.easyT;
        orb = BitmapFactory.decodeResource(context.getResources(),R.drawable.glassball3);

        handler = new Handler();

        p2.setColor(Color.parseColor("#F8F6F0"));
        p2.setFakeBoldText(true);
        p2.setTypeface(Typeface.defaultFromStyle(Typeface.ITALIC));
        p2.setTextAlign(Paint.Align.CENTER);
        //p2.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.ITALIC));

        startButton.setVisibility(View.VISIBLE);


                init();

        viewsToFadeIn = new ArrayList<View>();
        viewsToFadeIn.add(easyDiffB);
        viewsToFadeIn.add(normalDiffB);
        viewsToFadeIn.add(hardDiffB);


    }

    private void init() {

        Tools.PersistenceHelper ph = new Tools.PersistenceHelper(context);
        String playerName = ph.getString("PLAYER");
        if (playerName.equals(Tools.PersistenceHelper.UNDEFINED)) {
            Log.d("v", "new player");
            handler.post(new Runnable() {
                @Override
                public void run() {
                    displaySkillChoice();
                }
            });

        } else {
            Log.d("v", "player name: " + playerName);
            enableEntry();
        }
        //new GameView(this,createDB(),scoreT,retry);

    }

    boolean newPlayer = false;
    View selected = null;

    private void displaySkillChoice() {
        easyDiffB.setVisibility(View.VISIBLE);
        normalDiffB.setVisibility(View.VISIBLE);
        hardDiffB.setVisibility(View.VISIBLE);



        easyDiffB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selected = view;
                hardDiffB.setBackground(context.getDrawable(R.drawable.diff_hard));
                normalDiffB.setBackground(context.getDrawable(R.drawable.diff_normal));
                easyDiffB.setBackground(context.getDrawable(R.drawable.easy_selected_button));
                easyT.setText("EASY");
                easyT.setVisibility(View.VISIBLE);
                enableEntry();
            }
        });
        normalDiffB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selected = view;
                hardDiffB.setBackground(context.getDrawable(R.drawable.diff_hard));
                normalDiffB.setBackground(context.getDrawable(R.drawable.normal_selected_button));
                easyDiffB.setBackground(context.getDrawable(R.drawable.diff_easy));
                easyT.setText("NORMAL");
                easyT.setVisibility(View.VISIBLE);
                enableEntry();
            }
        });
        hardDiffB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selected = view;
                hardDiffB.setBackground(context.getDrawable(R.drawable.hard_selected_button));
                normalDiffB.setBackground(context.getDrawable(R.drawable.diff_normal));
                easyDiffB.setBackground(context.getDrawable(R.drawable.diff_easy));
                easyT.setText("HARD");
                easyT.setVisibility(View.VISIBLE);
                enableEntry();
            }
        });

        for (View v : viewsToFadeIn) {
            v.setAlpha(0); // make invisible to start
        }
        fade(1);

    }

    private void fade(float target) {
        int i = 1;
        for (View v : viewsToFadeIn) {
            v.animate().alpha(target).setDuration(i*1500).withEndAction(new Runnable() {
                @Override
                public void run() {
                    if (v.equals(hardDiffB)) {
                        if (selected == null) {
                            newPlayer = true;
                            normalDiffB.callOnClick();
                            easyTxt.setVisibility(View.VISIBLE);
                            normalTxt.setVisibility(View.VISIBLE);
                            hardTxt.setVisibility(View.VISIBLE);
                            invalidate();
                        } else {
                            hardDiffB.setVisibility(View.GONE);
                            easyDiffB.setVisibility(View.GONE);
                            normalDiffB.setVisibility(View.GONE);
                            startButton.setVisibility(View.GONE);
                            easyTxt.setVisibility(View.GONE);
                            normalTxt.setVisibility(View.GONE);
                            hardTxt.setVisibility(View.GONE);
                            shrinking=true;
                            orbY = gv.getOrbY();
                            shrinkOrb();
                        }
                    }
                }
            }).start();
            i++;
        }

    }



    private void enableEntry() {
        startButton.setBackgroundResource(R.drawable.buttonfade);
        frameAnimation = (AnimationDrawable) startButton.getBackground();
        handler.post(new Runnable(){
            public void run(){
                frameAnimation.start();
            }
        });
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
                        easyT.setVisibility(View.GONE);
                        fade(0);
                        startButton.setOnTouchListener(null);
                        break;
                }
                return false;
            }
        });
    }


    @Override
    public void surfaceCreated(@NonNull SurfaceHolder surfaceHolder) {
        Log.d("v","getzz");
        this.surfaceHolder=surfaceHolder;
        Canvas c = surfaceHolder.lockCanvas();
        bg = new Rect(0,0,c.getWidth(),c.getHeight());
        orbR = new Rect(0,0,c.getWidth(),c.getWidth());
        mShader = new LinearGradient(0, 0, 0, c.getHeight(), new int[] {
                Color.BLACK,Color.BLACK,Color.DKGRAY },
                null, Shader.TileMode.REPEAT);  // CLAMP MIRROR REPEAT
        shrinkTarget = c.getWidth()*2/3;
        surfaceHolder.unlockCanvasAndPost(c);
        invalidate();


    }

    private void invalidate() {
        Canvas c = surfaceHolder.lockCanvas();
        drawSplash(c);
        surfaceHolder.unlockCanvasAndPost(c);
    }

    Bitmap orb;
    Paint p3 = new Paint();
    boolean shrinking = false;

    private void shrinkOrb() {
        GameContext.tp.submit(new Runnable() {
            @Override
            public void run() {
                int shrinkFactor = 0;
                while(shrinkFactor < shrinkTarget) {
                    if (shrinkTarget-shrinkFactor < 5)
                        shrinkFactor+= (shrinkTarget-shrinkFactor);
                    else
                        shrinkFactor+=5;
                    invalidate();
                }
                surfaceHolder.removeCallback(GameCoreographer.this);
                gv.surfaceChanged();
            }
        });
    }
    int orbC = 0;
    private void drawSplash(Canvas c) {
        p.setShader(mShader);
        c.drawRect(bg,p);
        if (shrinking) {
            orbR.set(0,0,orbR.width()-4,orbR.height()-4);
            orbR.offsetTo((c.getWidth()-orbR.width())/2,orbC<orbY?orbC+=2:orbY);
        } else {
            orbR.offsetTo(0, 0);
            c.drawBitmap(logo, c.getWidth() / 2-logo.getWidth()/2, c.getWidth() / 2 -logo.getHeight()/2, p2);



        }
        c.drawBitmap(orb,null,orbR,p);


        //p2.setTextSize(35);
        //if (newPlayer && selected == null)
        //    c.drawText("Please choose",c.getWidth()/2,c.getWidth()-easyDiffB.getHeight()+35,p2);


    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder surfaceHolder, int i, int i1, int i2) {

    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder surfaceHolder) {

    }
}

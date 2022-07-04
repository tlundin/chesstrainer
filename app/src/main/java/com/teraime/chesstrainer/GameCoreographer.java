package com.teraime.chesstrainer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.graphics.drawable.AnimationDrawable;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.teraime.chesstrainer.databinding.ActivityFullscreenBinding;

import java.util.ArrayList;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;


public class GameCoreographer implements SceneContext, GameWidget {

    private final ImageButton startButton;
    private final ImageButton easyDiffB;
    private final ImageButton normalDiffB;
    private final ImageButton hardDiffB;
    private final TextView easyT;
    private final Context context;
    private final Paint p2 = new Paint();
    private final int orbYTarget;
    Paint p = new Paint();
    AnimationDrawable frameAnimation;
    Bitmap logo;
    final Handler handler;
    Rect bg,orbR;
    Shader mShader;
    Bitmap on,off,pressed;
    ArrayList<View> viewsToFadeIn;
    private final int shrinkTarget;
    private final ImageView easyTxt,normalTxt,hardTxt;
    boolean newPlayer = false;
    View selected = null;
    private final GameContext gc;
    final private Queue<DrawableGameWidget> mWidgets = new ConcurrentLinkedQueue<>();
    final Bitmap orb;




    public GameCoreographer(GameContext gameContext, ActivityFullscreenBinding binding)  {

        this.context = gameContext.context;
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

        easyT = binding.easyT;
        orb = BitmapFactory.decodeResource(context.getResources(),R.drawable.glassball3);

        handler = new Handler();

        p2.setColor(Color.parseColor("#F8F6F0"));
        p2.setFakeBoldText(true);
        p2.setTypeface(Typeface.defaultFromStyle(Typeface.ITALIC));
        p2.setTextAlign(Paint.Align.CENTER);
        //p2.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.ITALIC));

        startButton.setVisibility(View.VISIBLE);

        viewsToFadeIn = new ArrayList<>();
        viewsToFadeIn.add(easyDiffB);
        viewsToFadeIn.add(normalDiffB);
        viewsToFadeIn.add(hardDiffB);


        User user = Tools.getUser();
        if (false) {//playerName.equals(Tools.PersistenceHelper.UNDEFINED)) {
            Log.d("v", "new player");
            newPlayer = true;
            handler.post(new Runnable() {
                @Override
                public void run() {
                    displaySkillChoice();
                }
            });

        } else {
            Log.d("v", "player name: " + user.name);
            enableEntry(false);
        }


        bg = new Rect(0,0, gameContext.width, gameContext.height);
        orbR = new Rect(0,0,gameContext.width,gameContext.width);
        mShader = new LinearGradient(0, 0, 0, gameContext.height, new int[] {
                Color.BLACK,Color.BLACK,Color.DKGRAY },
                null, Shader.TileMode.REPEAT);  // CLAMP MIRROR REPEAT
        shrinkTarget = gameContext.width*2/6;
        orbYTarget = (gameContext.height / 3) - (gameContext.width / 3) - (gameContext.width / 12);
        gc = gameContext;

        DrawableGameWidget dgw = new DrawableGameWidget(this,0);
        mWidgets.add(dgw);
        mDrawableAspect = dgw;
        p.setShader(mShader);
        p.setAntiAlias(true);
        p.setDither(true);
    }



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
                enableEntry(true);
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
                enableEntry(true);
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
                enableEntry(true);
            }
        });

        for (View v : viewsToFadeIn) {
            v.setAlpha(0); // make invisible to start
        }
        fade(1,1500);

    }

    private void fade(float target,int duration) {
        int i = 1;
        for (View v : viewsToFadeIn) {
            v.animate().alpha(target).setDuration(i*duration).withEndAction(new Runnable() {
                @Override
                public void run() {
                    if (v.equals(hardDiffB)) {
                        if (selected == null) {
                            newPlayer = true;
                            normalDiffB.callOnClick();
                            easyTxt.setVisibility(View.VISIBLE);
                            normalTxt.setVisibility(View.VISIBLE);
                            hardTxt.setVisibility(View.VISIBLE);

                        } else {
                            hardDiffB.setVisibility(View.GONE);
                            easyDiffB.setVisibility(View.GONE);
                            normalDiffB.setVisibility(View.GONE);
                            easyTxt.setVisibility(View.GONE);
                            normalTxt.setVisibility(View.GONE);
                            hardTxt.setVisibility(View.GONE);
                            mDrawableAspect.addAnimation(shrink(), new AnimationDoneListener() {
                                @Override
                                public void onAnimationDone() {
                                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                                        @Override
                                        public void run() {
                                            try {
                                                Thread.sleep(2000);
                                            } catch (InterruptedException e) {
                                                e.printStackTrace();
                                            }
                                            startButton.setVisibility(View.GONE);
                                        }
                                    });
                                    gc.sceneDone("Intro");
                                }});
                            mDrawableAspect.addAnimation(move());
                        }
                    }
                }
            }).start();
            i++;
        }

    }


    private void enableEntry(boolean newPlayer) {
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
                        startButton.setOnTouchListener(null);
                        if (newPlayer) {
                            startButton.setImageBitmap(on);
                            easyT.setVisibility(View.GONE);
                            fade(0, 500);
                        } else {
                            startButton.setVisibility(View.GONE);
                            gc.sceneDone("Intro");
                        }

                        break;
                }
                return false;
            }
        });
    }






    private GameAnimation shrink() {
        Log.d("v","st "+shrinkTarget+" yt"+orbYTarget);
        return new GameAnimation() {
            boolean done = false;
            int shrinkFactor = 10;
            @Override
            public boolean stepAnimate() {
                if ((orbR.width()-shrinkTarget) < shrinkFactor) {
                    shrinkFactor = orbR.width() - shrinkTarget;
                    done = true;
                    Log.d("wack","done shrink");
                }
                orbR.set(0,0,orbR.width()-shrinkFactor,orbR.height()-shrinkFactor);

                return done;
            }
        };
    }

    float orbC=0;
    private GameAnimation move() {
        return new GameAnimation() {
            boolean done = false;
            float moveFactor = 2.5f;
            ;
            @Override
            public boolean stepAnimate() {
                if (orbYTarget-orbC < moveFactor) {
                    moveFactor = orbYTarget - orbC;
                    done = true;
                    Log.d("wack","done move");
                }
                orbC+=moveFactor;
                //Log.d("orb","T: "+orbYTarget+" Y: "+orbC);

                return done;
            }
        };

    }

    DrawableGameWidget mDrawableAspect;

    @Override
    public Queue<DrawableGameWidget> getWidgets() {

        return mWidgets;
    }


    @Override
    public void draw(Canvas c) {
        c.drawRect(bg,p);
        if (orbC == 0)
            c.drawBitmap(logo, c.getWidth() / 2-logo.getWidth()/2, c.getWidth() / 2 -logo.getHeight()/2, p2);
        orbR.offsetTo((gc.width-orbR.width())/2,(int)orbC);
        c.drawBitmap(orb,null,orbR,p);
    }

    @Override
    public String getName() {
        return "intro";
    }
}

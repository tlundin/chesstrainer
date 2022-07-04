package com.teraime.chesstrainer;

import android.annotation.SuppressLint;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.database.SQLException;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowInsets;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.teraime.chesstrainer.databinding.ActivityFullscreenBinding;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class OrbChessActivity extends AppCompatActivity implements SceneManager {

    private static final boolean AUTO_HIDE = true;
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;
    private static final int UI_ANIMATION_DELAY = 300;
    //context for the app. singleton.
    private boolean mVisible;
    private TextView scoreT;
    private ImageButton endB;
    private final Handler mHideHandler = new Handler(Looper.myLooper());
    private View mContentView;
    private GameContext mGameContext;
    private SceneLoop loop;
    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            // Delayed removal of status and navigation bar
            if (Build.VERSION.SDK_INT >= 30) {
                mContentView.getWindowInsetsController().hide(
                        WindowInsets.Type.statusBars() | WindowInsets.Type.navigationBars());
            } else {
                // Note that some of these constants are new as of API 16 (Jelly Bean)
                // and API 19 (KitKat). It is safe to use them, as they are inlined
                // at compile-time and do nothing on earlier devices.
                mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
            }


        }
    };



    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };
    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */

    private ActivityFullscreenBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityFullscreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        mContentView = binding.chessBg;
        mVisible = true;
        Button flipButton = binding.flip;
        Button testButton = binding.test;
        Button resetButton = binding.reset;
        endB = binding.endB;

        scoreT = binding.score;
        SurfaceView sf = (SurfaceView)mContentView;
        SurfaceHolder surfaceHolder = sf.getHolder();
        Context context = this;

        surfaceHolder.addCallback(new SurfaceHolder.Callback() {
                                      @Override
                                      public void surfaceCreated(@NonNull SurfaceHolder surfaceHolder) {
                                          mGameContext = GameContext.create(context,surfaceHolder,OrbChessActivity.this);
                                          GameCoreographer gameIntro = new GameCoreographer(mGameContext,binding);
                                          loop = new SceneLoop(mGameContext,gameIntro);
                                          //if (gameIntro.newPlayer)
                                          StageGenerator stageGenerator = new StageGenerator(myDBH,Tools.getUser());
                                          GameContext.tp.execute(stageGenerator);
                                          loop.start();
                                      }

                                      @Override
                                      public void surfaceChanged(@NonNull SurfaceHolder surfaceHolder, int i, int i1, int i2) {

                                      }

                                      @Override
                                      public void surfaceDestroyed(@NonNull SurfaceHolder surfaceHolder) {

                                      }
                                  });


                flipButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View _view) {
                        //game.onFlipClick();
                    }
                });
        testButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View _view) {

                //game.onTestClick();
            }
        });
        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View _view) {
               // game.onResetClick();
            }
        });
        createDB();
    }


    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.

    }

    @Override
    protected void onResume() {

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        if (Build.VERSION.SDK_INT >= 30) {
            mContentView.getWindowInsetsController().hide(
                    WindowInsets.Type.statusBars() | WindowInsets.Type.navigationBars());
        } else {
            mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
        super.onResume();
    }

    private void hide() {
        // Hide UI first
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        mVisible = false;

        // Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
    }


    /**
     * Schedules a call to hide() in delay milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }

    MySQLiteHelper myDBH;
    public MySQLiteHelper getDB() {
        return myDBH;
    }
    private MySQLiteHelper createDB() {
        myDBH = new MySQLiteHelper(this.getApplicationContext());
        try {
            myDBH.createDataBase();
        } catch (IOException e) {
            throw new Error("Unable to create database");
        }
        try {
            myDBH.openDataBase();
        } catch (SQLException e) {
            throw e;
        }
        return myDBH;
    }

    @Override
    public void onSceneDone() {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        loop.kill();
        GameView gv = new GameView(this,myDBH,scoreT,endB);
        loop = new SceneLoop(mGameContext,gv);
        gv.getBoard().setFade(0);
        DrawableGameWidget bWidget = gv.getBoardWidget();
        bWidget.addAnimation(gv.getBoard().fadeBoard(1, 2000), new AnimationDoneListener() {
            @Override
            public void onAnimationDone() {
                loop.stop();
                Move m1 = new Move(0,0,1,2);
                Move m2 = new Move(7,0,6,2);
                Move m3 = new Move(0,7,1,5);
                Move m4 = new Move(7,7,6,5);
                bWidget.addAnimation(gv.getBoard().move(m1,PathFactory.Type.Jump));
                bWidget.addAnimation(gv.getBoard().move(m2,PathFactory.Type.Jump));
                bWidget.addAnimation(gv.getBoard().move(m3,PathFactory.Type.Jump));
                bWidget.addAnimation(gv.getBoard().move(m4, PathFactory.Type.Jump), new AnimationDoneListener() {
                    @Override
                    public void onAnimationDone() {
                        loop.stop();
                        Move m1 = new Move(2,0,3,2);
                        Move m2 = new Move(5,0,5,2);
                        Move m3 = new Move(2,7,3,5);
                        Move m4 = new Move(5,7,5,5);
                        bWidget.addAnimation(gv.getBoard().move(m1,PathFactory.Type.Jump));
                        bWidget.addAnimation(gv.getBoard().move(m2,PathFactory.Type.Jump));
                        bWidget.addAnimation(gv.getBoard().move(m3,PathFactory.Type.Jump));
                        bWidget.addAnimation(gv.getBoard().move(m4, PathFactory.Type.Jump), new AnimationDoneListener() {
                            @Override
                            public void onAnimationDone() {
                                loop.stop();
                                Move m1 = new Move(1,0,2,2);
                                Move m2 = new Move(6,0,4,2);
                                Move m3 = new Move(1,7,2,5);
                                Move m4 = new Move(6,7,4,5);
                                bWidget.addAnimation(gv.getBoard().move(m1,PathFactory.Type.Jump));
                                bWidget.addAnimation(gv.getBoard().move(m2,PathFactory.Type.Jump));
                                bWidget.addAnimation(gv.getBoard().move(m3,PathFactory.Type.Jump));
                                bWidget.addAnimation(gv.getBoard().move(m4, PathFactory.Type.Jump), new AnimationDoneListener() {
                                    @Override
                                    public void onAnimationDone() {
                                        loop.stop();
                                        Move m1 = new Move(3,0,3,3);
                                        Move m2 = new Move(4,0,4,3);
                                        Move m3 = new Move(3,7,3,4);
                                        Move m4 = new Move(4,7,4,4);
                                        bWidget.addAnimation(gv.getBoard().move(m1,PathFactory.Type.Jump));
                                        bWidget.addAnimation(gv.getBoard().move(m2,PathFactory.Type.Jump));
                                        bWidget.addAnimation(gv.getBoard().move(m3,PathFactory.Type.Jump));
                                        bWidget.addAnimation(gv.getBoard().move(m4, PathFactory.Type.Jump), new AnimationDoneListener() {
                                            @Override
                                            public void onAnimationDone() {
                                                loop.stop();
                                                //loop.setSpeed(100);
                                                Move m1 = new Move(3,3,1,3);
                                                Move m2 = new Move(4,3,6,3);
                                                Move m3 = new Move(3,4,1,4);
                                                Move m4 = new Move(4,4,6,4);
                                                bWidget.addAnimation(gv.getBoard().move(m1,PathFactory.Type.Linear));
                                                bWidget.addAnimation(gv.getBoard().move(m2,PathFactory.Type.Linear));
                                                bWidget.addAnimation(gv.getBoard().move(m3,PathFactory.Type.Linear));
                                                bWidget.addAnimation(gv.getBoard().move(m4, PathFactory.Type.Linear), new AnimationDoneListener() {
                                                    @Override
                                                    public void onAnimationDone() {
                                                        gv.addStageWidget();
                                                        rotate(gv,bWidget);
                                                    }
                                                });
                                                loop.start();
                                            }
                                        });
                                        loop.start();
                                    }
                                });
                                loop.start();
                            }
                        });
                        loop.start();
                    }
                });
                loop.start();
            }
        });
        loop.start();
    }

    private void rotate(GameView gv, DrawableGameWidget bWidget) {
        loop.stop();
        //loop.setSpeed(100);
        Move m1 = new Move(1,2,1,3);
        Move m2 = new Move(1,3,1,4);
        Move m3 = new Move(1,4,1,5);
        Move m4 = new Move(1,5,2,5);
        Move m5 = new Move(2,5,3,5);
        Move m6 = new Move(3,5,4,5);
        Move m7 = new Move(4,5,5,5);
        Move m8 = new Move(5,5,6,5);
        Move m9 = new Move(6,5,6,4);
        Move m10 = new Move(6,4,6,3);
        Move m11 = new Move(6,3,6,2);
        Move m12 = new Move(6,2,5,2);
        Move m13 = new Move(5,2,4,2);
        Move m14 = new Move(4,2,3,2);
        Move m15 = new Move(3,2,2,2);
        Move m16 = new Move(2,2,1,2);

        bWidget.addAnimation(gv.getBoard().move(m1,PathFactory.Type.Linear));
        bWidget.addAnimation(gv.getBoard().move(m2,PathFactory.Type.Linear));
        bWidget.addAnimation(gv.getBoard().move(m3,PathFactory.Type.Linear));
        bWidget.addAnimation(gv.getBoard().move(m4,PathFactory.Type.Linear));
        bWidget.addAnimation(gv.getBoard().move(m5,PathFactory.Type.Linear));
        bWidget.addAnimation(gv.getBoard().move(m6,PathFactory.Type.Linear));
        bWidget.addAnimation(gv.getBoard().move(m7,PathFactory.Type.Linear));
        bWidget.addAnimation(gv.getBoard().move(m8,PathFactory.Type.Linear));
        bWidget.addAnimation(gv.getBoard().move(m9,PathFactory.Type.Linear));
        bWidget.addAnimation(gv.getBoard().move(m10,PathFactory.Type.Linear));
        bWidget.addAnimation(gv.getBoard().move(m11,PathFactory.Type.Linear));
        bWidget.addAnimation(gv.getBoard().move(m12,PathFactory.Type.Linear));
        bWidget.addAnimation(gv.getBoard().move(m13,PathFactory.Type.Linear));
        bWidget.addAnimation(gv.getBoard().move(m14,PathFactory.Type.Linear));
        bWidget.addAnimation(gv.getBoard().move(m15,PathFactory.Type.Linear));
        bWidget.addAnimation(gv.getBoard().move(m16, PathFactory.Type.Linear), new AnimationDoneListener() {
            @Override
            public void onAnimationDone() {
                rotate(gv,bWidget);
            }
        });
        loop.start();
    }
}
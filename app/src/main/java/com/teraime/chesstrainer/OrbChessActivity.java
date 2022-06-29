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
import java.util.concurrent.ThreadPoolExecutor;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class OrbChessActivity extends AppCompatActivity {

    private static final boolean AUTO_HIDE = true;
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;
    private static final int UI_ANIMATION_DELAY = 300;
    //context for the app. singleton.
    private boolean mVisible;
    private TextView scoreT;
    private final Handler mHideHandler = new Handler(Looper.myLooper());
    private View mContentView;
    private GameContext mGameContext;
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
        ImageButton endB = binding.endB;

        scoreT = binding.score;
        SurfaceView sf = (SurfaceView)mContentView;
        SurfaceHolder surfaceHolder = sf.getHolder();
        Context context = this;

        surfaceHolder.addCallback(new SurfaceHolder.Callback() {
                                      @Override
                                      public void surfaceCreated(@NonNull SurfaceHolder surfaceHolder) {
                                          mGameContext = GameContext.create(context,surfaceHolder);
                                          SceneLoop loop = new SceneLoop(mGameContext,new GameCoreographer(mGameContext,binding));
                                          //new GameView(this,createDB(),scoreT,endB);
                                      }

                                      @Override
                                      public void surfaceChanged(@NonNull SurfaceHolder surfaceHolder, int i, int i1, int i2) {

                                      }

                                      @Override
                                      public void surfaceDestroyed(@NonNull SurfaceHolder surfaceHolder) {

                                      }
                                  }


                flipButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View _view) {
                        game.onFlipClick();
                    }
                });
        testButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View _view) {
                game.onTestClick();
            }
        });
        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View _view) {
                game.onResetClick();
            }
        });

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
}
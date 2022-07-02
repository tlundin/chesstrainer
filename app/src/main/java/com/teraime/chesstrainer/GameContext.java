package com.teraime.chesstrainer;

import android.content.Context;
import android.graphics.Canvas;
import android.view.SurfaceHolder;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class GameContext {

    Context context;
    final static ThreadPoolExecutor tp = (ThreadPoolExecutor) Executors.newCachedThreadPool();
    int width;
    int height;
    SurfaceHolder sh;
    static GameContext gc;
    SceneManager sceneManager;

    public static GameContext create(Context ct, SurfaceHolder sh, SceneManager sceneManager) {
        if (gc == null)
            gc = new GameContext();
        gc.context=ct;
        gc.sh = sh;
        gc.width = sh.getSurfaceFrame().width();
        gc.height = sh.getSurfaceFrame().height();
        gc.sceneManager = sceneManager;
        return gc;
    }

    public void sceneDone() {
        sceneManager.onSceneDone();
    }
}

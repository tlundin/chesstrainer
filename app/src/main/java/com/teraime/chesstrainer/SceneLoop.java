package com.teraime.chesstrainer;

import android.graphics.Canvas;
import android.util.Log;

import java.util.concurrent.atomic.AtomicBoolean;

public class SceneLoop {
    private final Runnable mLoop;
    AtomicBoolean alive = new AtomicBoolean(true);
    public SceneLoop(GameContext gc, SceneContext scene) {
        mLoop = new Runnable() {
            Canvas c;
            @Override
            public void run() {

                int i = 0; long t1,diff;
                while (alive.get()) {
                    Log.d("vovo","gtzz");
                    t1 = System.currentTimeMillis();
                    c = gc.sh.lockCanvas();

                    for (DrawableGameWidget gw : scene.getWidgets()) {
                        c.save();
                        c.translate(0,gw.getOffset());
                        gw.stepAnimate();
                        gw.draw(c);

                    }

                    gc.sh.unlockCanvasAndPost(c);

                    diff = System.currentTimeMillis() - t1;
                    if (diff < 60) {
                        try {
                            Thread.sleep(60 - diff);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    } else
                        Log.d("v", "t" + i++ + " (" + diff + " )");
                }
            }
        };


    }

    public void start() {
        alive.set(true);
        GameContext.tp.submit(mLoop);
    }
    public void kill() {
        alive.set(false);
    }
    public void addDrawable(DrawableGameWidget widget) {

    }
}

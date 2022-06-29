package com.teraime.chesstrainer;

import android.graphics.Canvas;
import android.util.Log;

import java.util.concurrent.atomic.AtomicBoolean;

public class SceneLoop {
    AtomicBoolean alive = new AtomicBoolean(true);

    public SceneLoop(GameContext gc, SceneContext scene) {

        Runnable mainloop = new Runnable() {
            Canvas c;
            @Override
            public void run() {

                int i = 0; long t1,diff;
                while (alive.get()) {
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

                    try {
                        Thread.sleep(60-diff);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    Log.d("v", "t" + i++ + " (" + (System.currentTimeMillis()-t1) + " )");
                }
            }
        };


    }

    public void kill() {
        alive.set(false);
    }
    public void addDrawable(DrawableGameWidget widget) {

    }
}

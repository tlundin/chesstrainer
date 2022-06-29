package com.teraime.chesstrainer;

import android.graphics.Canvas;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;

public class MainGameLoop {
    AtomicBoolean alive = new AtomicBoolean(true);
    Scene scene;


    public void setScene(Scene scene) {
        this.scene = scene;
    }

    public MainGameLoop(GameContext gc) {

        Runnable mainloop = new Runnable() {
            Canvas c;
            @Override
            public void run() {


                int i = 0; long t1,diff;
                while (alive.get()) {
                    if (scene != null) {
                        t1 = System.currentTimeMillis();
                        c = gc.sh.lockCanvas();
                        for (DrawableGameWidget gw : gameWidgets) {
                            c.save();
                            c.translate(0, );
                            gw.draw(c);
                        }
                        gc.sh.unlockCanvasAndPost(c);
                    }
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

        gc.tp.submit(mainloop);
    }

    public void kill() {
        alive.set(false);
    }
    public void addDrawable(DrawableGameWidget widget) {

    }
}

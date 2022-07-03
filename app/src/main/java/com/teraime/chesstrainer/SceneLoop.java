package com.teraime.chesstrainer;

import android.graphics.Canvas;
import android.util.Log;

import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;

public class SceneLoop {
    static int FRAME_RATE = 30;
    private final Runnable mLoop;
    AtomicBoolean alive = new AtomicBoolean(false);
    AtomicBoolean stopped = new AtomicBoolean(false);
    public SceneLoop(GameContext gc, SceneContext scene) {
        mLoop = new Runnable() {
            Canvas c;
            @Override
            public void run() {
                int i = 0; long t1,diff;
                while (alive.get()) {
                    t1 = System.currentTimeMillis();
                    if (!stopped.get()) {
                        c = gc.sh.lockCanvas();
                        if (c == null)
                            SceneLoop.this.kill();
                        else {
                            scene.getWidgets().forEach(gw -> {
                                c.save();
                                c.translate(0, gw.getOffset());
                                gw.stepAnimate();
                                gw.draw(c);
                                c.restore();
                            });
                            gc.sh.unlockCanvasAndPost(c);
                        }
                    }
                        diff = System.currentTimeMillis() - t1;
                        if (diff < FRAME_RATE) {
                            try {
                                Thread.sleep(FRAME_RATE - diff);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }// else
                        //   Log.d("v", "t" + i++ + " (" + diff + " )");

                }
                Log.d("v","I died");
            }
        };


    }

    public void start() {
        stopped.set(false);
        if (!alive.get()) {
            alive.set(true);
            GameContext.tp.execute(mLoop);
        }
    }
    public void kill() {
        Log.d("v","kill call");
        alive.set(false);

    }

    public void stop() {
        stopped.set(true);
    }

    public void setSpeed(int i) {
        FRAME_RATE = i;
    }
}

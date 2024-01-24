package com.teraime.chesstrainer;

import android.graphics.Canvas;
import android.util.Log;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class SceneLoop {
    public static int FRAME_RATE = 30;
    private final Runnable mLoop;
    AtomicBoolean alive = new AtomicBoolean(false);
    AtomicInteger stopped = new AtomicInteger(-1);
    Queue<DoneCallback> doneQueue = new ConcurrentLinkedQueue<>();
    public SceneLoop(GameContext gc, SceneContext scene) {
        mLoop = new Runnable() {
            Canvas c;
            @Override
            public void run() {
                int i = 0; long t1,diff;
                while (alive.get()) {
                    t1 = System.currentTimeMillis();
                    if (stopped.get()!=0) {
                        stopped.decrementAndGet();
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
                    } else {
                        doneQueue.forEach(cb->{
                            cb.done();
                            doneQueue.remove(cb);
                        });
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
        stopped.set(-1);
        if (!alive.get()) {
            alive.set(true);
            GameContext.threadExecutorPool.execute(mLoop);
        }
    }
    public void kill() {
        Log.d("v","kill call");
        alive.set(false);

    }

    public void stop(DoneCallback stoppedCb) {
        doneQueue.add(stoppedCb);
        stopped.set(25);
    }
    public void stop() {
        stopped.set(0);
    }

    public void setSpeed(int i) {
        FRAME_RATE = i;
    }
}

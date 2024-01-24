package com.teraime.chesstrainer;

import android.graphics.Canvas;

import com.teraime.chesstrainer.widgets.GameWidget;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class DrawableGameWidget {

        final GameWidget mWidget;
        final Queue<GameAnimation> animations;
        final Queue<AnimationDoneListener> listenerMap;
        final int mOffset;

        public DrawableGameWidget(GameWidget widget, int offset) {
                mWidget = widget;
                mOffset = offset;
                animations = new ConcurrentLinkedQueue<>();
                listenerMap = new ConcurrentLinkedQueue<>();
        }

        public int getOffset() {
                //Log.d("v","offset for "+mWidget.getName()+" is "+mOffset);
                return mOffset;
        }

        public void addAnimation(GameAnimation gAnim, AnimationDoneListener animationDoneListener ) {
                listenerMap.add(animationDoneListener);
                animations.add(gAnim);
        }
        public void addAnimation(GameAnimation gAnim ) {
                animations.add(gAnim);
        }

        public void draw(Canvas c) {
                mWidget.draw(c);
        }

        public void stepAnimate() {
                animations.forEach(gameAnimation ->  {
                        if (gameAnimation.stepAnimate()) {
                                animations.remove(gameAnimation);
                                if (animations.isEmpty())
                                        if (!listenerMap.isEmpty()) {
                                                listenerMap.forEach(listener -> {
                                                        listenerMap.remove(listener);
                                                        listener.onAnimationDone();
                                                });
                                        }
                        }
                });
        }
}
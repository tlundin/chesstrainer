package com.teraime.chesstrainer;

import android.graphics.Canvas;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

class DrawableGameWidget {

        final GameWidget mWidget;
        final Queue<GameAnimation> animations;
        final Queue<AnimationDoneListener> listenerMap;
        final int mOffset;

        DrawableGameWidget(GameWidget widget, int offset) {
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
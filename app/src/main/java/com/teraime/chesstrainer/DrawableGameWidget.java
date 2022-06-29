package com.teraime.chesstrainer;

import android.graphics.Canvas;

import java.util.ArrayList;
import java.util.List;

class DrawableGameWidget {

        final GameWidget mWidget;
        final List<GameAnimation> animations;
        final int mOffset;

        DrawableGameWidget(GameWidget widget, int offset) {
                mWidget = widget;
                mOffset = offset;
                animations = new ArrayList<>();
        }

        public int getOffset() {
                return mOffset;
        }

        public void addAnimation(GameAnimation gAnim ) {
                animations.add(gAnim);
        }
        public void draw(Canvas c) {
                mWidget.draw(c);
        }

        public void stepAnimate() {
                for (GameAnimation gameAnimation:animations) {
                        if (gameAnimation.stepAnimate()) {
                                animations.remove(gameAnimation);
                        }
                }
        }
}
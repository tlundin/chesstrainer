package com.teraime.chesstrainer;

import android.graphics.Canvas;

class DrawableGameWidget {

        Widget mWidget;
        int mOffset = 0;

        DrawableGameWidget(GameWidget widget, int offset) {
                mWidget = widget;
                mOffset = offset;
        }

        public void draw(Canvas c) {
                mWidget.draw(c);
        }

        public boolean stepAnimate() {
                mWidget.stepAnimate();
        }
}
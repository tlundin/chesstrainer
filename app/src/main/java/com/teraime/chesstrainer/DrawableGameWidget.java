package com.teraime.chesstrainer;

import android.graphics.Canvas;

public abstract class DrawableGameWidget {

        public int offset = 0;
        public abstract void draw(Canvas c);

}
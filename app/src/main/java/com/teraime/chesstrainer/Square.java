package com.teraime.chesstrainer;

import android.graphics.Rect;

public class Square {

    private Rect mRect;
    boolean white;

    public Square(int left, int top, int right, int bottom, boolean isWhite) {
        mRect = new Rect(left,top,right,bottom);
        white = isWhite;
    }

    public Rect getRect() {
        return mRect;
    }
}

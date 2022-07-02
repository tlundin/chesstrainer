package com.teraime.chesstrainer;

import android.graphics.Point;

public class MoveVector {

    Point[] mPoints;
    float[] mScale;

    public MoveVector(Point[] mPoints, float[] mScale) {
        this.mPoints = mPoints;
        this.mScale = mScale;
    }
}

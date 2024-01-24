package com.teraime.chesstrainer;

import android.graphics.Point;

public class MoveVector {

    public Point[] mPoints;
    public float[] mScale;

    public MoveVector(Point[] mPoints, float[] mScale) {
        this.mPoints = mPoints;
        this.mScale = mScale;
    }
}

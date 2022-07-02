package com.teraime.chesstrainer;

import android.graphics.Point;
import android.util.Log;

import java.util.Random;

public class PathFactory {

    public enum Type {
        Linear,
        Jump
    }

    public static MoveVector generate(Type type, Point movingPiecePosition, Point movingPieceDestination) {
        int diff_X = movingPieceDestination.x - movingPiecePosition.x;
        int diff_Y = movingPieceDestination.y - movingPiecePosition.y;
        int pointNum = 16;
        Point[] pointList = new Point[pointNum];
        float[] scaleList = new float[pointNum];
        switch (type) {
            case Linear:
                int interval_X = diff_X / (pointNum);
                int interval_Y = diff_Y / (pointNum);
                for (int i = 0; i < pointNum; i++) {
                    pointList[i] = new Point(movingPiecePosition.x + interval_X * i, movingPiecePosition.y + interval_Y * i);
                    scaleList[i] = 0;
                }
                break;
            case Jump:
                Random r = new Random();
                float g = r.nextFloat()/10f; //0.05f;
                float v0 = g*(pointNum-1)/2;
                float vt=v0;
                float h=0;
                interval_X = diff_X / (pointNum);
                interval_Y = diff_Y / (pointNum);
                for (int i = 0; i < pointNum; i++) {
                    vt = v0 - g*i;
                    h = v0*i-(1f/2f*g*i*i);
                    Log.d("v", "v" + i + ": " + vt+" h: "+h);
                    pointList[i] = new Point(movingPiecePosition.x + interval_X * i, movingPiecePosition.y + interval_Y * i);
                    scaleList[i] = h;
                }
        }
        return new MoveVector(pointList,scaleList);
    }


}

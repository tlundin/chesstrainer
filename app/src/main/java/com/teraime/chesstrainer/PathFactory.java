package com.teraime.chesstrainer;

import android.graphics.Point;

public class PathFactory {

    public enum Type {
        Linear
    }

    public static Point[] generate(Type linear, Point movingPiecePosition, Point movingPieceDestination) {
        int diff_X = movingPieceDestination.x - movingPiecePosition.x;
        int diff_Y = movingPieceDestination.y - movingPiecePosition.y;
        int pointNum = 8;

        int interval_X = diff_X / (pointNum );
        int interval_Y = diff_Y / (pointNum );

        Point[] pointList = new Point[pointNum];
        for (int i = 0; i < pointNum; i++)
        {
            pointList[i]= new Point(movingPiecePosition.x + interval_X * i, movingPiecePosition.y + interval_Y*i);
        }
        return pointList;
    }


}

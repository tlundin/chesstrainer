package com.teraime.chesstrainer;

import android.graphics.Canvas;
import android.graphics.Rect;

public class Board
{
    BoxOfChessMen set = new BoxOfChessMen();
    final ScaleOptions mSize;
    final StyleOptions pieceStyle,boardStyle;

    public Board(ScaleOptions mSize, StyleOptions boardStyle, StyleOptions pieceStyle, int size_with_borders) {
        final float border_thickness_as_percentage = .10f;
        float sizef = scale(size_with_borders);
        this.mSize = mSize;
        this.pieceStyle = pieceStyle;
        this.boardStyle = boardStyle;
        Rect bg = new Rect(0,0,sizes,sizes);
        Rect L_Edge = new Rect(0,0,border_thickness,_size);
        Rect R_Edge = new Rect(0,0,border_thickness,_size);
    }

    int _size = (int)sizef;
    int border_thickness = (int) (w*border_thickness_as_percentage);
    public enum StyleOptions {
        plain,
        fancy
    }

    public enum ScaleOptions {
        MAX,
        HALF,
        MIN,
        ABSOLUTE
    }

    public void onDraw(Canvas c) {
        c.getWidth();

    }



}

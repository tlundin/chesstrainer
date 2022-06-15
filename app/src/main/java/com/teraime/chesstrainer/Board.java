package com.teraime.chesstrainer;

import android.graphics.Canvas;
import android.graphics.Rect;

public class Board
{
    BoxOfChessMen set = new BoxOfChessMen();
    final StyleOptions pieceStyle,boardStyle;
    final static float border_thickness_as_percentage = .10f;

    public Board(ScaleOptions scaleOption, StyleOptions boardStyle, StyleOptions pieceStyle, int size_with_borders) {
        int _size = scale(size_with_borders,scaleOption);
        int border_thickness = (int) (_size*border_thickness_as_percentage);
        this.pieceStyle = pieceStyle;
        this.boardStyle = boardStyle;
        Rect bg = new Rect(0,0,_size,_size);
        Rect L_Edge = new Rect(0,0,border_thickness,_size);
        Rect R_Edge = new Rect(0,0,border_thickness,_size);
        Rect B_Edge = new Rect(0,0,_size,border_thickness);
        Rect T_Edge = new Rect(0,0,_size,border_thickness);
    }

    private int scale(int size,ScaleOptions scaleOption) {
        switch (scaleOption) {
            case MAX:
            default:
                return size;
        }
    }



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

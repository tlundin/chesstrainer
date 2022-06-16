package com.teraime.chesstrainer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;

public class Board
{
    BoxOfChessMen set = new BoxOfChessMen();
    final StyleOptions pieceStyle,boardStyle;
    final static float border_thickness_as_percentage = .10f;
    final Rect boardRect,gridRect,L_Edge,R_Edge,B_Edge,T_Edge;
    final Paint edgeColor = new Paint();
    final Paint p = new Paint();
    final Bitmap edgeHorisontal,edgeVertical,w_square,b_square;
    final Bitmap w_knight,w_bishop,w_king,w_rook,w_pawn,w_queen;
    final Bitmap b_knight,b_bishop,b_king,b_rook,b_pawn,b_queen;
    final Bitmap[] pieceBox;
    final Boolean hasEdge;
    final Rect[] squares;
    final int border_thickness,gridSize;
    private ChessPosition mPosition=null;
    private boolean isFlipped=false;

    public Board(Context context, ScaleOptions scaleOption, StyleOptions boardStyle, StyleOptions pieceStyle, int size_with_borders) {
        int _size = scale(size_with_borders,scaleOption);
        this.pieceStyle = pieceStyle;
        this.boardStyle = boardStyle;
        boardRect = new Rect(0,0,_size,_size);

        switch (boardStyle) {
            case oak:
                border_thickness = (int) (_size*border_thickness_as_percentage)/2;
                hasEdge = true;
                L_Edge = new Rect(0,0,border_thickness,_size);
                R_Edge = new Rect(_size-border_thickness,0,_size,_size);
                T_Edge = new Rect(0,0,_size,border_thickness);
                B_Edge = new Rect(0,_size-border_thickness,_size,_size);
                edgeHorisontal=BitmapFactory.decodeResource(context.getResources(),R.drawable.horisontal_edge_wood);
                edgeVertical=BitmapFactory.decodeResource(context.getResources(),R.drawable.vertical_edge_wood);
                gridSize = size_with_borders-border_thickness*2;
            break;
            default:
                border_thickness = 0;
                gridSize = size_with_borders;
                hasEdge = false;
                edgeHorisontal = null;
                edgeVertical = null;
                L_Edge=R_Edge=T_Edge=B_Edge=null;
            break;
        }
        gridRect = new Rect(border_thickness,border_thickness,_size-border_thickness,_size-border_thickness);
        squares = new Rect[64];
        w_square = BitmapFactory.decodeResource(context.getResources(),R.drawable.white_square_wood);
        b_square = BitmapFactory.decodeResource(context.getResources(),R.drawable.dark_square_wood);
        int top = 0;int left=0;int bottom=0;int right=0;
        int i=0;
        int square_size = gridSize/8;
        for(int row=0; row<8 ; row++) {
            for (int column=0; column<8 ; column++) {
                left   = border_thickness+column*square_size;
                top    = border_thickness+row*square_size;
                right  = left+square_size;
                bottom = top+square_size;
                squares[i++] = new Rect(left,top,right,bottom);
            }
        }
        switch (pieceStyle) {
            case oak:
            default:
                w_knight = BitmapFactory.decodeResource(context.getResources(),R.drawable.w_knight);
                w_bishop = BitmapFactory.decodeResource(context.getResources(),R.drawable.w_bishop);
                w_king = BitmapFactory.decodeResource(context.getResources(),R.drawable.w_king);
                w_rook = BitmapFactory.decodeResource(context.getResources(),R.drawable.w_rook);
                w_pawn = BitmapFactory.decodeResource(context.getResources(),R.drawable.w_pawn);
                w_queen = BitmapFactory.decodeResource(context.getResources(),R.drawable.w_queen);
                b_knight = BitmapFactory.decodeResource(context.getResources(),R.drawable.b_knight);
                b_bishop = BitmapFactory.decodeResource(context.getResources(),R.drawable.b_bishop);
                b_king = BitmapFactory.decodeResource(context.getResources(),R.drawable.b_king);
                b_rook = BitmapFactory.decodeResource(context.getResources(),R.drawable.b_rook);
                b_pawn = BitmapFactory.decodeResource(context.getResources(),R.drawable.b_pawn);
                b_queen = BitmapFactory.decodeResource(context.getResources(),R.drawable.b_queen);
                pieceBox = new Bitmap[]{null,w_king,w_queen,w_bishop,w_knight,w_rook,w_pawn,b_king,b_queen,b_bishop,b_knight,b_rook,b_pawn};
        }
    }

    private int scale(int size,ScaleOptions scaleOption) {
        switch (scaleOption) {
            case MAX:
            default:
                return size;
        }
    }

    public void setupPosition(ChessPosition position) {
        mPosition = position;
    }

    public void dragIfPiece(int x, int y) {
        int gridSize = gridRect.width()/8;
        if (gridRect.contains(x,y)) {
            int column = (x-border_thickness)/gridSize;
            int row = (y-border_thickness)/gridSize;
            Log.d("x","col:"+column+" row:"+row);
            Log.d("x",ChessConstants.pieceNames[mPosition.get(column,row)]);
        }
    }


    public enum StyleOptions {
        plain,
        fancy,
        oak
    }

    public enum ScaleOptions {
        MAX,
        HALF,
        MIN,
        ABSOLUTE
    }

    public void flip() {
        isFlipped = !isFlipped;
    }
    public void onDraw(Canvas c) {
        c.getWidth();
        if (hasEdge) {
            c.drawBitmap(edgeHorisontal, null, T_Edge, p);
            c.drawBitmap(edgeHorisontal, null, B_Edge, p);
            c.drawBitmap(edgeVertical, null, L_Edge, p);
            c.drawBitmap(edgeVertical, null, R_Edge, p);
        }
        int i=0;
        Bitmap square=w_square;
        for(int row=0; row<8 ; row++) {
            square=(square==w_square)?b_square:w_square;
            for (int column = 0; column < 8; column++) {
                square=(square==w_square)?b_square:w_square;
                c.drawBitmap(square,null,squares[i],p);
                int flip_row = isFlipped?7-row:row;
                if (!mPosition.isEmpty(column,flip_row))
                    c.drawBitmap(pieceBox[mPosition.get(column,flip_row)],null,squares[i],p);
                i++;
            }
        }

    }



}

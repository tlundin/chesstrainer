package com.teraime.chesstrainer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;


public class Board
{
    BoxOfChessMen set = new BoxOfChessMen();
    final StyleOptions pieceStyle,boardStyle;
    final static float border_thickness_as_percentage = .10f;
    final Rect boardRect,gridRect,L_Edge,R_Edge,B_Edge,T_Edge;
    final Paint edgeColor = new Paint();
    final Paint bgColor = new Paint(Color.BLACK);
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
    private Position movingPieceInitialPosition,Outside;

    public Board(Context context, ScaleOptions scaleOption, StyleOptions boardStyle, StyleOptions pieceStyle, int size_with_borders) {
        int _size = scale(size_with_borders,scaleOption);
        this.pieceStyle = pieceStyle;
        this.boardStyle = boardStyle;
        boardRect = new Rect(0,0,_size,_size);
        Outside = new Position(-1,-1);
        movingPieceInitialPosition =Outside;
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

    public Bitmap[] getPieceBox() {
        return pieceBox;
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

    public int dragIfPiece(int x, int y) {
        if (isInside(x,y)) {
            Position p = calculateColRow(x,y);
            Log.d("x","col:"+p.getColumn()+" row:"+p.getRow());
            int piece = mPosition.get(p.getColumn(),p.getRow());
            if (piece != ChessConstants.B_EMPTY) {
                movingPieceInitialPosition = p;
                return piece;
            }
        }
        return -1;
    }

    private Position calculateColRow(int x, int y) {
        int column = (x-border_thickness)/getSquareSize();
        int row = (y-border_thickness)/getSquareSize();
        int fliprow = isFlipped?7-row:row;
        return new Position(column,fliprow);
    }
    private Position calculateXY(int col, int row) {
        int x = col*getSquareSize()+border_thickness;
        int y = row*getSquareSize()+border_thickness;
        return new Position(x,y);
    }

    public void dropDraggedPiece(int x, int y, int piece) {
        if (isInside(x,y)) {
            Position p = calculateColRow(x, y);
            mPosition.put(movingPieceInitialPosition.getColumn(), movingPieceInitialPosition.getRow(), ChessConstants.B_EMPTY);
            mPosition.put(p.getColumn(), p.getRow(), piece);
        }
        movingPieceInitialPosition = Outside;
    }

    public boolean isInside(int x, int y) {
        return gridRect.contains(x,y);
    }

    public int getSquareSize() {
        return gridSize/8;
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

    public void move(int colFrom,int rowFrom, int colTo, int rowTo) {
       final Handler moveHandler = new Handler(Looper.myLooper());
       Bitmap piecebmp = pieceBox[mPosition.get(colFrom,rowFrom)];
       Position p = calculateXY(colFrom,rowFrom);
       movingPieceInitialPosition=p;
       moveHandler.post(new Runnable() {
           @Override
           public void run() {

           }
       });
    }

    public void onDraw(Canvas c) {
        if (c != null) {
            c.drawColor(Color.BLACK);
            if (hasEdge) {
                c.drawBitmap(edgeHorisontal, null, T_Edge, p);
                c.drawBitmap(edgeHorisontal, null, B_Edge, p);
                c.drawBitmap(edgeVertical, null, L_Edge, p);
                c.drawBitmap(edgeVertical, null, R_Edge, p);
            }
            int i = 0;
            Bitmap square = w_square;
            for (int row = 0; row < 8; row++) {
                square = (square == w_square) ? b_square : w_square;
                for (int column = 0; column < 8; column++) {
                    square = (square == w_square) ? b_square : w_square;
                    c.drawBitmap(square, null, squares[i], p);
                    int flip_row = isFlipped ? 7 - row : row;
                    if (!mPosition.isEmpty(column, flip_row))
                        if (!movingPieceInitialPosition.equals(column, flip_row))
                            c.drawBitmap(pieceBox[mPosition.get(column, flip_row)], null, squares[i], p);
                    i++;
                }
            }
        }

    }



}

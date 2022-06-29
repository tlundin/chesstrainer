package com.teraime.chesstrainer;

import static com.teraime.chesstrainer.PathFactory.Type.Linear;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;


public class Board implements GameWidget
{
    final StyleOptions pieceStyle,boardStyle;
    final static float border_thickness_as_percentage = .10f;
    final Rect boardRect,gridRect,L_Edge,R_Edge,B_Edge,T_Edge;
    final Paint edgeColor = new Paint();
    final Paint bgColor = new Paint(Color.BLACK);
    final Paint p = new Paint();
    final Paint sg = new Paint();
    final Paint wh = new Paint();
    final Bitmap edgeHorisontal,edgeVertical,w_square,b_square;
    final Bitmap w_knight,w_bishop,w_king,w_rook,w_pawn,w_queen;
    final Bitmap b_knight,b_bishop,b_king,b_rook,b_pawn,b_queen;

    final Bitmap[] pieceBox;
    final Boolean hasEdge;
    final Square[] squares;
    final Rect[] pieceSquares;
    final int border_thickness,gridSize;
    private ChessPosition mPosition=null;
    private boolean whiteOnTop = false;
    private Cord movingPieceInitialPosition,Outside;
    private Bitmap movingPieceBmp,okAnimateBmp;
    private Rect dragRect,moveRect,okRect;
    private SurfaceHolderCallback surfer;
    private int boardOffset;
    final float centerX,centerY;
    private Cord okLocation;
    private boolean dragActive = false, moveActive = false;

    public Board() {

    }
    public Board(Context context, ScaleOptions scaleOption, StyleOptions boardStyle, StyleOptions pieceStyle, int size_with_borders, int bo, SurfaceHolderCallback _surfer) {
        int _size = scale(size_with_borders,scaleOption);
        this.boardOffset = bo;
        this.pieceStyle = pieceStyle;
        this.boardStyle = boardStyle;
        surfer = _surfer;
        sg.setColor(Color.parseColor("#798A80"));
        wh.setColor(Color.parseColor("#F8F4EC"));
        boardRect = new Rect(0,0,_size,_size);
        Outside = new Cord(-1,-1);
        movingPieceInitialPosition =Outside;
        centerX = boardRect.width()/2;
        centerY = boardRect.height()/2;
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
        squares = new Square[64]; pieceSquares = new Rect[64];
        w_square = BitmapFactory.decodeResource(context.getResources(),R.drawable.white_square_wood);
        b_square = BitmapFactory.decodeResource(context.getResources(),R.drawable.dark_square_wood);
        int top = 0;int left=0;int bottom=0;int right=0;
        int i=0;
        int square_size = getSquareSize();
        dragRect = new Rect(0,0,(int)(square_size*GameView.DragRectSize),(int)(square_size*GameView.DragRectSize));
        moveRect = new Rect(0,0,square_size,square_size);
        boolean isWhite=false;
        for(int row=0; row<8 ; row++) {
            isWhite = !isWhite;
            for (int column=0; column<8 ; column++) {
                left   = border_thickness+column*square_size;
                top    = border_thickness+row*square_size;
                right  = left+square_size;
                bottom = top+square_size;
                squares[i] = new Square(left,top,right,bottom,isWhite);
                isWhite = !isWhite;
                pieceSquares[i++] = new Rect(left,top,right,bottom);
            }
        }
        okRect = new Rect(0,0,square_size/2,square_size/2);
        okAnimateBmp = BitmapFactory.decodeResource(context.getResources(),R.drawable.ok_icon);
        switch (pieceStyle) {
            case fancy:
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

                break;
            case plain:
            default:
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inScaled = false;
                Bitmap piecesBMP = BitmapFactory.decodeResource(context.getResources(),R.drawable.chess_plain,options);
                Log.d("farko","W "+piecesBMP.getWidth()+"H: "+piecesBMP.getHeight());
                b_rook = Bitmap.createBitmap(piecesBMP,0,50,300,300);
                b_bishop =  Bitmap.createBitmap(piecesBMP,300,50,300,300);
                b_queen = Bitmap.createBitmap(piecesBMP,600,50,300,300);
                b_king = Bitmap.createBitmap(piecesBMP,900,50,300,300);
                b_knight = Bitmap.createBitmap(piecesBMP,1200,50,300,300);
                b_pawn = Bitmap.createBitmap(piecesBMP,1500,50,300,300);
                w_rook = Bitmap.createBitmap(piecesBMP,0,425,300,300);
                w_bishop =  Bitmap.createBitmap(piecesBMP,300,425,300,300);
                w_queen = Bitmap.createBitmap(piecesBMP,600,425,300,300);
                w_king = Bitmap.createBitmap(piecesBMP,900,425,300,300);
                w_knight = Bitmap.createBitmap(piecesBMP,1200,425,300,300);
                w_pawn = Bitmap.createBitmap(piecesBMP,1500,425,300,300);
            break;
        }
        pieceBox = new Bitmap[]{null,w_king,w_queen,w_bishop,w_knight,w_rook,w_pawn,b_king,b_queen,b_bishop,b_knight,b_rook,b_pawn};
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

    public int getPiece(int x, int y) {
        if (isInside(x,y)) {
            Cord p = calculateColRow(x,y);
            Log.d("x","col:"+p.getColumn()+" row:"+p.getRow());
            int piece = mPosition.get(p.getColumn(),p.getRow());
            if (piece != ChessConstants.B_EMPTY) {
                return piece;
            }
        }
        return -1;
    }

    public void startDrag(Cord p, int draggedPiece) {
        Bitmap draggedPieceBmp = getPieceBox()[draggedPiece];
        int squareSize = getSquareSize();
        movingPieceBmp=draggedPieceBmp;
        movingPieceInitialPosition = p;
        dragActive=true;

    }

    public Cord calculateColRow(int x, int y) {
        int column = (x-border_thickness)/getSquareSize();
        int row = (y-border_thickness)/getSquareSize();
        int flip_row = whiteOnTop ?7-row:row;
        int flip_column = whiteOnTop ?7-column:column;
        return new Cord(flip_column,flip_row);
    }
    private Point calculateXYFromGrid(int column, int row) {
        int flip_row = whiteOnTop ?7-row:row;
        int flip_column = whiteOnTop ?7-column:column;
        int x = flip_column*getSquareSize()+border_thickness;
        int y = flip_row*getSquareSize()+border_thickness;
        return new Point(x,y);
    }

    public BasicMove dropMovingPiece(Point dropPoint, int piece) {
        int x = dropPoint.x;
        int y = dropPoint.y;
        Cord movingPieceEndPosition;
        BasicMove bMove = null;
        if (isInside(x,y)) {
            movingPieceEndPosition = calculateColRow(x, y);
            mPosition.put(movingPieceInitialPosition.getColumn(), movingPieceInitialPosition.getRow(), ChessConstants.B_EMPTY);
            mPosition.put(movingPieceEndPosition.getColumn(), movingPieceEndPosition.getRow(), piece);
            if (!(movingPieceInitialPosition.equals(movingPieceEndPosition)))
                bMove = new BasicMove(movingPieceInitialPosition,movingPieceEndPosition);
        }

        movingPieceInitialPosition = Outside;
        movingPieceBmp = null;
        dragActive=false;
        return bMove;
    }

    public boolean isInside(int x, int y) {
        return gridRect.contains(x,y);
    }

    public int getSquareSize() {
        return gridSize/8;
    }



    public void setWhiteOnTop() {
        whiteOnTop=true;
    }
    public void setBlackOnTop() {
        whiteOnTop=false;
    }

    @Override
    public void setup(GameContext gc, int offset) {

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
        whiteOnTop = !whiteOnTop;
    }

    final Handler animationHandler = new Handler(Looper.myLooper());


    public void move(Move move) {
        surfer.moveIsActive();
       movingPieceInitialPosition=move.getFromCord();
       final Point movingPiecePosition=calculateXYFromGrid(move.getFromColumn(),move.getFromRow());
       final Point movingPieceDestination=calculateXYFromGrid(move.getToColumn(),move.getToRow());
       int movingPieceId = mPosition.get(move.getFromColumn(),move.getFromRow());
       movingPieceBmp = pieceBox[movingPieceId];
       int squareSize = getSquareSize();
       Point[] pointsOnTheWay = PathFactory.generate(Linear,movingPiecePosition,movingPieceDestination);
       animationHandler.post(new Runnable() {
           int count = 0;
           @Override
           public void run() {
               moveActive = true;
               while(count<pointsOnTheWay.length) {
                   moveRect.offsetTo(pointsOnTheWay[count].x,pointsOnTheWay[count].y);
                   invalidateView();
                   count++;
               }
               dropMovingPiece(movingPieceDestination, movingPieceId);
               moveActive = false;
               surfer.moveIsDone();

           }
       });
    }

    boolean swellAnimate = false, okAnimate=false;
    float swellFactor=1f;

    public void okAnimate(Cord location, AnimationDoneListener animationDoneListener) {
        final float[] swellFactors = new float[]{0,45,90,180,245,360};

        animationHandler.post(new Runnable() {
            int count = 0;
            @Override
            public void run() {
                okAnimate=true;
                okLocation=location;
                while(count<swellFactors.length) {
                    swellFactor=swellFactors[count];
                    invalidateView();
                    count++;
                }
                okAnimate=false;
                animationDoneListener.onAnimationDone();
            }
        });
    }
    public void swellAnimate(AnimationDoneListener animationDoneListener) {
        final float[] swellFactors = new float[]{1.05f,1.06f,1.1f,1.1f,1.06f,1.05f,1};

        animationHandler.post(new Runnable() {
            int count = 0;
            @Override
            public void run() {
                swellAnimate=true;
                while(count<swellFactors.length) {
                    swellFactor=swellFactors[count];
                    invalidateView();
                    count++;
                }
                swellAnimate=false;
                animationDoneListener.onAnimationDone();
            }
        });
    }


    public void moveDragRect(Point point) {
        dragRect.offsetTo(point.x,point.y);
    }

    private void invalidateView() {
        surfer.surfaceChanged();
    }

    public Square[] getSquares() {
        return squares;
    }

    @Override
    public void draw(Canvas c) {
        c.drawColor(Color.BLACK);
        if (hasEdge) {
            c.drawBitmap(edgeHorisontal, null, T_Edge, p);
            c.drawBitmap(edgeHorisontal, null, B_Edge, p);
            c.drawBitmap(edgeVertical, null, L_Edge, p);
            c.drawBitmap(edgeVertical, null, R_Edge, p);
        }
        int i = 0;
        Paint worb;
        for (int row = 0; row < 8; row++) {
            //square = (square == w_square) ? b_square : w_square;
            for (int column = 0; column < 8; column++) {
                //square = (square == w_square) ? b_square : w_square;
                //c.drawBitmap(square, null, squares[i], p);
                worb = (column%2+row%2)%2==0 ? wh:sg;
                c.drawRect(squares[i].mRect,worb);

                int flip_row = whiteOnTop ? 7 - row : row;
                int flip_column = whiteOnTop ? 7 - column : column;
                //Don't draw The moving piece

                if (!mPosition.isEmpty(flip_column, flip_row))
                        c.drawBitmap(pieceBox[mPosition.get(flip_column, flip_row)], null, pieceSquares[i], p);
                i++;
            }
        }
    }


    public void onDraw(Canvas c) {
        c.save();
        c.translate(0,boardOffset);
        if (c != null) {
            c.drawColor(Color.BLACK);
            if (swellAnimate) {
                c.scale(swellFactor, swellFactor,centerX,centerY);
            }
            if (hasEdge) {
                c.drawBitmap(edgeHorisontal, null, T_Edge, p);
                c.drawBitmap(edgeHorisontal, null, B_Edge, p);
                c.drawBitmap(edgeVertical, null, L_Edge, p);
                c.drawBitmap(edgeVertical, null, R_Edge, p);
            }
            int i = 0;
            Bitmap square = w_square;
            Paint worb;
            for (int row = 0; row < 8; row++) {
                //square = (square == w_square) ? b_square : w_square;
                for (int column = 0; column < 8; column++) {
                    //square = (square == w_square) ? b_square : w_square;
                    //c.drawBitmap(square, null, squares[i], p);
                    worb = (column%2+row%2)%2==0 ? wh:sg;
                    c.drawRect(squares[i].mRect,worb);

                    int flip_row = whiteOnTop ? 7 - row : row;
                    int flip_column = whiteOnTop ? 7 - column : column;
                    //Don't draw The moving piece

                    if (!mPosition.isEmpty(flip_column, flip_row))
                        if (!movingPieceInitialPosition.equals(flip_column, flip_row))
                            c.drawBitmap(pieceBox[mPosition.get(flip_column, flip_row)], null, pieceSquares[i], p);

                    i++;
                }
            }
            if (dragActive) {
                c.drawBitmap(movingPieceBmp,null, dragRect,p);
            }
            if (moveActive) {
                c.drawBitmap(movingPieceBmp,null, moveRect,p);
            }
            if (okAnimate) {

                final Point okLocationXY=calculateXYFromGrid(okLocation.column,okLocation.row);
                c.rotate(swellFactor,okLocationXY.x+getSquareSize()/4,okLocationXY.y+getSquareSize()/4);
                okRect.offsetTo(okLocationXY.x,okLocationXY.y);
                c.drawBitmap(okAnimateBmp,null,okRect,p);
            }
            //if (swellAnimate || okAnimate )
            //    c.restore();
            c.restore();

        }

    }



}

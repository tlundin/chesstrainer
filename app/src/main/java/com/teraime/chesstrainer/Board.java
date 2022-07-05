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
import android.util.Log;

import java.util.Stack;


public class Board implements GameWidget
{
    final StyleOptions pieceStyle,boardStyle;
    final static float border_thickness_as_percentage = .10f;
    final Rect boardRect,gridRect,L_Edge,R_Edge,B_Edge,T_Edge;
    final Paint edgeColor = new Paint();
    final Paint bgColor = new Paint(Color.BLACK);
    final Paint neutralP = new Paint();
    final Paint blackP = new Paint();
    final Paint whiteP = new Paint();
    Paint p = new Paint();
    final Bitmap edgeHorisontal,edgeVertical,w_square,b_square;
    final Bitmap w_knight,w_bishop,w_king,w_rook,w_pawn,w_queen;
    final Bitmap b_knight,b_bishop,b_king,b_rook,b_pawn,b_queen;

    final Bitmap[] pieceBox;
    final Boolean hasEdge;
    final Square[] squares;
    final PieceRect[] pieceSquares;
    final int border_thickness,gridSize;
    private ChessPosition mPosition=null;
    private boolean whiteOnTop = false;
    private Cord movingPieceInitialPosition,Outside;
    private Bitmap draggedPieceBmp,okAnimateBmp;
    private Rect okRect;
    private int boardOffset;
    final float centerX,centerY;
    private Cord okLocation;
    private Rect dragStartRect;
    private int dragStartIndex;
    private final Bitmap chessBoardBmp;

    public Board(Context context, ScaleOptions scaleOption, StyleOptions boardStyle, StyleOptions pieceStyle, int size_with_borders, int bo) {
        int _size = scale(size_with_borders,scaleOption);
        this.boardOffset = bo;
        this.pieceStyle = pieceStyle;
        this.boardStyle = boardStyle;
        blackP.setColor(Color.parseColor("#798A80"));
        whiteP.setColor(Color.parseColor("#F8F4EC"));
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
        squares = new Square[64]; pieceSquares = new PieceRect[64];
        w_square = BitmapFactory.decodeResource(context.getResources(),R.drawable.white_square_wood);
        b_square = BitmapFactory.decodeResource(context.getResources(),R.drawable.dark_square_wood);
        int top = 0;int left=0;int bottom=0;int right=0;
        int i=0;
        int square_size = getSquareSize();

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
                pieceSquares[i++] = new PieceRect(left,top,right,bottom);
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
        chessBoardBmp = Bitmap.createBitmap(square_size*8,square_size*8, Bitmap.Config.ARGB_8888);

        Canvas c = new Canvas(chessBoardBmp);
        i = 0;
        for (int row = 0; row < 8; row++) {
            //square = (square == w_square) ? b_square : w_square;
            for (int column = 0; column < 8; column++) {
                //square = (square == w_square) ? b_square : w_square;
                //c.drawBitmap(square, null, squares[i], p);
                p = (column % 2 + row % 2) % 2 == 0 ? whiteP : blackP;
                c.drawRect(squares[i++].mRect, p);
            }
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

    public BasicMove dropMovingPiece(PieceRect movingSquare, Cord movingPieceInitialPosition, Point dropPoint, int piece) {
        int x = dropPoint.x;
        int y = dropPoint.y;
        Cord movingPieceEndPosition;
        BasicMove bMove = null;
        if (isInside(x,y)) {
            movingPieceEndPosition = calculateColRow(x, y);
            int oPiece = mPosition.get(movingPieceInitialPosition.getColumn(), movingPieceInitialPosition.getRow());
            if (oPiece == piece)
                mPosition.put(movingPieceInitialPosition.getColumn(), movingPieceInitialPosition.getRow(), ChessConstants.B_EMPTY);
            mPosition.put(movingPieceEndPosition.getColumn(), movingPieceEndPosition.getRow(), piece);
            if (!(movingPieceInitialPosition.equals(movingPieceEndPosition)))
                bMove = new BasicMove(movingPieceInitialPosition,movingPieceEndPosition);
        }
        movingSquare.moveDone();
        return bMove;
    }

    public PieceRect startDrag(int piece,int x,int y) {
        int column = (x-border_thickness)/getSquareSize();
        int row = (y-border_thickness)/getSquareSize();
        dragStartIndex = 8*row+column;
        return pieceSquares[dragStartIndex].move(piece);
        //dragRect = new Rect(0,0,(int)(square_size*GameView.DragRectSize),(int)(square_size*GameView.DragRectSize));
    }

    public GameAnimation move(Move move, PathFactory.Type type) {
        Cord movingPieceInitialPosition = move.getFromCord();
        final Point movingPiecePosition = calculateXYFromGrid(move.getFromColumn(), move.getFromRow());
        final Point movingPieceDestination = calculateXYFromGrid(move.getToColumn(), move.getToRow());
        final int movingPieceId = mPosition.get(move.getFromColumn(), move.getFromRow());
        //movingPieceBmp = pieceBox[movingPieceId];
        final MoveVector vect = PathFactory.generate(type, movingPiecePosition, movingPieceDestination);
        final Point[] pointsOnTheWay = vect.mPoints;
        final PieceRect movePieceRect = pieceSquares[8*move.getFromRow()+move.getFromColumn()].move(movingPieceId);
        final Rect moveRect = movePieceRect.r;
        final float width = moveRect.width();
        final float height = moveRect.height();

        return new GameAnimation() {
            int count = 0;
            boolean done = false;

            @Override
            public boolean stepAnimate() {
                moveRect.offsetTo(pointsOnTheWay[count].x, pointsOnTheWay[count].y);
                final int rCalc = (int)(width+width*vect.mScale[count]);
                final int bCalc = (int)(height+height*vect.mScale[count]);
                moveRect.set(moveRect.left,moveRect.top,moveRect.left+rCalc,moveRect.top+bCalc);
                count++;
                if (count == pointsOnTheWay.length) {
                    dropMovingPiece(movePieceRect,movingPieceInitialPosition,movingPieceDestination, movingPieceId);
                    done = true;
                }

                return done;
            }
        };

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



    public GameAnimation move(Move move) {
        return move(move, Linear);
    }


    boolean swellAnimate = false, okAnimate=false;
    float swellFactor=1f;
    float rotateDegrees;

    public GameAnimation okAnimate(Cord location) {
        final float[] rotateFactors = new float[]{0,45,90,180,245,360};
        okAnimate=true;
        okLocation=location;
        return new GameAnimation() {
            int count = 0;
            boolean done = false;
            @Override
            public boolean stepAnimate() {
                rotateDegrees = rotateFactors[count++];
                if (count == rotateFactors.length) {
                    okAnimate = false;
                    done = true;
                }
                return done;
            }
        };


    }

    public GameAnimation swell() {
        final float[] swellFactors = new float[]{1.05f,1.06f,1.1f,1.1f,1.06f,1.05f,1};
        return new GameAnimation() {
            int count = 0;
            boolean done = false;
            @Override
            public boolean stepAnimate() {
                swellAnimate=true;
                swellFactor=swellFactors[count];
                count++;
                done = count == swellFactors.length;
                if (done)
                    swellAnimate = false;
                return (done);
            }
        };
    }

    public void setFade(float fade) {
        int alphaOut = (int)fade*255;
        blackP.setAlpha(alphaOut);
        whiteP.setAlpha(alphaOut);
        neutralP.setAlpha(alphaOut);
    }


    public GameAnimation fadeBoard(final int alphaTarget, int duration) {

        return new GameAnimation() {
            boolean done = false;
            double alpha = neutralP.getAlpha()/255f;
            int loopsteps = duration/SceneLoop.FRAME_RATE;
            final double alphaDiff = (alphaTarget - alpha)/loopsteps;
            @Override
            public boolean stepAnimate() {
                if (--loopsteps == 0)
                    done = true;
                alpha+=alphaDiff;
                int alphaOut = (int)(alpha*255d);
                //Log.d("v","alphFiff "+alphaDiff+" loopst "+loopsteps+"aout"+alphaOut);
                blackP.setAlpha(alphaOut);
                whiteP.setAlpha(alphaOut);
                neutralP.setAlpha(alphaOut);
                return done;
            }
        };


    }


    public void moveDragRect(PieceRect dragRect,Point point) {
        dragRect.r.offsetTo(point.x,point.y);
    }

    public Square[] getSquares() {
        return squares;
    }

    Stack<PieceRect> moveStack = new Stack<>();

    public void draw(Canvas c) {
        c.drawColor(Color.BLACK);
        if (swellAnimate) {
            c.scale(swellFactor, swellFactor,centerX,centerY);
        }
        if (hasEdge) {
            c.drawBitmap(edgeHorisontal, null, T_Edge, neutralP);
            c.drawBitmap(edgeHorisontal, null, B_Edge, neutralP);
            c.drawBitmap(edgeVertical, null, L_Edge, neutralP);
            c.drawBitmap(edgeVertical, null, R_Edge, neutralP);
        }
        c.drawBitmap(chessBoardBmp,null,boardRect,neutralP);

        int i=0;
        for (int row = 0; row < 8; row++) {
            //square = (square == w_square) ? b_square : w_square;
            for (int column = 0; column < 8; column++) {
                int flip_row = whiteOnTop ? 7 - row : row;
                int flip_column = whiteOnTop ? 7 - column : column;
                //Don't draw The moving piece

                if (!mPosition.isEmpty(flip_column, flip_row)) {
                    if (!pieceSquares[i].isMoving)
                        c.drawBitmap(pieceBox[mPosition.get(flip_column, flip_row)], null, pieceSquares[i].r, neutralP);
                    else
                        moveStack.push(pieceSquares[i]);
                }
                i++;
            }
        }
        PieceRect pr;
        while(!moveStack.isEmpty()) {
            pr = moveStack.pop();
            c.drawBitmap(pieceBox[pr.mPiece], null, pr.r, neutralP);
        }
        if (okAnimate) {
            final Point okLocationXY=calculateXYFromGrid(okLocation.column,okLocation.row);
            c.rotate(rotateDegrees,okLocationXY.x+getSquareSize()/4,okLocationXY.y+getSquareSize()/4);
            okRect.offsetTo(okLocationXY.x,okLocationXY.y);
            c.drawBitmap(okAnimateBmp,null,okRect, neutralP);
        }

    }

    @Override
    public String getName() {
        return "board";
    }






}

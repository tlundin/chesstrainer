package com.teraime.chesstrainer;


import static com.teraime.chesstrainer.ChessConstants.INITIAL_BOARD;
import static com.teraime.chesstrainer.ChessConstants.TWO_PAWN_BOARD;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.View;

import androidx.annotation.NonNull;

import java.util.Random;

public class GameView implements SurfaceHolder.Callback, View.OnClickListener, View.OnTouchListener {


    private final Context context;
    private Board board;
    private SurfaceHolder mSurfaceHolder = null;

    public GameView(Context context) {

        this.context = context;

    }
    String TAG = "beboop";

    @Override
    public void surfaceCreated(@NonNull SurfaceHolder surfaceHolder) {
        Log.d(TAG,"Surf is good");
        mSurfaceHolder=surfaceHolder;
        board = new Board(context,Board.ScaleOptions.MAX, Board.StyleOptions.plain, Board.StyleOptions.fancy,surfaceHolder.getSurfaceFrame().width());
        ChessPosition pos = new ChessPosition(INITIAL_BOARD);//new ChessPosition(GameState.convertFenToBoard(ChessConstants.FEN_STARTING_POSITION));
        pos.print();
        board.setupPosition(pos);
        Canvas c = mSurfaceHolder.lockCanvas();
        board.onDraw(c);
        mSurfaceHolder.unlockCanvasAndPost(c);

    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder surfaceHolder, int i, int i1, int i2) {

    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder surfaceHolder) {

    }




    @Override
    public void onClick(View view) {
        Log.d(TAG,"pluck");
    }

    Bitmap draggedPieceBmp;
    boolean dragActive = false;
    int draggedPiece;
    Rect dragRect;
    Paint p = new Paint();

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        int x = (int)motionEvent.getX();
        int y = (int)motionEvent.getY();
        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
                Log.d(TAG,"x: "+x+"y:"+y);
                draggedPiece = board.dragIfPiece(x,y);
                if (draggedPiece > 0) {
                    dragActive = true;
                    draggedPieceBmp = board.getPieceBox()[draggedPiece];
                    int squareSize = board.getSquareSize();
                    dragRect = new Rect(0,0,squareSize,squareSize);
                }
                break;
            case MotionEvent.ACTION_UP:
                if (dragActive) {
                    dragActive = false;
                    Canvas c = mSurfaceHolder.lockCanvas();
                    board.dropDraggedPiece(x, y, draggedPiece);
                    board.onDraw(c);
                    mSurfaceHolder.unlockCanvasAndPost(c);
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (dragActive) {
                    Canvas c = mSurfaceHolder.lockCanvas();
                    int squareSize = board.getSquareSize();
                    dragRect.offsetTo(x-squareSize/2,y-squareSize/2);
                    board.onDraw(c);
                    c.drawBitmap(draggedPieceBmp,null,dragRect,p);
                    mSurfaceHolder.unlockCanvasAndPost(c);
                }
                break;
            default:
                break;
        }
        return false;
    }

    public void onFlipClick() {
        if (mSurfaceHolder != null) {
            board.flip();
            Canvas c = mSurfaceHolder.lockCanvas();
            board.onDraw(c);
            mSurfaceHolder.unlockCanvasAndPost(c);
        }
    }
}
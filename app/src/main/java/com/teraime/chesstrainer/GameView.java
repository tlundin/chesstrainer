package com.teraime.chesstrainer;


import static com.teraime.chesstrainer.ChessConstants.INITIAL_BOARD;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.View;

import androidx.annotation.NonNull;

import java.io.File;
import java.util.List;

public class GameView implements SurfaceHolder.Callback, View.OnClickListener, View.OnTouchListener,SurfaceHolderCallback {


    private final Context context;
    private Board board;
    private SurfaceHolder mSurfaceHolder = null;
    private boolean moveIsActive = false;
    private MySQLiteHelper db;
    public GameView(Context context, MySQLiteHelper db) {

        this.context = context;
        this.db = db;

    }
    String TAG = "beboop";

    @Override
    public void surfaceCreated(@NonNull SurfaceHolder surfaceHolder) {
        Log.d(TAG,"Surf is good");
        mSurfaceHolder=surfaceHolder;
        board = new Board(context,Board.ScaleOptions.MAX, Board.StyleOptions.oak, Board.StyleOptions.fancy,surfaceHolder.getSurfaceFrame().width(),this);
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
    public void surfaceChanged() {
        Canvas c = mSurfaceHolder.lockCanvas();
        board.onDraw(c);
        mSurfaceHolder.unlockCanvasAndPost(c);
    }

    @Override
    public void moveIsActive() {
        moveIsActive = true;
    }
    @Override
    public void moveIsDone() {
        moveIsActive = false;
    }

    @Override
    public void onClick(View view) {
        Log.d(TAG,"pluck");
    }

    boolean dragActive = false;
    int draggedPiece;

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        int x = (int)motionEvent.getX();
        int y = (int)motionEvent.getY();
        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (!moveIsActive && !dragActive) {
                    Log.d(TAG, "x: " + x + "y:" + y);
                    draggedPiece = board.dragIfPiece(x, y);
                    if (draggedPiece > 0) {
                        dragActive = true;
                        Bitmap draggedPieceBmp = board.getPieceBox()[draggedPiece];
                        int squareSize = board.getSquareSize();
                        Rect dragRect = new Rect(0, 0, squareSize, squareSize);
                        board.initialiseMoveRect(dragRect, draggedPieceBmp);
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                if (dragActive) {
                    dragActive = false;
                    board.dropMovingPiece(new Point(x, y), draggedPiece);
                    moveIsDone();
                    surfaceChanged();
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (dragActive) {
                    int squareSize = board.getSquareSize();
                    board.movePieceTo(new Point(x-squareSize/2,y-squareSize/2));
                    surfaceChanged();
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

    public void onTestClick() {
        //board.move(new BasicMove(new Cord(4,1),new Cord(4,2)));
        //File dbP = context.getDatabasePath("chess.db");
        //Log.d("db","FILE "+dbP.getAbsolutePath());
        db.openDataBase();
        int noOfProblems = 5;
        int minProbLvl = 1500;
        int maxProbLvl = 2000;
        Types.TacticProblem problem = db.getTacticProblem(minProbLvl,maxProbLvl);
        int[][] pos = Tools.translateBoard(problem.board);
        MoveList ml = new MoveList();
        ml.add(new GameState(pos,problem.whiteToMove));
        Tools.addMoves(problem.moves,ml);
        ml.resetMovePointer();
        GameState gs = ml.getCurrentPosition();
        board.setupPosition(gs.getPosition());
        if (!gs.whiteToMove)
            board.flip();

        if(ml.goForward()) {
            Move moveThatLeadHere = ml.getCurrentPosition().getMove();
            GameState newState = ml.getCurrentPosition();
            board.move(moveThatLeadHere, new MoveCallBack_I() {
                @Override
                public void onMoveDone() {
                    Log.d("schack", "move done. Now waiting for player input");
                    int result = newState.checkForEndConditions();
                    if (result != GameResult.NORMAL) {
                        if (result == GameResult.MATE) {
                            Log.d("schack", "Computer won!");
                        } else {
                            Log.d("schack", "Stale mate!");
                        }

                    } else if (newState.checkIfDraw(!newState.whiteToMove)) {
                        Log.d("schack", "NO WAY TO WIN");
                    } else
                        MoveCallBack_I playerMovedCb = new MoveCallBack_I() {
                            public void onMoveDone() {

                            }
                        }
                }
            });
        }
    }

    public void onResetClick() {
        if (mSurfaceHolder != null) {
            board.setupPosition(new ChessPosition(INITIAL_BOARD));
            Canvas c = mSurfaceHolder.lockCanvas();
            board.onDraw(c);
            mSurfaceHolder.unlockCanvasAndPost(c);
        }

    }
}
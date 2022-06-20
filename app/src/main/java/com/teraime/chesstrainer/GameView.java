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
import android.widget.TextView;

import androidx.annotation.NonNull;

public class GameView implements SurfaceHolder.Callback, View.OnClickListener, View.OnTouchListener,SurfaceHolderCallback {


    private final Context context;
    private Board board;
    private Progressor progressor;
    private SurfaceHolder mSurfaceHolder = null;
    private boolean moveIsActive = false;
    private MySQLiteHelper db;
    private ChessPosition boardAfterMove;
    private int boardOffset,progressorOffset,progressorHeight;
    TextView scoreT;
    private int score = 0;

    public GameView(Context context, MySQLiteHelper db, TextView scoreT) {

        this.context = context;
        this.db = db;
        this.scoreT = scoreT;

    }
    String TAG = "beboop";

    @Override
    public void surfaceCreated(@NonNull SurfaceHolder surfaceHolder) {
        Log.d(TAG,"Surf is good");
        mSurfaceHolder=surfaceHolder;
        int canvasW = surfaceHolder.getSurfaceFrame().width();
        int canvasH = surfaceHolder.getSurfaceFrame().height();
        boardOffset = (int)(Math.abs(canvasH-canvasW));
        //Board is screenw wide. Let progressor be about two squares wide.
        progressorHeight = canvasW/4;
        progressorOffset = boardOffset-progressorHeight;
        board = new Board(context,Board.ScaleOptions.MAX, Board.StyleOptions.oak, Board.StyleOptions.fancy,canvasW, boardOffset, this);
        ChessPosition pos = new ChessPosition(INITIAL_BOARD);//new ChessPosition(GameState.convertFenToBoard(ChessConstants.FEN_STARTING_POSITION));
        pos.print();
        board.setupPosition(pos);
        progressor = new Progressor(progressorOffset,canvasW,progressorHeight,this);
        surfaceChanged();

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
        progressor.onDraw(c);
        mSurfaceHolder.unlockCanvasAndPost(c);
    }

    public void nextLevel() {
        try {
            scoreT.setText("" + score++);
            Thread.sleep(500);
            progressor.scrollAnimate(1, 1);
            onTestClick();
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void moveIsActive() {
        moveIsActive = true;
    }
    @Override
    public void moveIsDone() {
        board.setupPosition(boardAfterMove);
        boardAfterMove.print();
        surfaceChanged();
        moveIsActive = false;
        if (moveListener != null)
            moveListener.onMoveDone();
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
                        Bitmap draggedPieceBmp = board.getPieceBox()[draggedPiece];
                        int squareSize = board.getSquareSize();
                        Rect dragRect = new Rect(0, 0, squareSize, squareSize);
                        board.initialiseMoveRect(dragRect, draggedPieceBmp);
                        dragActive = true;
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                if (dragActive) {
                    dragActive = false;
                    BasicMove bMove= board.dropMovingPiece(new Point(x, y- boardOffset), draggedPiece);
                    surfaceChanged();
                    if (dragListener != null) {
                        dragListener.onDragDone(bMove);
                    }
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (dragActive) {
                    int squareSize = board.getSquareSize();
                    board.movePieceTo(new Point(x-squareSize/2,y-squareSize/2- boardOffset));
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

    float lastmin=0;
    public void onTestClick() {
        //board.move(new BasicMove(new Cord(4,1),new Cord(4,2)));
        //File dbP = context.getDatabasePath("chess.db");
        //Log.d("db","FILE "+dbP.getAbsolutePath());
        //progressor.scrollAnimate(1,1);
        if (!moveIsActive) {
            db.openDataBase();
            int noOfProblems = 5;
            int minProbLvl = 1500;
            int maxProbLvl = 2000;
            Types.TacticProblem problem = db.getTacticProblem(lastmin);
            lastmin = problem.rating;
            Log.d("v","lastmin now "+lastmin);
            int[][] pos = Tools.translateBoard(problem.board);
            MoveList ml = new MoveList();
            ml.add(new GameState(pos, problem.whiteToMove));
            Tools.addMoves(problem.moves, ml);
            ml.resetMovePointer();
            GameState gs = ml.getCurrentPosition();
            board.setupPosition(gs.getPosition());
            if (gs.whiteToMove)
                board.setWhiteOnTop();
            else
                board.setBlackOnTop();

            if (ml.goForward()) {
                Move moveThatLeadHere = ml.getCurrentPosition().getMove();
                Log.d("v", "Move to do: " + moveThatLeadHere.getShortNoFancy() + " raw: " + moveThatLeadHere.getRawNotation() + " ts: " + moveThatLeadHere.toString());
                this.registerDragAndMoveListener(new TacticsHandler(ml, board,this));
                board.move(moveThatLeadHere);
                boardAfterMove = ml.getCurrentPosition().getPosition();
            }
        }
    }

    MoveCallBack_I dragListener,moveListener;
    private void registerDragAndMoveListener(MoveCallBack_I listener) {
        this.dragListener=listener;
        this.moveListener=listener;
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
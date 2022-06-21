package com.teraime.chesstrainer;


import static com.teraime.chesstrainer.ChessConstants.INITIAL_BOARD;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Point;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.List;
import java.util.Random;

public class GameView implements SurfaceHolder.Callback, View.OnClickListener, View.OnTouchListener,SurfaceHolderCallback {


    public static float DragRectSize = 1.5f;
    private final Context context;
    private final Animation shake;
    private final Handler shakeHandler;
    private final Runnable repeatShake;
    private Board board;
    private Progressor progressor;
    private SurfaceHolder mSurfaceHolder = null;
    private boolean moveIsActive = false;
    private MySQLiteHelper db;
    private ChessPosition boardAfterMove;
    private int boardOffset,progressorOffset,progressorHeight;
    TextView scoreT;
    ImageButton retryB;
    private int score = 0;

    public GameView(Context context, MySQLiteHelper db, TextView scoreT, ImageButton retry) {

        this.context = context;
        this.db = db;
        this.scoreT = scoreT;
        retryB=retry;
        this.shake = AnimationUtils.loadAnimation(context, R.anim.shake);
        shakeHandler = new Handler();
        final boolean[] first = {true};
        repeatShake = new Runnable() {
            @Override
            public void run() {
                try {
                    if (!first[0])
                        retryB.startAnimation(shake);
                    else
                        first[0] = false;
                } finally {
                    // 100% guarantee that this always happens, even if
                    // your update method throws an exception
                    shakeHandler.postDelayed(repeatShake, 2500);
                }
            }
        };

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
        progressorHeight = canvasW/3;
        progressorOffset = boardOffset-progressorHeight;
        board = new Board(context,Board.ScaleOptions.MAX, Board.StyleOptions.plain, Board.StyleOptions.plain,canvasW, boardOffset, this);
        ChessPosition pos = new ChessPosition(INITIAL_BOARD);//new ChessPosition(GameState.convertFenToBoard(ChessConstants.FEN_STARTING_POSITION));
        pos.print();
        board.setupPosition(pos);
        progressor = new Progressor(context,progressorOffset,canvasW,progressorHeight,this);
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

    public void onFail() {
        retryB.setVisibility(View.VISIBLE);
        repeatShake.run();
        retryB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shakeHandler.removeCallbacks(repeatShake);
                retryB.setVisibility(View.GONE);
                onTestClickFail();
            }
        });


    }

    int[] moveSpeedA = new int[]{1,2,4,8,10,16,20,25};
    int moveSpeedIndex = 0;
    int currentMoveSpeed = 1;

    public void nextLevel() {
        try {
            scoreT.setText("" + score++);
            Thread.sleep(500);
            currentMoveSpeed = moveSpeedA[moveSpeedIndex];
            progressor.scrollAnimate(currentMoveSpeed, new AnimationDoneListener() {
                @Override
                public void onAnimationDone() {

                    onTestClickSuccess();
                }
            });
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
    GameState gameState = null;

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        int x = (int)motionEvent.getX();
        int y = (int)motionEvent.getY()-boardOffset;
        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (!moveIsActive && !dragActive) {
                    Log.d(TAG, "x: " + x + "y:" + y);
                    draggedPiece = board.getPiece(x, y);
                    if (draggedPiece > 0) {
                        if (gameState != null) {
                            boolean isW = ChessConstants.isWhite(draggedPiece);
                            if ((gameState.whiteToMove && isW) || (!gameState.whiteToMove && !isW))
                                dragActive = true;
                        } else
                            dragActive = true;
                        if (dragActive) {
                            board.startDrag(board.calculateColRow(x, y), draggedPiece);
                        }
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                if (dragActive) {
                    dragActive = false;
                    BasicMove bMove= board.dropMovingPiece(new Point(x, y), draggedPiece);
                    surfaceChanged();
                    if (dragListener != null && bMove != null) {
                        dragListener.onDragDone(bMove);
                    }
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (dragActive) {
                    int dragPoint = (int)(board.getSquareSize()*DragRectSize/2);
                    board.moveDragRect(new Point(x-dragPoint,y-dragPoint));
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

    float lastmin=0,currentMin=0;


    private void onTestClickSuccess() {
        moveSpeedIndex = Math.min(moveSpeedIndex+1,moveSpeedA.length-1);
        onTactic();
    }
    private void onTestClickFail() {
        moveSpeedIndex = Math.max(moveSpeedIndex-1,0);
        lastmin=currentMin;
        onTactic();
    }

    public Progressor.Difficulty getDifficulty(int level) {
        if (mTacticProblems == null)
            return Progressor.Difficulty.normal;
        else
            return mTacticProblems.get(level).difficulty;

    }

    List<Types.TacticProblem> mTacticProblems;
    public void onTestClick() {
        db.openDataBase();
        mTacticProblems = db.getTacticProblems(500,800);
        db.close();
        onTactic();
    }

    public void onTactic() {
        //board.move(new BasicMove(new Cord(4,1),new Cord(4,2)));
        //File dbP = context.getDatabasePath("chess.db");
        //Log.d("db","FILE "+dbP.getAbsolutePath());
        Types.TacticProblem problem = mTacticProblems.get(progressor.currentLevel+currentMoveSpeed);
        if (!moveIsActive) {
            currentMin=lastmin;
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
            this.surfaceChanged();
            if (ml.goForward()) {
                Move moveThatLeadHere = ml.getCurrentPosition().getMove();
                Log.d("v", "Move to do: " + moveThatLeadHere.getShortNoFancy() + " raw: " + moveThatLeadHere.getRawNotation() + " ts: " + moveThatLeadHere.toString());
                this.registerDragAndMoveListener(new TacticsHandler(ml, board,this));
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
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

    public void setCurrentGameState(GameState newState) {
        gameState = newState;
    }
}
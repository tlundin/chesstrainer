package com.teraime.chesstrainer;


import static com.teraime.chesstrainer.ChessConstants.INITIAL_BOARD;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Point;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class GameView implements SceneContext, View.OnClickListener, View.OnTouchListener {


    public static float DragRectSize = 1.5f;
    private final Context context;
    private final Animation shake;
    private final Handler shakeHandler;
    private final Runnable repeatShake;
    private final DrawableGameWidget boardDrawableWidget;
    private Board board;
    private StageWidget stageWidget;
    private ScoreKeeper progressor;
    private boolean moveIsActive = false;
    private MySQLiteHelper db;
    private ChessPosition boardAfterMove;
    private int boardOffset, progressorOffsetY,progressorHeight;
    TextView scoreT;
    ImageButton endButton;
    Bitmap happy,sad;
    private int score = 0;
    String TAG = "GameView";
    private final Queue<DrawableGameWidget> mWidgets;

    public GameView(Context context, MySQLiteHelper db, TextView scoreT, ImageButton endB)  {

        this.context = context;
        this.db = db;
        this.scoreT = scoreT;
        this.endButton = endB;
        this.shake = AnimationUtils.loadAnimation(context, R.anim.shake);
        shakeHandler = new Handler(Looper.getMainLooper());
        final boolean[] first = {true};
        repeatShake = new Runnable() {
            @Override
            public void run() {
                try {
                    if (!first[0])
                        endB.startAnimation(shake);
                    else
                        first[0] = false;
                } finally {
                    // 100% guarantee that this always happens, even if
                    // your update method throws an exception
                    shakeHandler.postDelayed(repeatShake, 2500);
                }
            }
        };
        happy = BitmapFactory.decodeResource(context.getResources(), R.drawable.happy);
        sad = BitmapFactory.decodeResource(context.getResources(),R.drawable.sad);
        int canvasW = GameContext.gc.width;
        int canvasH = GameContext.gc.height;
        Log.d("zurf","CANVAS "+canvasW+" "+canvasH);
        boardOffset = canvasH/3;
        //Board is screenw wide. Let progressor be about two squares wide.
        progressorHeight = canvasW/3;
        progressorOffsetY = boardOffset-progressorHeight-canvasW/12;
        board = new Board(context,Board.ScaleOptions.MAX, Board.StyleOptions.plain, Board.StyleOptions.plain,canvasW, boardOffset);

        ChessPosition pos = new ChessPosition(INITIAL_BOARD);//new ChessPosition(GameState.convertFenToBoard(ChessConstants.FEN_STARTING_POSITION));
        pos.print();
        board.setupPosition(pos);
        User user = Tools.getUser();
        progressor = new ScoreKeeper(context, progressorOffsetY,canvasW,progressorHeight);
        stageWidget = new StageWidget(context,boardOffset,canvasW,canvasW,board.getSquareSize(),user.stage);
        mWidgets = new ConcurrentLinkedQueue<>();
        boardDrawableWidget = new DrawableGameWidget(board,boardOffset);
        mWidgets.add(boardDrawableWidget);
        mWidgets.add(new DrawableGameWidget(progressor,progressorOffsetY));

    }

    public Queue<DrawableGameWidget> getWidgets() {
        return mWidgets;
    }

    public DrawableGameWidget getBoardWidget() {
        return boardDrawableWidget;
    }

    public void addStageWidget() {
        mWidgets.add(new DrawableGameWidget(stageWidget,boardOffset));
    }

    public void onFail() {
        currFill = (currFill-10);
        if (currFill < 0) {
            endButton.setImageBitmap(sad);
            endButton.setVisibility(View.VISIBLE);
            repeatShake.run();
            endButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    shakeHandler.removeCallbacks(repeatShake);
                    endButton.setVisibility(View.GONE);
                    currFill = 0;
                    onTestClickFail();
                }
            });
        } else {
            progressor.setFillValue(currFill);
            onTestClickFail();
        }
    }

    int[] moveSpeedA = new int[]{1,2,4,8,10,16,20,25};
    int moveSpeedIndex = 0;
    int currentMoveSpeed = 1;
    int currFill = 0;

    public void nextLevel() {
        try {
            scoreT.setText("" + score++);
            Thread.sleep(500);
            currentMoveSpeed = moveSpeedA[moveSpeedIndex];
            /*progressor.scrollAnimate(currentMoveSpeed, new AnimationDoneListener() {
                @Override
                public void onAnimationDone() {

                    onTestClickSuccess();
                }
            });

             */
            currFill = (currFill+10);
            progressor.setFillValue(currFill);
            if (currFill == 100) {

                endButton.setImageBitmap(happy);
                endButton.setVisibility(View.VISIBLE);
                repeatShake.run();
                endButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        shakeHandler.removeCallbacks(repeatShake);
                        endButton.setVisibility(View.GONE);
                        currFill = 0;
                        onTestClickSuccess();
                    }
                });
            }
            onTestClickSuccess();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void moveIsActive() {
        moveIsActive = true;
    }

    public void moveIsDone() {
        board.setupPosition(boardAfterMove);
        boardAfterMove.print();
        moveIsActive = false;
        if (moveListener != null)
            moveListener.onMoveDone();
    }

    public Board getBoard() {
        return board;
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
        Cord movingPieceInitialPosition = null;
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
                            movingPieceInitialPosition = board.calculateColRow(x, y);
                            board.startDrag(movingPieceInitialPosition, draggedPiece);
                        }
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                if (dragActive) {
                    dragActive = false;
                    BasicMove bMove= board.dropMovingPiece(movingPieceInitialPosition,new Point(x, y), draggedPiece);
                    if (dragListener != null && bMove != null) {
                        dragListener.onDragDone(bMove);
                    }
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (dragActive) {
                    int dragPoint = (int)(board.getSquareSize()*DragRectSize/2);
                    board.moveDragRect(new Point(x-dragPoint,y-dragPoint));
                }
                break;
            default:
                break;
        }
        return false;
    }

    public void onFlipClick() {
        board.flip();
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
        Types.TacticProblem problem = mTacticProblems.get(currentMoveSpeed);
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

            if (ml.goForward()) {
                Move moveThatLeadHere = ml.getCurrentPosition().getMove();
                Log.d("v", "Move to do: " + moveThatLeadHere.getShortNoFancy() + " raw: " + moveThatLeadHere.getRawNotation() + " ts: " + moveThatLeadHere.toString());
                this.registerDragAndMoveListener(new TacticsHandler(ml,this));
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
            board.setupPosition(new ChessPosition(INITIAL_BOARD));
    }

    public void setCurrentGameState(GameState newState) {
        gameState = newState;
    }
}
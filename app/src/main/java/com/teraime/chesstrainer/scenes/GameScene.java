package com.teraime.chesstrainer.scenes;


import static com.teraime.chesstrainer.ChessConstants.INITIAL_BOARD;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.TextView;

import com.teraime.chesstrainer.AnimationDoneListener;
import com.teraime.chesstrainer.BasicMove;
import com.teraime.chesstrainer.BoardWidget;
import com.teraime.chesstrainer.ChessConstants;
import com.teraime.chesstrainer.ChessPosition;
import com.teraime.chesstrainer.Cord;
import com.teraime.chesstrainer.DrawableGameWidget;
import com.teraime.chesstrainer.GameContext;
import com.teraime.chesstrainer.GameState;
import com.teraime.chesstrainer.Move;
import com.teraime.chesstrainer.MoveCallBack_I;
import com.teraime.chesstrainer.MoveList;
import com.teraime.chesstrainer.MySQLiteHelper;
import com.teraime.chesstrainer.PieceRect;
import com.teraime.chesstrainer.R;
import com.teraime.chesstrainer.SceneContext;
import com.teraime.chesstrainer.StageDescriptor;
import com.teraime.chesstrainer.TacticsHandler;
import com.teraime.chesstrainer.Tools;
import com.teraime.chesstrainer.Types;
import com.teraime.chesstrainer.User;
import com.teraime.chesstrainer.widgets.ScoreKeeper;
import com.teraime.chesstrainer.widgets.StageButtonWidget;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class GameScene implements SceneContext, View.OnTouchListener {


    public static float DragRectSize = 1.5f;
    private final Context context;
    private final Animation shake;
    private final Handler shakeHandler;
    private final Runnable repeatShake;
    private final DrawableGameWidget boardDrawableWidget;
    private final DrawableGameWidget stageWidgetD;
    private BoardWidget board;
    private StageButtonWidget stageButtonWidget;
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
    private StageDescriptor mStage;

    public GameScene(Context context, MySQLiteHelper db, TextView scoreT, ImageButton endB)  {

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
        board = new BoardWidget(context, BoardWidget.ScaleOptions.MAX, BoardWidget.StyleOptions.plain, BoardWidget.StyleOptions.plain,canvasW, boardOffset);

        ChessPosition pos = new ChessPosition(INITIAL_BOARD);//new ChessPosition(GameState.convertFenToBoard(ChessConstants.FEN_STARTING_POSITION));
        pos.print();
        board.setupPosition(pos);
        User user = Tools.getUser();
        progressor = new ScoreKeeper(context, progressorOffsetY,canvasW,progressorHeight);
        stageButtonWidget = new StageButtonWidget(context,boardOffset,canvasW,canvasW,board.getSquareSize(),user.stage);
        mWidgets = new ConcurrentLinkedQueue<>();
        boardDrawableWidget = new DrawableGameWidget(board,boardOffset);
        mWidgets.add(boardDrawableWidget);
        mWidgets.add(new DrawableGameWidget(progressor,progressorOffsetY));
        stageWidgetD = new DrawableGameWidget(stageButtonWidget,boardOffset);
    }

    public Queue<DrawableGameWidget> getWidgets() {
        return mWidgets;
    }

    public DrawableGameWidget getBoardWidget() {
        return boardDrawableWidget;
    }

    public void addStageWidget() {
        mWidgets.add(stageWidgetD);
        GameContext.gc.registerClickListener(stageButtonWidget);
    }
    public void removeStageWidget() {
        mWidgets.remove(stageWidgetD);
        GameContext.gc.unregisterClickListener(stageButtonWidget);
    }

    int currFill = 0;

    public void onFail() {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    endButton.setImageBitmap(sad);
                    endButton.setVisibility(View.VISIBLE);
                    repeatShake.run();
                    endButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            shakeHandler.removeCallbacks(repeatShake);
                            endButton.setVisibility(View.GONE);
                            onTestClickFail();
                        }
                    });
                }
            });
    }


    public void nextLevel() {
        try {
            scoreT.setText("" + score++);
            Thread.sleep(500);
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
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {

                        endButton.setImageBitmap(happy);
                        endButton.setVisibility(View.VISIBLE);
                        repeatShake.run();
                        endButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                shakeHandler.removeCallbacks(repeatShake);
                                endButton.setVisibility(View.GONE);
                                currFill = 0;
                                progressor.setFillValue(currFill);
                                onTestClickSuccess();
                            }
                        });
                    }
                });
            } else
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
            moveListener.forEach(onMoveDone());
    }

    public BoardWidget getBoard() {
        return board;
    }


    boolean dragActive = false;
    int draggedPiece;
    GameState gameState = null;
    Cord movingPieceInitialPosition = null;
    PieceRect movePieceRect;
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
                            movingPieceInitialPosition = board.calculateColRow(x, y);
                            movePieceRect = board.startDrag(draggedPiece,x,y);
                        }
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                if (dragActive) {
                    dragActive = false;
                    BasicMove bMove= board.dropMovingPiece(movePieceRect,movingPieceInitialPosition,new Point(x, y), draggedPiece);
                    if (dragListener != null && bMove != null) {
                        dragListener.onDragDone(bMove);
                    }
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (dragActive) {
                    int dragPoint = (int)(board.getSquareSize()*DragRectSize/2);
                    board.moveDragRect(movePieceRect,new Point(x-dragPoint,y-dragPoint));
                }
                break;
            default:
                break;
        }
        return true;
    }

    public void onFlipClick() {
        board.flip();
    }

    float lastmin=0,currentMin=0;


    private void onTestClickSuccess() {
        GameContext.gc.user.level++;
        onTactic();
    }
    private void onTestClickFail() {
        lastmin=currentMin;
        onTactic();
    }


    List<Types.TacticProblem> mTacticProblems;
    public void onTestClick() {

    }

    public void startStage(StageDescriptor sd) {
        //board.setupPosition(new ChessPosition(INITIAL_BOARD));
        Log.d("v","In startStage");
        GameContext.gc.registerClickListener(this);
        mStage = sd;
        onTactic();
    }

    public void onTactic() {
        //board.move(new BasicMove(new Cord(4,1),new Cord(4,2)));
        //File dbP = context.getDatabasePath("chess.db");
        //Log.d("db","FILE "+dbP.getAbsolutePath());
        Types.TacticProblem problem =(Types.TacticProblem)mStage.getLevelMap().get(GameContext.gc.user.level);
        if (!moveIsActive && problem != null) {
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
                boardDrawableWidget.addAnimation(board.move(moveThatLeadHere), new AnimationDoneListener() {
                    @Override
                    public void onAnimationDone() {
                        moveIsDone();
                    }
                });
                boardAfterMove = ml.getCurrentPosition().getPosition();
            }
        }
    }

    MoveCallBack_I dragListener;
    List<MoveCallBack_I> moveListener = new ArrayList<>();
    private void registerDragAndMoveListener(MoveCallBack_I listener) {
        this.dragListener=listener;
        this.moveListener.add(listener);
    }

    public void onResetClick() {
            board.setupPosition(new ChessPosition(INITIAL_BOARD));
    }

    public void setCurrentGameState(GameState newState) {
        gameState = newState;
    }

    
}
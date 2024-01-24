package com.teraime.chesstrainer;

import android.util.Log;

import com.teraime.chesstrainer.scenes.GameScene;

public class TacticsHandler implements MoveCallBack_I {

    private MoveList moveList;
    private DrawableGameWidget wBoard;
    private BoardWidget board;
    private GameScene gv;

    public TacticsHandler(MoveList ml, GameScene gv) {
        this.moveList = ml;
        this.board = gv.getBoard();
        this.gv = gv;
        wBoard = gv.getBoardWidget();
    }

    @Override
    public void onMoveDone() {
        Log.d("schack", "move done. Now waiting for player input");
        GameState newState = moveList.getCurrentPosition();
        gv.setCurrentGameState(newState);
        int result = newState.checkForEndConditions();
        if (result != GameResult.NORMAL) {
            if (result == GameResult.MATE) {
                Log.d("schack", "Computer won!");
            } else {
                Log.d("schack", "Stale mate!");
            }

        } else if (newState.checkIfDraw(!newState.whiteToMove)) {
            Log.d("schack", "NO WAY TO WIN");
        }

    }

    @Override
    public void onDragDone(BasicMove bMove) {
        if(bMove != null) {
            Log.d("schack", "player moved " + bMove.toString());

            if (moveList.goForward()) {
                GameState newState = moveList.getCurrentPosition();
                Move moveThatLeadHere = newState.getMove();
                Log.d("schack", "correct move " + moveThatLeadHere.getShortNoFancy());
                if (moveThatLeadHere.equals(bMove)) {
                    Log.d("v", "CORRECT!");
                    if (moveList.goForward()) {
                        Log.d("v", "another move!");
                        wBoard.addAnimation(board.move(moveList.getCurrentPosition().getMove()), new AnimationDoneListener() {
                            @Override
                            public void onAnimationDone() {
                                gv.moveIsDone();
                            }
                        });

                    } else {
                        wBoard.addAnimation(board.okAnimate(bMove.to), new AnimationDoneListener() {
                            @Override
                            public void onAnimationDone() {
                                gv.nextLevel();
                            }
                        });
                    }
                } else {
                    wBoard.addAnimation(board.swell(),new AnimationDoneListener() {
                        @Override
                        public void onAnimationDone() {
                            gv.onFail();
                        }
                    });
                    //Log.d("v", "WRONG MOVE - Correct was " + moveThatLeadHere.getShortNoFancy());
                }


            }

        }
    }
}
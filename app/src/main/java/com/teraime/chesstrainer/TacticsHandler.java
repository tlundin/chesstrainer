package com.teraime.chesstrainer;

import android.util.Log;

public class TacticsHandler implements MoveCallBack_I {

    private MoveList moveList;
    private Board board;
    private GameView gv;

    public TacticsHandler(MoveList ml, Board board,GameView gv) {
        this.moveList = ml;
        this.board = board;
        this.gv = gv;
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
                        board.move(moveList.getCurrentPosition().getMove());
                    } else {
                        board.okAnimate(bMove.to, new AnimationDoneListener() {
                            @Override
                            public void onAnimationDone() {
                                gv.nextLevel();
                            }
                        });
                    }
                } else {
                    board.swellAnimate(new AnimationDoneListener() {
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
package com.teraime.chesstrainer;

import android.graphics.Rect;

public class PieceRect  {

        private Rect r;
        Rect tmpRect;
        private int mPiece;
        private boolean isMoving=false;

        public PieceRect(int left,int top,int right,int bottom) {
            r = new Rect(left,top,right,bottom);
        }

        public int width() {
            return r.width();
        }
        public int height() {
            return r.height();
        }

    public PieceRect move(int piece) {
            isMoving = true;
            tmpRect = new Rect(r);
            mPiece = piece;
            return this;
    }
    public void moveDone() {
            r = tmpRect;
            isMoving = false;
    }

    public Rect getRect() {
            return r;
    }

    public int getPiece() {
            return mPiece;
    }

    public boolean isMoving() {
            return isMoving;
    }
}

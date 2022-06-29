package com.teraime.chesstrainer;

public interface GameAnimation {

    enum AnimationType {
        XY,
        shrink,
        grow
    }

    public boolean stepAnimate();

}

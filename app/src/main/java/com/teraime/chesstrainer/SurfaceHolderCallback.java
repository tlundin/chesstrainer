package com.teraime.chesstrainer;

public interface SurfaceHolderCallback {
    public void surfaceChanged();

    void moveIsActive();
    void moveIsDone();
    Progressor.Difficulty getDifficulty(int level);
}

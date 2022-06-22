package com.teraime.chesstrainer;

public class LevelDescriptor {

    int level;
    boolean isGoal;

    LevelDescriptor(int level, boolean isGoal) {
        this.isGoal = isGoal;
        this.level = level;
    }
}

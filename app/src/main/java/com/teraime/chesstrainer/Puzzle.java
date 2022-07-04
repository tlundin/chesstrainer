package com.teraime.chesstrainer;

public class Puzzle {
    public static enum PuzzleType {
        TACTICS,GAME,MATE
    }

    protected PuzzleType puzzleType = PuzzleType.TACTICS;
}

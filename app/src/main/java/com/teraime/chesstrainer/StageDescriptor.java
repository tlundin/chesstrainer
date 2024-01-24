package com.teraime.chesstrainer;

import java.util.Map;

public class StageDescriptor {

    private final int stage;
    private final String stageText;
    private final Map<Integer,Puzzle> levelMap;

    public StageDescriptor(int stage, String stageText,Map<Integer,Puzzle> levelMap) {
        this.stage = stage;
        this.stageText = stageText;
        this.levelMap = levelMap;

    }

    public int getStage() {
        return stage;
    }

    public Map<Integer, Puzzle> getLevelMap() {
        return levelMap;
    }
}

package com.teraime.chesstrainer;

import java.util.Map;

public class StageDescriptor {

    final int stage;
    final String stageText;
    final Map<Integer,Puzzle> levelMap;

    public StageDescriptor(int stage, String stageText,Map<Integer,Puzzle> levelMap) {
        this.stage = stage;
        this.stageText = stageText;
        this.levelMap = levelMap;

    }
}

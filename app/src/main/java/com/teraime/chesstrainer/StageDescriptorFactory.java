package com.teraime.chesstrainer;

public class StageDescriptorFactory {

    public static StageDescriptor getStageDescriptor(int stage,int rating) {

        return new StageDescriptor(stage,"Test");
    }
/*
    MoveList ml = null;
			do {
        MatePuzzle mp = ResourceManager.getInstance().db.getMatePuzzle(moves);
        ml = GameAnalyzer.createGameFromFen(mp.fen, mp.moves);
    } while (ml==null);

 */
}

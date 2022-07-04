package com.teraime.chesstrainer;

import java.util.HashMap;
import java.util.Map;

public class StageDescriptorFactory {

    public static StageDescriptor getStageDescriptor(int stage) {
        GameContext gc = GameContext.gc;
        Map<Integer,Puzzle> levelMap = new HashMap<>();
        int uRating = gc.user.rating;
        switch (stage) {
            default:
                for (int i=0; i< 15;i++) {
                    if (i%2==0)
                        levelMap.put(i,gc.getTactic(uRating));
                    else
                        levelMap.put(i,gc.getMatePuzzle(2));
                }

        }
        return new StageDescriptor(stage,"Test",levelMap);
    }
/*
    MoveList ml = null;
			do {
        MatePuzzle mp = ResourceManager.getInstance().db.getMatePuzzle(moves);
        ml = GameAnalyzer.createGameFromFen(mp.fen, mp.moves);
    } while (ml==null);

 */
}

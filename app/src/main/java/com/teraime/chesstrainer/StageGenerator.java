package com.teraime.chesstrainer;

import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StageGenerator implements Runnable {
    List<StageDescriptor> stages;
    final Map<Integer,List<Types.TacticProblem>> ratedProblemMap;
    Map<Integer,List<Types.MatePuzzle>> matingPuzzleMap;
    final MySQLiteHelper myDBH;
    final GameContext gc;
    User user;
    DoneCallback cb;

    public StageGenerator(MySQLiteHelper myDBH,GameContext gameContext, DoneCallback cb) {
        this.myDBH = myDBH;
        gc = gameContext;
        user = gc.user;
        ratedProblemMap = new HashMap<>();
        this.cb = cb;
    }


    @Override
    public void run() {
        Log.d("Robo","getx");
        int min = user.rating-500;
        int max = user.rating+500;
        List<Types.TacticProblem> tacticProblems = myDBH.getTacticProblems(min,max);
        Log.d("Robo","rf "+tacticProblems.get(0).rating+" rl "+tacticProblems.get(tacticProblems.size()-1).rating);
        int increment = 100;
        tacticProblems.forEach(problem-> {
            int slot = (int) (problem.rating/100);
            List<Types.TacticProblem> l = ratedProblemMap.computeIfAbsent(slot, k -> new ArrayList<>());
            l.add(problem);
        });

        matingPuzzleMap = myDBH.getMatePuzzles();
        matingPuzzleMap.forEach((k,v)->Log.d("mate","Mate in "+k+" Probsize: "+v.size()));
        gc.setPuzzles(ratedProblemMap,matingPuzzleMap);
        cb.done();
    }
}

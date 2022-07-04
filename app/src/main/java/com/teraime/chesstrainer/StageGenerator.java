package com.teraime.chesstrainer;

import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StageGenerator implements Runnable {
    List<StageDescriptor> stages;
    MySQLiteHelper myDBH;
    User user;

    public StageGenerator(MySQLiteHelper myDBH, User user) {
        this.user = user;
        this.myDBH = myDBH;
    }


    @Override
    public void run() {
        Log.d("Robo","getx");
        int min = user.rating-500;
        int max = user.rating+500;
        List<Types.TacticProblem> tacticProblems = myDBH.getTacticProblems(min,max);
        Log.d("Robo","rf "+tacticProblems.get(0).rating+" rl "+tacticProblems.get(tacticProblems.size()-1).rating);
        int increment = 100;
        Map<Integer,List> ratedProblemMap = new HashMap();
        tacticProblems.forEach(problem-> {
            int slot = (int) (problem.rating/100);
            List l = ratedProblemMap.get(slot);
            if (l == null) {
                l = new ArrayList<Types.TacticProblem>();
                ratedProblemMap.put(slot, l);
            }
            l.add(problem);
        });
        //ratedProblemMap.forEach((key,value) -> {
        //    Log.d("v","n "+key+" "+value.size()+" "+((Types.TacticProblem)value.get(0)).rating);
        //});
        //StageDescriptorFactory.getStageDescriptor(1,u.);
    }
}

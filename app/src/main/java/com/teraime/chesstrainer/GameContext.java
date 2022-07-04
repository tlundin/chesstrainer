package com.teraime.chesstrainer;

import android.content.Context;
import android.graphics.Canvas;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.View;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class GameContext {

    Context context;
    final static ThreadPoolExecutor threadExecutorPool = (ThreadPoolExecutor) Executors.newCachedThreadPool();
    int width;
    int height;
    SurfaceHolder sh;
    static GameContext gc;
    SceneManager sceneManager;
    Map<Integer,List<Types.TacticProblem>> ratedProblemMap;
    Map<Integer,List<Types.MatePuzzle>> matingPuzzleMap;
    User user;

    public static GameContext create(User user,Context ct, SurfaceHolder sh, SceneManager sceneManager) {
        if (gc == null)
            gc = new GameContext();
        gc.context=ct;
        gc.sh = sh;
        gc.width = sh.getSurfaceFrame().width();
        gc.height = sh.getSurfaceFrame().height();
        gc.sceneManager = sceneManager;
        gc.user = user;
        return gc;
    }

    public void sceneDone(String sceneName) {
        sceneManager.onSceneDone(sceneName);
    }

    public void setPuzzles(Map<Integer, List<Types.TacticProblem>> ratedProblemMap, Map<Integer, List<Types.MatePuzzle>> matingPuzzleMap) {
        this.matingPuzzleMap = matingPuzzleMap;
        this.ratedProblemMap = ratedProblemMap;
    }

    public Types.TacticProblem getTactic(int rating) {
        int sel = rating/100;
        boolean forever = true;
        while (forever) {
            List<Types.TacticProblem> tpList = ratedProblemMap.get(sel);
            if (tpList.isEmpty())
                sel += 100;
            else
                return tpList.remove(0);
            if (sel > user.rating+500) {
                Log.e("ERR","no more tactics");
                return null;
            }
        }
        return null;
    }

    public Types.MatePuzzle getMatePuzzle(int moves) {
        List<Types.MatePuzzle> mL = matingPuzzleMap.get(moves);
        if (!mL.isEmpty())
            return mL.remove(0);
        else
            Log.e("v","no more mate puzzles size "+moves);
        return null;
    }

    public void registerClickListener(View.OnTouchListener listener) {
        sceneManager.registerClickListener(listener);
    }
    public void unregisterClickListener(View.OnTouchListener listener) {
        sceneManager.unregisterClickListener(listener);
    }


    public void incrementCurrentStage(int stage) {
        user.stage = stage;
    }


}

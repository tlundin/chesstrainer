package com.teraime.chesstrainer;

import android.view.View;

public interface SceneManager {

    public void onSceneDone(String sceneName);
    public void registerClickListener(View.OnTouchListener listener);
    public void unregisterClickListener(View.OnTouchListener listener);
}

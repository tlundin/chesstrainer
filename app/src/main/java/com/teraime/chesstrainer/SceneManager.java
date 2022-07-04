package com.teraime.chesstrainer;

import android.view.View;

public interface SceneManager {

    public void onSceneDone();
    public void registerClickListener(View.OnTouchListener listener);
}

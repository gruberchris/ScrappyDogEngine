package com.scrappydogengine;

import com.scrappydogengine.core.EngineManager;
import com.scrappydogengine.core.WindowManager;
import com.scrappydogengine.core.utils.Consts;

public class Launcher {
    private static WindowManager windowManager;

    public static void main(String[] args) {
        windowManager = new WindowManager(Consts.TITLE, 1600, 900, false);

        var testGame = new TestGame();
        var engineManager = new EngineManager(testGame);

        try {
            engineManager.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static WindowManager getWindowManager() {
        return windowManager;
    }
}

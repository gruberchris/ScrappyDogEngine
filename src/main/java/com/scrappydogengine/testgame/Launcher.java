package com.scrappydogengine.testgame;

import com.scrappydogengine.core.EngineManager;
import com.scrappydogengine.core.WindowManager;
import com.scrappydogengine.core.utils.Consts;

public class Launcher {
    public static void main(String[] args) {
        var windowManager = new WindowManager(Consts.TITLE, 1920, 1080, false);
        var testGame = new TestGame(windowManager);
        var engineManager = new EngineManager(windowManager, testGame);

        try {
            engineManager.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

package com.scrappydogengine;

import com.scrappydogengine.core.WindowManager;
import org.lwjgl.Version;

public class Launcher {
    public static void main(String[] args) {
        System.out.println(Version.getVersion());

        var window = new WindowManager("Scrappy Dog Engine", 1600, 900, false);
        window.init();

        while(!window.windowShouldClose()) {
            window.update();
        }

        window.cleanup();
    }
}

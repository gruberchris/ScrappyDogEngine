package com.scrappydogengine.core;

import com.scrappydogengine.Launcher;
import com.scrappydogengine.core.utils.Consts;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;

public class EngineManager {
    public static final long NANOSECOND = 1000000000L;
    public static final float FRAMERATE = 1000;
    private static final float FRAMETIME = 1.0f / FRAMERATE;

    private static int fps;

    private boolean isRunning;
    private WindowManager windowManager;
    private GLFWErrorCallback errorCallback;

    private void init() {
        GLFW.glfwSetErrorCallback(errorCallback = GLFWErrorCallback.createPrint(System.err));
        windowManager = Launcher.getWindowManager();
        windowManager.init();
    }

    public void start() {
        init();

        if (isRunning)
            return;

        run();
    }

    public void run() {
        isRunning = true;

        var frames = 0;
        var frameCounter = 0;
        var lastTime = System.nanoTime();
        double unprocessedTime = 0;

        while (isRunning) {
            boolean render = false;
            long startTime = System.nanoTime();
            long elapsedTime = startTime - lastTime;
            lastTime = startTime;

            unprocessedTime += elapsedTime / (double) NANOSECOND;
            frameCounter += elapsedTime;

            input();

            while (unprocessedTime > FRAMETIME) {
                render = true;
                unprocessedTime -= FRAMETIME;

                if (windowManager.windowShouldClose())
                    stop();

                if (frameCounter >= NANOSECOND) {
                    setFps(frames);
                    windowManager.setTitle(Consts.TITLE + ": " + getFps());
                    frames = 0;
                    frameCounter = 0;
                }
            }

            if (render) {
                update();
                render();
                frames++;
            }
        }

        cleanup();
    }

    public void stop() {
        if (!isRunning)
            return;

        isRunning = false;
    }

    private void input() {
        // TODO:
    }

    private void render() {
        windowManager.update();
    }

    private void update() {
        // TODO:
    }

    private void cleanup() {
        windowManager.cleanup();
        errorCallback.free();
        GLFW.glfwTerminate();
    }

    public static int getFps() {
        return fps;
    }

    public static void setFps(int fps) {
        EngineManager.fps = fps;
    }
}

package com.scrappydogengine.core;

import com.scrappydogengine.Launcher;
import com.scrappydogengine.core.utils.Consts;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;

public class EngineManager {
    public static final long NANOSECOND = 1000000000L;
    public static final float FRAMERATE = 1000;

    private static int fps;
    private static float frametime = 1.0f / FRAMERATE;

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
            long passedTime = startTime - lastTime;
            lastTime = startTime;

            unprocessedTime += passedTime / (double) NANOSECOND;
            frameCounter += passedTime;

            input();

            while (unprocessedTime > frametime) {
                render = true;
                unprocessedTime -= frametime;

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

    }

    private void render() {
        windowManager.update();
    }

    private void update() {

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

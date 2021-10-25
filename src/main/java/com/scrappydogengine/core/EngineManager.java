package com.scrappydogengine.core;

import com.scrappydogengine.core.utils.Consts;
import org.lwjgl.Version;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL11;

import static org.lwjgl.opengl.GL11.GL_VERSION;

public class EngineManager {
    public static final long NANOSECOND = 1000000000L;
    public static final float FRAMERATE = 1000;
    private static final float FRAMETIME = 1.0f / FRAMERATE;

    private static int fps;

    private boolean isRunning;
    private final WindowManager windowManager;
    private MouseInput mouseInput;
    private GLFWErrorCallback errorCallback;
    private final ILogic gameLogic;

    public EngineManager(WindowManager windowManager, ILogic gameLogic) {
        this.windowManager = windowManager;
        this.gameLogic = gameLogic;
    }

    private void init() throws Exception {
        GLFW.glfwSetErrorCallback(errorCallback = GLFWErrorCallback.createPrint(System.err));
        mouseInput = new MouseInput(windowManager);

        windowManager.init();
        gameLogic.init();
        mouseInput.init();
    }

    public void start() throws Exception {
        init();

        if (isRunning)
            return;

        run();
    }

    public void run() {
        System.out.println("LWJGL Version: " + Version.getVersion());
        System.out.println("OpenGL Version: " + GL11.glGetString(GL_VERSION));

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
                update(0);
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
        mouseInput.input();
        gameLogic.input();
    }

    private void render() {
        gameLogic.render();
        windowManager.update();
    }

    private void update(float interval) {
        gameLogic.update(interval, mouseInput);
    }

    private void cleanup() {
        gameLogic.cleanup();
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

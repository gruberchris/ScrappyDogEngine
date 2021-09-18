package com.scrappydogengine;

import com.scrappydogengine.core.ILogic;
import com.scrappydogengine.core.RenderManager;
import com.scrappydogengine.core.WindowManager;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;

public class TestGame implements ILogic {
    private int direction = 0;
    private float color = 0.0f;

    private final RenderManager renderManager;
    private final WindowManager windowManager;

    public TestGame() {
        renderManager = new RenderManager();
        windowManager = Launcher.getWindowManager();
    }

    @Override
    public void init() throws Exception {
        renderManager.init();
    }

    @Override
    public void input() {
        if (windowManager.isKeyPressed(GLFW.GLFW_KEY_UP))
            direction = 1;
        else if (windowManager.isKeyPressed(GLFW.GLFW_KEY_DOWN))
            direction = -1;
        else
            direction = 0;
    }

    @Override
    public void update() {
        color += direction * 0.01f;

        if (color > 1)
            color = 1.0f;
        else if (color <= 0)
            color = 0.0f;
    }

    @Override
    public void render() {
        if (windowManager.isResize()) {
            GL11.glViewport(0, 0, windowManager.getWidth(), windowManager.getHeight());
            windowManager.setResize(true);
        }

        windowManager.setClearColor(color, color, color, 0.0f);
        renderManager.clear();
    }

    @Override
    public void cleanup() {
        renderManager.cleanup();
    }
}

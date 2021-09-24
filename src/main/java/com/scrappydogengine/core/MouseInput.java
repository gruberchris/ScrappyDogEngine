package com.scrappydogengine.core;

import org.joml.Vector2d;
import org.joml.Vector2f;
import org.lwjgl.glfw.GLFW;

public class MouseInput {
    private final Vector2d previousPosition, currentPosition;
    private final Vector2f displayVector;

    private final WindowManager windowManager;

    private boolean inWindow = false, leftButtonPress = false, rightButtonPress = false;

    public MouseInput(WindowManager windowManager) {
        previousPosition = new Vector2d(-1, -1);
        currentPosition = new Vector2d(0, 0);
        displayVector = new Vector2f();

        this.windowManager = windowManager;
    }

    public void init() {

        // The video for tutorial #10 does not define the implementation for:
        // Main.getWindow().getWindowHandle()

        /*
        GLFW.glfwSetCursorPosCallback(Main.getWindow().getWindowHandle(), (window, xpos, ypos) -> {
            currentPosition.x = xpos;
            currentPosition.y = ypos;
        });

        GLFW.glfwSetCursorEnterCallback(Main.getWindow().getWindowHandle(), (window, entered) -> {
            inWindow = entered;
        });

        GLFW.glfwSetMouseButtonCallback(Main.getWindow().getWindowHandle(), (window, button, action, mods) -> {
            leftButtonPress = button == GLFW.GLFW_MOUSE_BUTTON_1 && action == GLFW.GLFW_PRESS;
            rightButtonPress = button == GLFW.GLFW_MOUSE_BUTTON_2 && action == GLFW.GLFW_PRESS;
        });
        */


        GLFW.glfwSetCursorPosCallback(windowManager.getWindow(), (window, xpos, ypos) -> {
            currentPosition.x = xpos;
            currentPosition.y = ypos;
        });

        GLFW.glfwSetCursorEnterCallback(windowManager.getWindow(), (window, entered) -> inWindow = entered);

        GLFW.glfwSetMouseButtonCallback(windowManager.getWindow(), (window, button, action, mods) -> {
            leftButtonPress = button == GLFW.GLFW_MOUSE_BUTTON_1 && action == GLFW.GLFW_PRESS;
            rightButtonPress = button == GLFW.GLFW_MOUSE_BUTTON_2 && action == GLFW.GLFW_PRESS;
        });
    }

    public void input() {
        displayVector.x = 0;
        displayVector.y = 0;

        if (previousPosition.x > 0 && previousPosition.y > 0 && inWindow) {
            var x = currentPosition.x - previousPosition.x;
            var y = currentPosition.y - previousPosition.y;
            var rotateX = x != 0;
            var rotateY = y != 0;

            if (rotateX)
                displayVector.y = (float) x;

            if (rotateY)
                displayVector.x = (float) y;
        }

        previousPosition.x = currentPosition.x;
        previousPosition.y = currentPosition.y;
    }

    public Vector2f getDisplayVector() {
        return displayVector;
    }

    public boolean isLeftButtonPress() {
        return leftButtonPress;
    }

    public boolean isRightButtonPress() {
        return rightButtonPress;
    }
}

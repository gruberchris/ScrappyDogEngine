package com.scrappydogengine;

import com.scrappydogengine.core.*;
import com.scrappydogengine.core.entity.Entity;
import com.scrappydogengine.core.entity.Texture;
import com.scrappydogengine.core.utils.Consts;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;

public class TestGame implements ILogic {
    private static final float CAMERA_MOVE_SPEED = 0.05f;

    private final RenderManager renderManager;
    private final WindowManager windowManager;
    private final ObjectLoader objectLoader;
    private final Camera camera;
    private final Vector3f cameraInc;

    private Entity entity;

    public TestGame() {
        renderManager = new RenderManager();
        windowManager = Launcher.getWindowManager();
        objectLoader = new ObjectLoader();
        camera = new Camera();
        cameraInc = new Vector3f(0, 0, 0);
    }

    @Override
    public void init() throws Exception {
        renderManager.init();



        var model = objectLoader.loadObjModel("/models/bunny.obj");
        model.setTexture(new Texture(objectLoader.loadTexture("textures/grassblock.png")));
        entity = new Entity(model, new Vector3f(0, 0, -2), new Vector3f(0, 0, 0), 1);
    }

    @Override
    public void input() {
        cameraInc.set(0, 0, 0);

        if (windowManager.isKeyPressed(GLFW.GLFW_KEY_W))
            cameraInc.z = -1;

        if (windowManager.isKeyPressed(GLFW.GLFW_KEY_S))
            cameraInc.z = 1;

        if (windowManager.isKeyPressed(GLFW.GLFW_KEY_A))
            cameraInc.x = -1;

        if (windowManager.isKeyPressed(GLFW.GLFW_KEY_D))
            cameraInc.x = 1;

        if (windowManager.isKeyPressed(GLFW.GLFW_KEY_Z))
            cameraInc.y = -1;

        if (windowManager.isKeyPressed(GLFW.GLFW_KEY_X))
            cameraInc.y = 1;

        if (windowManager.isKeyPressed(GLFW.GLFW_KEY_TAB)) {
            camera.setPosition(0, 0, 0);
            camera.setRotation(0, 0, 0);
        }
    }

    @Override
    public void update(float interval, MouseInput mouseInput) {
        camera.movePosition(cameraInc.x * CAMERA_MOVE_SPEED, cameraInc.y * CAMERA_MOVE_SPEED, cameraInc.z * CAMERA_MOVE_SPEED);

        if (mouseInput.isRightButtonPress()) {
            var rotateVector = mouseInput.getDisplayVector();
            camera.moveRotation(rotateVector.x * Consts.MOUSE_SENSITIVITY, rotateVector.y * Consts.MOUSE_SENSITIVITY, 0);
        }

        entity.incrementRotation(0.0f, 0.5f, 0.0f);
    }

    @Override
    public void render() {
        if (windowManager.isResize()) {
            GL11.glViewport(0, 0, windowManager.getWidth(), windowManager.getHeight());
            windowManager.setResize(true);
        }

        var color = 0.0f;

        windowManager.setClearColor(color, color, color, 0.0f);

        renderManager.render(entity, camera);
    }

    @Override
    public void cleanup() {
        renderManager.cleanup();
        objectLoader.cleanup();
    }
}

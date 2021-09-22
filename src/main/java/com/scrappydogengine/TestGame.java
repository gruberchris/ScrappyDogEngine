package com.scrappydogengine;

import com.scrappydogengine.core.ILogic;
import com.scrappydogengine.core.ObjectLoader;
import com.scrappydogengine.core.RenderManager;
import com.scrappydogengine.core.WindowManager;
import com.scrappydogengine.core.entity.Entity;
import com.scrappydogengine.core.entity.Model;
import com.scrappydogengine.core.entity.Texture;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;

public class TestGame implements ILogic {
    private int direction = 0;
    private float color = 0.0f;

    private final RenderManager renderManager;
    private final WindowManager windowManager;
    private final ObjectLoader objectLoader;

    private Entity entity;

    public TestGame() {
        renderManager = new RenderManager();
        windowManager = Launcher.getWindowManager();
        objectLoader = new ObjectLoader();
    }

    @Override
    public void init() throws Exception {
        renderManager.init();

        float[] vertices = {
                -0.5f,  0.5f, 0f,
                -0.5f, -0.5f, 0f,
                0.5f, -0.5f, 0f,
                0.5f,  0.5f, 0f,
        };

        int[] indices = {
                0, 1, 3,
                3, 1, 2
        };

        float[] textureCoords = {
                0, 0,
                0, 1,
                1, 1,
                1, 0
        };

        var model = objectLoader.loadModel(vertices, textureCoords, indices);
        model.setTexture(new Texture(objectLoader.loadTexture("textures/grassblock.png")));
        entity = new Entity(model, new Vector3f(1, 0, 0), new Vector3f(0, 0, 0), 1);
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

        var entityPosition = entity.getPosition();

        if (entityPosition.x < -1.5f)
            entityPosition.x = 1.5f;

        entityPosition.x -= 0.01f;
    }

    @Override
    public void render() {
        if (windowManager.isResize()) {
            GL11.glViewport(0, 0, windowManager.getWidth(), windowManager.getHeight());
            windowManager.setResize(true);
        }

        windowManager.setClearColor(color, color, color, 0.0f);

        renderManager.render(entity);
    }

    @Override
    public void cleanup() {
        renderManager.cleanup();
        objectLoader.cleanup();
    }
}

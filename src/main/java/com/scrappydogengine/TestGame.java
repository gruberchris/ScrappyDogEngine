package com.scrappydogengine;

import com.scrappydogengine.core.*;
import com.scrappydogengine.core.entity.Entity;
import com.scrappydogengine.core.entity.Texture;
import com.scrappydogengine.core.lighting.DirectionalLight;
import com.scrappydogengine.core.lighting.PointLight;
import com.scrappydogengine.core.lighting.SpotLight;
import com.scrappydogengine.core.utils.Consts;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TestGame implements ILogic {
    private static final float CAMERA_MOVE_SPEED = 0.05f;

    private final RenderManager renderManager;
    private final WindowManager windowManager;
    private final ObjectLoader objectLoader;

    private List<Entity> entities;
    private final Camera camera;

    private final Vector3f cameraInc;

    private float lightAngle, spotAngle = 0, spotInc = 1;
    private DirectionalLight directionalLight;
    private PointLight[] pointLights;
    private SpotLight[] spotLights;

    public TestGame() {
        renderManager = new RenderManager();
        windowManager = Launcher.getWindowManager();
        objectLoader = new ObjectLoader();
        camera = new Camera();
        cameraInc = new Vector3f(0, 0, 0);
        lightAngle = -90;
    }

    @Override
    public void init() throws Exception {
        renderManager.init();

        // model and model texture
        var model = objectLoader.loadObjModel("/models/cube.obj");
        model.setTexture(new Texture(objectLoader.loadTexture("textures/grassblock.png")), 1f);

        entities = new ArrayList<>();
        var rnd = new Random();

        for (var i = 0; i < 200; i++) {
            // renders 200 entities of the same model and texture
            var x = rnd.nextFloat() * 100 - 50;
            var y = rnd.nextFloat() * 100 - 50;
            var z = rnd.nextFloat() * -300;

            var entityPosition = new Vector3f(x, y, z);
            var entityRotation = new Vector3f(rnd.nextFloat() * 180, rnd.nextFloat() * 180, 0);
            var entityScale = 1;

            entities.add(new Entity(model, entityPosition, entityRotation, entityScale));
        }

        // TODO: There is a bug somewhere that appears to prevent the texture from rendering with the model.
        //          The entity below is the only one that has a texture. But changing the position Z value to -5f
        //          makes the texture not appear. -4f the texture is visable but looks washed out. The texture appears
        //          "normal" with a Z value of -2f.
        entities.add(new Entity(model, new Vector3f(0, 0, -3f), new Vector3f(0, 0, 0), 1));

        var lightIntensity = 1.0f;

        // point lighting
        Vector3f lightPosition = new Vector3f(-0.5f, -0.5f, -3.2f);
        Vector3f lightColor = new Vector3f(1, 1, 1);
        var pointLight = new PointLight(lightColor, lightPosition, lightIntensity, 0, 0, 1);

        // spotlight
        Vector3f coneDirection = new Vector3f(0, 0, -1);
        float cutoff = (float) Math.cos(Math.toRadians(140));
        var spotLight = new SpotLight(new PointLight(lightColor, new Vector3f(0, 0, -3.6f), lightIntensity, 0, 0, 0.2f), coneDirection, cutoff);

        var spotLight2 = new SpotLight(new PointLight(lightColor, lightPosition, lightIntensity, 0, 0, 1), coneDirection, cutoff);
        spotLight2.getPointLight().setPosition(new Vector3f(0.5f, 0.5f, -3.6f));

        // directional lighting
        lightPosition = new Vector3f(-1, -10, 0);
        lightColor = new Vector3f(1, 1, 1);
        directionalLight = new DirectionalLight(lightColor, lightPosition, lightIntensity);

        pointLights = new PointLight[] { pointLight };
        spotLights = new SpotLight[] { spotLight, spotLight2 };
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

        var lightPos = spotLights[0].getPointLight().getPosition().z;

        if (windowManager.isKeyPressed(GLFW.GLFW_KEY_N))
            spotLights[0].getPointLight().getColor().z = lightPos + 0.1f;

        if (windowManager.isKeyPressed(GLFW.GLFW_KEY_M))
            spotLights[0].getPointLight().getColor().z = lightPos - 0.1f;

        if (windowManager.isKeyPressed(GLFW.GLFW_KEY_C)) {
            camera.setPosition(0, 0, 0);
            camera.setRotation(0, 0, 0);
        }

        if (windowManager.isKeyPressed(GLFW.GLFW_KEY_O))
            pointLights[0].getPosition().x += 0.1f;

        if (windowManager.isKeyPressed(GLFW.GLFW_KEY_P))
            pointLights[0].getPosition().x -= 0.1f;
    }

    @Override
    public void update(float interval, MouseInput mouseInput) {
        camera.movePosition(cameraInc.x * CAMERA_MOVE_SPEED, cameraInc.y * CAMERA_MOVE_SPEED, cameraInc.z * CAMERA_MOVE_SPEED);

        if (mouseInput.isRightButtonPress()) {
            var rotateVector = mouseInput.getDisplayVector();
            camera.moveRotation(rotateVector.x * Consts.MOUSE_SENSITIVITY, rotateVector.y * Consts.MOUSE_SENSITIVITY, 0);
        }

        // entity.incrementRotation(0.0f, 0.25f, 0.0f);

        spotAngle += spotInc * 0.5f;

        if (spotAngle > 4)
            spotInc = -1;
        else if (spotAngle <= -4)
            spotInc = 1;

        var spotAngleRad = Math.toRadians(spotAngle);
        var coneDirection = spotLights[0].getPointLight().getPosition();
        coneDirection.y = (float) Math.sin(spotAngleRad);

        // a basic day-night environment light cycle
        lightAngle += 0.5f;

        if (lightAngle > 90) {
            directionalLight.setIntensity(0);

            if (lightAngle >= 360)
                lightAngle = -90;
        } else if (lightAngle <= -80 || lightAngle >= 80) {
            var factor = 1 - (Math.abs(lightAngle) - 80) / 10.0f;
            directionalLight.setIntensity(factor);
            directionalLight.getColor().y = Math.max(factor, 0.9f);
            directionalLight.getColor().z = Math.max(factor, 0.5f);
        } else {
            directionalLight.setIntensity(1);
            directionalLight.getColor().x = 1;
            directionalLight.getColor().y = 1;
            directionalLight.getColor().z = 1;
        }

        var angRad = Math.toRadians(lightAngle);
        directionalLight.getDirection().x = (float) Math.sin(angRad);
        directionalLight.getDirection().y = (float) Math.cos(angRad);

        for (var entity : entities) {
            renderManager.processEntity(entity);
        }
    }

    @Override
    public void render() {
        renderManager.render(camera, directionalLight, pointLights, spotLights);
    }

    @Override
    public void cleanup() {
        renderManager.cleanup();
        objectLoader.cleanup();
    }
}

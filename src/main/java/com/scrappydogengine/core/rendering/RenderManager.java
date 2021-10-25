package com.scrappydogengine.core.rendering;

import com.scrappydogengine.core.Camera;
import com.scrappydogengine.core.ShaderManager;
import com.scrappydogengine.core.WindowManager;
import com.scrappydogengine.core.entity.Entity;
import com.scrappydogengine.core.entity.Model;
import com.scrappydogengine.core.lighting.DirectionalLight;
import com.scrappydogengine.core.lighting.PointLight;
import com.scrappydogengine.core.lighting.SpotLight;
import com.scrappydogengine.core.utils.Consts;
import com.scrappydogengine.core.utils.Transformation;
import com.scrappydogengine.core.utils.Utils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RenderManager {
    private final String textureSamplerUniformName = "textureSampler";
    private final String transformationMatrixUniformName = "transformationMatrix";
    private final String projectionMatrixUniformName = "projectionMatrix";
    private final String viewMatrixUniformName = "viewMatrix";
    private final String ambientLightUniformName = "ambientLight";
    private final String materialUniformName = "material";
    private final String specularPowerUniformName = "specularPower";
    private final String directionalLightUniformName = "directionalLight";
    private final String pointLightListUniformName = "pointLights";
    private final String spotLightListUniformName = "spotLights";

    private final WindowManager windowManager;
    private ShaderManager shaderManager;

    private final Map<Model, List<Entity>> entities = new HashMap<>();

    public RenderManager(WindowManager windowManager) {
        this.windowManager = windowManager;
    }

    public void init() throws Exception {
        shaderManager = new ShaderManager();
        shaderManager.createVertexShader(Utils.loadResource("/shaders/vertex.glsl"));
        shaderManager.createFragmentShader(Utils.loadResource("/shaders/fragment.glsl"));
        shaderManager.link();
        shaderManager.createUniform(textureSamplerUniformName);
        shaderManager.createUniform(transformationMatrixUniformName);
        shaderManager.createUniform(projectionMatrixUniformName);
        shaderManager.createUniform(viewMatrixUniformName);
        shaderManager.createUniform(ambientLightUniformName);
        shaderManager.createMaterialUniform(materialUniformName);
        shaderManager.createUniform(specularPowerUniformName);
        shaderManager.createDirectionalLightUniform(directionalLightUniformName);
        shaderManager.createPointLightListUniform(pointLightListUniformName, 5);
        shaderManager.createSpotLightListUniform(spotLightListUniformName, 5);
    }

    public void bind(Model model) {
        GL30.glBindVertexArray(model.getId());
        GL20.glEnableVertexAttribArray(0);
        GL20.glEnableVertexAttribArray(1);
        GL20.glEnableVertexAttribArray(2);

        shaderManager.setUniform(materialUniformName, model.getMaterial());

        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, model.getTexture().getId());
    }

    public void unbind() {
        GL20.glDisableVertexAttribArray(0);
        GL20.glDisableVertexAttribArray(1);
        GL20.glDisableVertexAttribArray(2);
        GL30.glBindVertexArray(0);
    }

    public void prepare(Entity entity, Camera camera) {
        shaderManager.setUniform(textureSamplerUniformName, 0);
        shaderManager.setUniform(transformationMatrixUniformName, Transformation.createTransformationMatrix(entity));
        shaderManager.setUniform(viewMatrixUniformName, Transformation.getViewMatrix(camera));
    }

    public void renderLights(Camera camera, PointLight[] pointLights, SpotLight[] spotLights, DirectionalLight directionalLight) {
        shaderManager.setUniform(ambientLightUniformName, Consts.AMBIENT_LIGHT);
        shaderManager.setUniform(specularPowerUniformName, Consts.SPECULAR_POWER);

        var numLights = spotLights != null ? spotLights.length : 0;

        for (var i = 0; i < numLights; i++) {
            shaderManager.setUniform("spotLights", spotLights[i], i);
        }

        numLights = pointLights != null ? pointLights.length : 0;

        for (var i = 0; i < numLights; i++) {
            shaderManager.setUniform("pointLights", pointLights[i], i);
        }

        shaderManager.setUniform(directionalLightUniformName, directionalLight);
    }

    public void render(Camera camera, DirectionalLight directionalLight, PointLight[] pointLights, SpotLight[] spotLights) {
        clear();

        if (windowManager.isResize()) {
            GL11.glViewport(0, 0, windowManager.getWidth(), windowManager.getHeight());
            windowManager.setResize(true);
        }

        shaderManager.bind();

        shaderManager.setUniform(projectionMatrixUniformName, windowManager.updateProjectionMatrix());
        renderLights(camera, pointLights, spotLights, directionalLight);
        for (var model : entities.keySet()) {
            bind(model);
            List<Entity> entityList = entities.get(model);

            for (var entity : entityList) {
                prepare(entity, camera);
                GL11.glDrawElements(GL11.GL_TRIANGLES, model.getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
            }

            unbind();
        }

        entities.clear();
        shaderManager.unbind();
    }

    public void processEntity(Entity entity) {
        List<Entity> entityList = entities.get(entity.getModel());

        if (entityList != null) {
            entityList.add(entity);
        } else {
            List<Entity> newEntityList = new ArrayList<>();
            newEntityList.add(entity);
            entities.put(entity.getModel(), newEntityList);
        }
    }

    public void clear() {
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
    }

    public void cleanup() {
        shaderManager.cleanup();
    }
}

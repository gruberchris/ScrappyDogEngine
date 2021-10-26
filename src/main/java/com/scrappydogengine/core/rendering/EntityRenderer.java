package com.scrappydogengine.core.rendering;

import com.scrappydogengine.core.Camera;
import com.scrappydogengine.core.ShaderManager;
import com.scrappydogengine.core.WindowManager;
import com.scrappydogengine.core.entity.Entity;
import com.scrappydogengine.core.entity.Model;
import com.scrappydogengine.core.lighting.DirectionalLight;
import com.scrappydogengine.core.lighting.PointLight;
import com.scrappydogengine.core.lighting.SpotLight;
import com.scrappydogengine.core.utils.Transformation;
import com.scrappydogengine.core.utils.Utils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EntityRenderer implements IRenderer<Entity> {
    ShaderManager shaderManager;
    WindowManager windowManager;

    private Map<Model, List<Entity>> entities;

    private final String textureSamplerUniformName = "textureSampler";
    private final String transformationMatrixUniformName = "transformationMatrix";
    private final String projectionMatrixUniformName = "projectionMatrix";
    private final String viewMatrixUniformName = "viewMatrix";
    private final String materialUniformName = "material";
    private final String ambientLightUniformName = "ambientLight";
    private final String specularPowerUniformName = "specularPower";
    private final String directionalLightUniformName = "directionalLight";
    private final String pointLightListUniformName = "pointLights";
    private final String spotLightListUniformName = "spotLights";

    public EntityRenderer(WindowManager windowManager) throws Exception {
        entities = new HashMap<>();
        shaderManager = new ShaderManager();
        this.windowManager = windowManager;
    }

    @Override
    public void init() throws Exception {
        shaderManager.createVertexShader(Utils.loadResource("/shaders/entity_vertex.glsl"));
        shaderManager.createFragmentShader(Utils.loadResource("/shaders/entity_fragment.glsl"));
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

    @Override
    public void render(Camera camera, PointLight[] pointLights, SpotLight[] spotLights, DirectionalLight directionalLight) {
        shaderManager.bind();

        shaderManager.setUniform(projectionMatrixUniformName, windowManager.updateProjectionMatrix());

        // TODO:
        RenderManager.renderLights(pointLights, spotLights, directionalLight, shaderManager);

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

    @Override
    public void bind(Model model) {
        GL30.glBindVertexArray(model.getId());
        GL20.glEnableVertexAttribArray(0);
        GL20.glEnableVertexAttribArray(1);
        GL20.glEnableVertexAttribArray(2);

        shaderManager.setUniform(materialUniformName, model.getMaterial());

        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, model.getTexture().getId());
    }

    @Override
    public void unbind() {
        GL20.glDisableVertexAttribArray(0);
        GL20.glDisableVertexAttribArray(1);
        GL20.glDisableVertexAttribArray(2);
        GL30.glBindVertexArray(0);
    }

    @Override
    public void prepare(Entity entity, Camera camera) {
        shaderManager.setUniform(textureSamplerUniformName, 0);
        shaderManager.setUniform(transformationMatrixUniformName, Transformation.createTransformationMatrix(entity));
        shaderManager.setUniform(viewMatrixUniformName, Transformation.getViewMatrix(camera));
    }

    @Override
    public void cleanup() {
        shaderManager.cleanup();
    }

    public Map<Model, List<Entity>> getEntities() {
        return entities;
    }
}

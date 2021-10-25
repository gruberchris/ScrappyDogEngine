package com.scrappydogengine.core.rendering;

import com.scrappydogengine.core.Camera;
import com.scrappydogengine.core.entity.Model;
import com.scrappydogengine.core.lighting.DirectionalLight;
import com.scrappydogengine.core.lighting.PointLight;
import com.scrappydogengine.core.lighting.SpotLight;

public interface IRenderer<T> {
    void init() throws Exception;

    void render(Camera camera, PointLight[] pointLights, SpotLight[] spotLights, DirectionalLight directionalLight);

    void bind(Model model);

    void unbind();

    void prepare(T t, Camera camera);

    void cleanup();
}

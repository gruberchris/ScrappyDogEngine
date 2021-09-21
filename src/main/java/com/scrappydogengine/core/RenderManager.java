package com.scrappydogengine.core;

import com.scrappydogengine.Launcher;
import com.scrappydogengine.core.entity.Model;
import com.scrappydogengine.core.utils.Utils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

public class RenderManager {
    private final WindowManager windowManager;
    private ShaderManager shaderManager;

    public RenderManager() {
        windowManager = Launcher.getWindowManager();
    }

    public void init() throws Exception {
        shaderManager = new ShaderManager();
        shaderManager.createVertexShader(Utils.loadResource("/shaders/vertex.glsl"));
        shaderManager.createFragmentShader(Utils.loadResource("/shaders/fragment.glsl"));
        shaderManager.link();
    }

    public void render(Model model) {
        clear();

        shaderManager.bind();

        GL30.glBindVertexArray(model.getId());
        GL20.glEnableVertexAttribArray(0);
        GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, model.getVertexCount());
        GL20.glDisableVertexAttribArray(0);
        GL30.glBindVertexArray(0);

        shaderManager.unbind();
    }

    public void clear() {
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
    }

    public void cleanup() {
        shaderManager.cleanup();
    }
}

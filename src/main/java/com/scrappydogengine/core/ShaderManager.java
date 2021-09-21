package com.scrappydogengine.core;

import org.joml.Matrix4f;
import org.lwjgl.opengl.GL20;
import org.lwjgl.system.MemoryStack;

import java.util.HashMap;
import java.util.Map;

public class ShaderManager {
    private final int shaderInfoLogMaxLength = 1024;

    private final int programId;
    private int vertexShaderId, fragmentShaderId;

    private final Map<String, Integer> uniforms;

    public ShaderManager() throws Exception {
        programId = GL20.glCreateProgram();

        if (programId == 0)
            throw new Exception("Could not create shader");

        uniforms = new HashMap<>();
    }

    public void createUniform(String uniformName) throws Exception {
        var uniformLocation = GL20.glGetUniformLocation(programId, uniformName);

        if (uniformLocation < 0)
            throw new Exception("Could not find uniform " + uniformName);

        uniforms.put(uniformName, uniformLocation);
    }

    public void setUniform(String uniformName, Matrix4f value) {
        try(var stack = MemoryStack.stackPush()) {
            GL20.glUniformMatrix4fv(uniforms.get(uniformName), false, value.get(stack.mallocFloat(16)));
        }
    }

    public void setUniform(String uniformName, int value) {
        GL20.glUniform1i(uniforms.get(uniformName), value);
    }

    public void createVertexShader(String shaderCode) throws Exception {
        vertexShaderId = createShader(shaderCode, GL20.GL_VERTEX_SHADER);
    }

    public void createFragmentShader(String shaderCode) throws Exception {
        fragmentShaderId =  createShader(shaderCode, GL20.GL_FRAGMENT_SHADER);
    }

    public int createShader(String shaderCode, int shaderType) throws Exception {
        var shaderId = GL20.glCreateShader(shaderType);

        if (shaderId == 0)
            throw new Exception("Error creating shader. Type : " + shaderType);

        GL20.glShaderSource(shaderId, shaderCode);
        GL20.glCompileShader(shaderId);

        if (GL20.glGetShaderi(shaderId, GL20.GL_COMPILE_STATUS) == 0) {
            var exceptionMessage = "Error compiling shader code: TYPE : " + shaderType + " Info "
                    + GL20.glGetShaderInfoLog(shaderId, shaderInfoLogMaxLength);

            throw new Exception(exceptionMessage);
        }

        GL20.glAttachShader(programId, shaderId);

        return shaderId;
    }

    public void link() throws Exception {
        GL20.glLinkProgram(programId);

        if (GL20.glGetProgrami(programId, GL20.GL_LINK_STATUS) == 0) {
            var exceptionMessage = "Error linking shader code. Info " + GL20.glGetShaderInfoLog(programId, shaderInfoLogMaxLength);
            throw new Exception(exceptionMessage);
        }

        if (vertexShaderId != 0)
            GL20.glDetachShader(programId, vertexShaderId);

        if (fragmentShaderId != 0)
            GL20.glDetachShader(programId, fragmentShaderId);

        GL20.glValidateProgram(programId);

        if (GL20.glGetProgrami(programId, GL20.GL_VALIDATE_STATUS) == 0) {
            var exceptionMessage = "Unable to validate shader code: " + GL20.glGetProgramInfoLog(programId, shaderInfoLogMaxLength);
            throw new Exception(exceptionMessage);
        }
    }

    public void bind() {
        GL20.glUseProgram(programId);
    }

    public void unbind() {
        GL20.glUseProgram(0);
    }

    public void cleanup() {
        unbind();

        if (programId != 0)
            GL20.glDeleteProgram(programId);
    }
}

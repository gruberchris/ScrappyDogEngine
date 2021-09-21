package com.scrappydogengine.core;

import com.scrappydogengine.core.entity.Model;
import com.scrappydogengine.core.utils.Utils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class ObjectLoader {
    // vertex array object list
    // coordinates of an object
    private final List<Integer> vaos = new ArrayList<>();

    // vertex buffer object list
    // coordinates of the texture of an object
    private final List<Integer> vbos = new ArrayList<>();

    private final List<Integer> textures = new ArrayList<>();

    public Model loadModel(float[] vertices, float[] textureCoords, int[] indices) {
        var id = createVAO();
        storeIndicesBuffer(indices);
        storeDataInAttribList(0, 3, vertices);
        storeDataInAttribList(1, 2, textureCoords);
        unbind();

        return new Model(id, indices.length);
    }

    public int loadTexture(String filename) throws Exception {
        int width, height;
        ByteBuffer byteBuffer;

        try(var stack = MemoryStack.stackPush()) {
            var w = stack.mallocInt(1);
            var h = stack.mallocInt(1);
            var c = stack.mallocInt(1);

            // 4 channels in file: red, green, blue and alpha
            final int desiredChannels = 4;

            byteBuffer = STBImage.stbi_load(filename, w, h, c, desiredChannels);

            if (byteBuffer == null) {
                var exceptionMessage = "Texture Image File " + filename + " not loaded " + STBImage.stbi_failure_reason();
                throw new Exception(exceptionMessage);
            }

            width = w.get();
            height = h.get();
        }

        var id = GL11.glGenTextures();

        textures.add(id);

        GL11.glBindTexture(GL11.GL_TEXTURE_2D, id);
        GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 1);
        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, width, height, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, byteBuffer);
        GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D);
        STBImage.stbi_image_free(byteBuffer);

        return id;
    }

    private int createVAO() {
        var id = GL30.glGenVertexArrays();
        vaos.add(id);
        GL30.glBindVertexArray(id);

        return id;
    }

    private void storeIndicesBuffer(int[] indices) {
        var vbo = GL15.glGenBuffers();
        vbos.add(vbo);
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, vbo);

        var buffer = Utils.storeDataInIntegerBuffer(indices);
        GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
    }

    private void storeDataInAttribList(int attribNo, int vertexCount, float[] data) {
        int vbo = GL15.glGenBuffers();
        vbos.add(vbo);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo);

        var buffer = Utils.storeDataInFloatBuffer(data);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
        GL20.glVertexAttribPointer(attribNo, vertexCount, GL11.GL_FLOAT, false, 0, 0);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
    }

    private void unbind() {
        GL30.glBindVertexArray(0);
    }

    public void cleanup() {
        for(var vao: vaos)
            GL30.glDeleteVertexArrays(vao);

        for(var vbo: vbos)
            GL30.glDeleteBuffers(vbo);

        for(var texture : textures)
            GL11.glDeleteTextures(texture);
    }
}

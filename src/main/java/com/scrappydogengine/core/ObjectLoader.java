package com.scrappydogengine.core;

import com.scrappydogengine.core.entity.Model;
import com.scrappydogengine.core.utils.Utils;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector3i;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
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

    public Model loadObjModel(String fileName) {
        var lines = Utils.readAllLines(fileName);

        var vertices = new ArrayList<Vector3f>();
        var normals = new ArrayList<Vector3f>();
        var textures = new ArrayList<Vector2f>();
        var faces = new ArrayList<Vector3i>();

        for (String line : lines) {
            var tokens = line.split("\\s+");
            switch (tokens[0]) {
                case "v" -> {
                    // vertices
                    var verticesVec = new Vector3f(
                            Float.parseFloat(tokens[1]),
                            Float.parseFloat(tokens[2]),
                            Float.parseFloat(tokens[3])
                    );
                    vertices.add(verticesVec);
                }
                case "vt" -> {
                    // vertex textures
                    var textureVec = new Vector2f(
                            Float.parseFloat(tokens[1]),
                            Float.parseFloat(tokens[2])
                    );
                    textures.add(textureVec);
                }
                case "vn" -> {
                    // vertex normals
                    var normalsVec = new Vector3f(
                            Float.parseFloat(tokens[1]),
                            Float.parseFloat(tokens[2]),
                            Float.parseFloat(tokens[3])
                    );
                    normals.add(normalsVec);
                }
                case "f" -> {
                    // faces
                    processFace(tokens[1], faces);
                    processFace(tokens[2], faces);
                    processFace(tokens[3], faces);
                }
                default -> {
                }
            }
        }

        var indices = new ArrayList<Integer>();
        var verticesArray = new float[vertices.size() * 3];
        var i = 0;

        for(var pos : vertices) {
            verticesArray[i * 3] = pos.x;
            verticesArray[i * 3 + 1] = pos.y;
            verticesArray[i * 3 + 2] = pos.z;
            i++;
        }

        var textureCoordinateArray = new float[vertices.size() * 2];
        var normalArray = new float[vertices.size() * 3];

        for (var face : faces) {
            processVertex(face.x, face.y, face.z, textures, normals, indices, textureCoordinateArray, normalArray);
        }

        var indicesArray = indices.stream().mapToInt((Integer v) -> v).toArray();

        return loadModel(verticesArray, textureCoordinateArray, normalArray, indicesArray);
    }

    private static void processVertex(int pos, int textCoord, int normal, List<Vector2f> texCoordList, List<Vector3f> normalList, List<Integer> indicesList, float[] texCoordArr, float[] normalArr) {
        indicesList.add(pos);

        if (textCoord >= 0) {
            var texCoordVec = texCoordList.get(textCoord);
            texCoordArr[pos * 2] = texCoordVec.x;
            texCoordArr[pos * 2 + 1] = 1 - texCoordVec.y;
        }

        if (normal >= 0) {
            var normalVec = normalList.get(normal);
            normalArr[pos * 3] = normalVec.x;
            normalArr[pos * 3 + 1] = normalVec.y;
            normalArr[pos * 3 + 2] = normalVec.z;
        }
    }

    private static void processFace(String token, List<Vector3i> faces) {
        var lineToken = token.split("/");
        var length = lineToken.length;
        int coords = -1, normal = -1;

        var pos = Integer.parseInt(lineToken[0]) - 1;

        if (length > 1) {
            var textCoord = lineToken[1];
            coords = textCoord.length() > 0 ? Integer.parseInt(textCoord) - 1: -1;

            if (length > 2)
                normal = Integer.parseInt(lineToken[2]) - 1;
        }

        var facesVec = new Vector3i(pos, coords, normal);

        faces.add(facesVec);
    }

    public Model loadModel(float[] vertices, float[] textureCoords, float[] normals, int[] indices) {
        var id = createVAO();
        storeIndicesBuffer(indices);
        storeDataInAttribList(0, 3, vertices);
        storeDataInAttribList(1, 2, textureCoords);
        storeDataInAttribList(2, 3, normals);
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

        IntBuffer buffer = null;

        try {
            buffer = Utils.storeDataInIntegerBuffer(indices);
            GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
        } finally {
            if (buffer != null) {
                MemoryUtil.memFree(buffer);
            }
        }
    }

    private void storeDataInAttribList(int attribNo, int vertexCount, float[] data) {
        int vbo = GL15.glGenBuffers();
        vbos.add(vbo);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo);

        FloatBuffer buffer = null;

        try {
            buffer = Utils.storeDataInFloatBuffer(data);
            GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
            GL20.glVertexAttribPointer(attribNo, vertexCount, GL11.GL_FLOAT, false, 0, 0);
            GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
        } finally {
            if (buffer != null) {
                MemoryUtil.memFree(buffer);
            }
        }
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

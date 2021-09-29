package com.scrappydogengine.core.entity;

import org.w3c.dom.Text;

public class Model {
    private int id;
    private int vertexCount;
    private Material material;

    public Model(int id, int vertexCount) {
        this.id = id;
        this.vertexCount = vertexCount;
        material = new Material();
    }

    public Model(int id, int vertexCount, Texture texture) {
        this.id = id;
        this.vertexCount = vertexCount;
        material = new Material(texture);
    }

    public Model(Model model, Texture texture) {
        id = model.getId();
        vertexCount = model.getVertexCount();
        material = model.getMaterial();
        material.setTexture(texture);
    }

    public int getId() {
        return id;
    }

    public int getVertexCount() {
        return vertexCount;
    }

    public Material getMaterial() {
        return material;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }

    public Texture getTexture() {
        return material.getTexture();
    }

    public void setMaterial(Texture texture) {
        material.setTexture(texture);
    }

    public void setTexture(Texture texture, float reflectance) {
        material.setTexture(texture);
        material.setReflectance(reflectance);
    }
}

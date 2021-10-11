package com.scrappydogengine.core.lighting;

import org.joml.Vector3f;

public class SpotLight {
    private PointLight pointLight;
    private Vector3f coneDirection;
    private float cutoff;

    public SpotLight(PointLight pointLight, Vector3f codeDirection, float cutoff) {
        this.pointLight = pointLight;
        this.coneDirection = codeDirection;
        this.cutoff = cutoff;
    }

    public SpotLight(SpotLight spotLight) {
        pointLight = spotLight.getPointLight();
        coneDirection = spotLight.getConeDirection();
        cutoff = spotLight.getCutoff();
    }

    public PointLight getPointLight() {
        return pointLight;
    }

    public void setPointLight(PointLight pointLight) {
        this.pointLight = pointLight;
    }

    public Vector3f getConeDirection() {
        return coneDirection;
    }

    public void setConeDirection(Vector3f coneDirection) {
        this.coneDirection = coneDirection;
    }

    public float getCutoff() {
        return cutoff;
    }

    public void setCutoff(float cutoff) {
        this.cutoff = cutoff;
    }
}

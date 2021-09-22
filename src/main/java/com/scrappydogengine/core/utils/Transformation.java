package com.scrappydogengine.core.utils;

import com.scrappydogengine.core.entity.Entity;
import org.joml.Matrix4f;

public class Transformation {
    public static Matrix4f createTransformationMatrix(Entity entity) {
        var matrix = new Matrix4f();

        matrix.identity().translate(entity.getPosition()).
                rotateX((float) Math.toRadians(entity.getRotation().x)).
                rotateY((float) Math.toRadians(entity.getRotation().y)).
                rotateZ((float) Math.toRadians(entity.getRotation().z)).
                scale(entity.getScale());

        return matrix;
    }
}

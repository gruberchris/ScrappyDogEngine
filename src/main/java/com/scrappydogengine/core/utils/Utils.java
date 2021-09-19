package com.scrappydogengine.core.utils;

import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;

public class Utils {
    public static FloatBuffer storeDataInFloatBuffer(float[] data) {
        var buffer = MemoryUtil.memAllocFloat(data.length);
        buffer.put(data).flip();
        return buffer;
    }
}

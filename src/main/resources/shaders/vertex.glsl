#version 400 core

in vec3 position;
in vec2 textureCoord;

out vec2 textureCoordFragment;

void main() {
    gl_Position = vec4(position, 1.0);
    textureCoordFragment = textureCoord;
}
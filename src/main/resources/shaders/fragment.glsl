#version 400 core

in vec2 textureCoordFragment;
in vec3 fragNormal;
in vec3 fragPos;

out vec4 fragmentColor;

struct Material {
    vec4 ambient;
    vec4 diffuse;
    vec4 specular;
    int hasTexture;
    float reflectance;
};

struct DirectionalLight {
    vec3 color;
    vec3 direction;
    float intensity;
};

struct PointLight {
    vec3 color;
    vec3 position;
    float intensity;
    float constant;
    float linear;
    float exponent;
};

uniform sampler2D textureSampler;
uniform vec3 ambientLight;
uniform Material material;
uniform float specularPower;
uniform DirectionalLight directionalLight;
uniform PointLight pointLight;

vec4 ambientC;
vec4 diffuseC;
vec4 specularC;

void setupColors(Material material, vec2 textureCoordinates) {
    if (material.hasTexture == 1) {
        ambientC = texture(textureSampler, textureCoordinates);
        diffuseC = ambientC;
        specularC = ambientC;
    } else {
        ambientC = material.ambient;
        diffuseC = material.diffuse;
        specularC = material.specular;
    }
}

vec4 calcLightColor(vec3 light_color, float light_intensity, vec3 position, vec3 to_light_direction, vec3 normal) {
    vec4 diffuseColor = vec4(0, 0, 0, 0);
    vec4 specularColor = vec4(0, 0, 0, 0);

    // diffuse light
    float diffuseFactor = max(dot(normal, to_light_direction), 0.0);
    diffuseColor = diffuseC * vec4(light_color, 1.0) * light_intensity * diffuseFactor;

    // specular color
    vec3 camera_direction = normalize(-position);
    vec3 from_light_direction = -to_light_direction;
    vec3 reflectedLight = normalize(reflect(from_light_direction, normal));
    float specularFactor = max(dot(camera_direction, reflectedLight), 0.0);
    specularFactor = pow(specularFactor, specularPower);
    specularColor = specularC * light_intensity * specularFactor * material.reflectance * vec4(light_color, 1.0);

    return diffuseColor + specularColor;
}

vec4 calcPointLight(PointLight pointLight, vec3 position, vec3 normal) {
    vec3 light_direction = pointLight.position - position;
    vec3 to_light_direction = normalize(light_direction);
    vec4 light_color = calcLightColor(pointLight.color, pointLight.intensity, position, to_light_direction, normal);

    // attenuation
    float distance = length(light_direction);
    float attenuationInv = pointLight.constant + pointLight.linear * distance + pointLight.exponent * distance * distance;
    return light_color / attenuationInv;
}

vec4 calcDirectionalLight(DirectionalLight directionalLight, vec3 position, vec3 normal) {
    return calcLightColor(directionalLight.color, directionalLight.intensity, position, normalize(directionalLight.direction), normal);
}

void main() {
    setupColors(material, textureCoordFragment);

    vec4 diffuseSpecularComp = calcDirectionalLight(directionalLight, fragPos, fragNormal);
    diffuseSpecularComp += calcPointLight(pointLight, fragPos, fragNormal);

    fragmentColor = ambientC * vec4(ambientLight, 1) + diffuseSpecularComp;
}
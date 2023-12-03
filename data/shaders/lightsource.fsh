#version 330 core

in vec2 fTexelCoord;

out vec4 FragColour;

struct Material {
    sampler2D diffuse;
    vec3 specular;
    float shininess;
};

uniform Material material;

void main() {
    //Make the cube white.
    //Different shader for light sources, so the object colour is not affected by lighting calculations
    FragColour = texture(material.diffuse, fTexelCoord);
}
#version 330 core

in vec2 fTexelCoord;

out vec4 FragColour;

struct Material {
    vec3 objectColour;
    vec3 ambient;
    vec3 diffuse;
    vec3 specular;
    float shininess;
    sampler2D main2D;
};

uniform Material material;

void main() {
    //Make the cube white.
    //Different shader for light sources, so the object colour is not affected by lighting calculations
    FragColour = texture(material.main2D, fTexelCoord) * vec4(material.objectColour, 1.0f);
}
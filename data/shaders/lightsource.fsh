#version 330 core

in vec2 fTexelCoord;

out vec4 FragColour;

uniform sampler2D main2D;
uniform vec3 objectColour;

void main() {
    //Make the cube white.
    //Different shader for light sources, so the object colour is not affected by lighting calculations
    FragColour = texture(main2D, fTexelCoord) * vec4(objectColour, 1.0f);
}
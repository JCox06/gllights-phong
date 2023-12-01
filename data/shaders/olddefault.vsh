#version 330 core

layout (location = 0) in vec3 aPos;
layout (location = 1) in vec3 aNormal;
layout (location = 2) in vec2 aTex;

uniform mat4 camera;
uniform mat4 projection;
uniform mat4 model;

out vec2 fTexelCoord;
out vec3 fNormal;
out vec3 fWorldPos;

void main() {
    fTexelCoord = aTex;


    //(simplified proof from learnopengl and lighthouse3d)
    //Normal and vertex vectors are perpendicular (by definition) so:
    // Normal Vector {dot} Vertex Vector = 0
    //When transformed into worldspace correctly, this should still be true
    // let vec a = Normal Model Matrix * Normal (assuming the normal model matrix is unknown)
    // let vec b = model matrix * vertex then:
    // a {dot} b = 0
    // => a^t * b = 0 (Matrix-Matrix Mulitplication)
    //(When you continue expanding and resubstitute vectors A and B)
    //(You can form an equation in terms of the model matrix for the normal model matrix)
    //Normal model matrix = transpose(inverse(modelMatrix))
    fNormal = mat3(transpose(inverse(model))) * aNormal;
    fWorldPos = vec3(model * vec4(aPos, 1.0f));
    gl_Position = projection * camera * model * vec4(aPos, 1.0f);
}
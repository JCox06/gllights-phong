#version 330 core

out vec4 FragColour;

in vec2 fTexelCoord;
in vec3 fNormal;

//This is multipled by the model matrix so the coords are in worldspace
in vec3 fWorldPos;

uniform vec3 cameraPos;


uniform sampler2D main2D;
uniform vec3 diffuseColour;
uniform vec3 lightColour;
uniform vec3 lightLoc;

void main() {

    //We have to normalize this normal as fragments are linearly interpolated. This can cause the magnitude to differ from 1.
    vec3 norm = normalize(fNormal);


    //Direction from the light source to the frag position in worldspace
    vec3 direction = normalize(lightLoc - fWorldPos);

    //Get how close the direction is to the normal
    //A direction vector parralell to the normal will have a greater effect
    float diffSF = max(dot(norm, direction), 0.0f);
    //The greates effect is when the dot product returns 1
    //Note we have to add a max check. Vectors that are anti parallel will return -1 (Pointing away from the light source)


    vec3 finalDiffuse = diffSF * lightColour;

    float specularStrength = 0.5f;

    //Direction from camera to fragment
    vec3 viewDir = normalize(cameraPos - fWorldPos);
    vec3 reflectDir = reflect(-direction, norm);
    float spec = pow(max(dot(viewDir, reflectDir), 0.0f), 32);
    vec3 specular = specularStrength * spec * lightColour;


    float ambientStrength = 0.1f;

    vec3 ambient = ambientStrength * lightColour;
    vec3 resultingBacklight = ( ambient + finalDiffuse + specular) * diffuseColour;

    FragColour = texture(main2D, fTexelCoord) * vec4(resultingBacklight, 1.0f);
}
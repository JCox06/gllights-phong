#version 330 core

//Phong lighting in world space

out vec4 FragColour;

in vec2 fTexelCoord;
in vec3 fNormal;
in vec3 fWorldPos;

uniform vec3 cameraPos;
uniform vec3 objectColour;
uniform vec3 lightColour;
uniform vec3 lightPos;

uniform sampler2D main2D;

vec3 calcAmbient(vec3 lightColour, float strength);
vec3 calcDiffuse(vec3 lightColour, vec3 lightDir, vec3 normal);
vec3 calcSpecular(vec3 lightColour, vec3 lightDir, vec3 normal);
vec4 getFinalColour(vec3 ambience, vec3 diffuse, vec3 specular, sampler2D tex);


void main() {
    vec3 normalVec = normalize(fNormal);
    vec3 fragToLight = normalize(lightPos - fWorldPos); //Frag to Light

    vec3 ambientComponent = calcAmbient(lightColour, 0.1f);
    vec3 diffuseComponent = calcDiffuse(lightColour, fragToLight, normalVec);
    vec3 specularComponent = calcSpecular(lightColour, fragToLight, normalVec);

    FragColour = getFinalColour(ambientComponent, diffuseComponent, specularComponent, main2D);
}


vec3 calcAmbient(vec3 lightColour, float strength) {
    return lightColour * strength;
}


vec3 calcDiffuse(vec3 lightColour, vec3 lightDir, vec3 normal) {
    //Calculate how close the light direction is to the normal
    float scaleFactor = max(dot(normal, lightDir), 0.0f);
    vec3 diffuseShading = scaleFactor * lightColour;
    return diffuseShading;
}


vec3 calcSpecular(vec3 lightColour, vec3 lightDir,vec3 normal) {
    float specularStength = 0.5f;
    vec3 fragToCam = normalize(cameraPos - fWorldPos);
    vec3 reflectDir = reflect(-lightDir, normal); //Calculate relflection ray from indicent ray
    float specScaleFactor = pow(max(dot(fragToCam, reflectDir), 0.0f), 32) * specularStength;
    vec3 specularShading = lightColour * specScaleFactor;
    return specularShading;
}


vec4 getFinalColour(vec3 ambience, vec3 diffuse, vec3 specular, sampler2D tex) {
    vec3 newColour = (ambience + diffuse + specular) * objectColour;
    vec4 colour = texture(tex, fTexelCoord) * vec4(newColour, 1.0f);
    return colour;
}

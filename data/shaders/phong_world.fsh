#version 330 core

//Phong lighting in world space

out vec4 FragColour;

in vec2 fTexelCoord;
in vec3 fNormal;
in vec3 fWorldPos;

uniform vec3 cameraPos;
uniform vec3 lightColour;
uniform vec3 lightPos;

struct Material {
    vec3 objectColour;
    vec3 ambient;
    vec3 diffuse;
    vec3 specular;
    float shininess;
    sampler2D main2D;
};

uniform Material material;


vec3 calcAmbient(vec3 lightColour, vec3 strength);
vec3 calcDiffuse(vec3 lightColour, vec3 lightDir, vec3 normal);
vec3 calcSpecular(vec3 lightColour, vec3 lightDir, vec3 normal);
vec4 getFinalColour(vec3 ambience, vec3 diffuse, vec3 specular, sampler2D tex);


void main() {
    vec3 normalVec = normalize(fNormal);
    vec3 fragToLight = normalize(lightPos - fWorldPos); //Frag to Light

    vec3 ambientComponent = calcAmbient(lightColour, material.ambient);
    vec3 diffuseComponent = calcDiffuse(lightColour, fragToLight, normalVec);
    vec3 specularComponent = calcSpecular(lightColour, fragToLight, normalVec);

    FragColour = getFinalColour(ambientComponent, diffuseComponent, specularComponent, material.main2D);
}


vec3 calcAmbient(vec3 lightColour, vec3 strength) {
    return lightColour * strength;
}


vec3 calcDiffuse(vec3 lightColour, vec3 lightDir, vec3 normal) {
    //Calculate how close the light direction is to the normal
    float scaleFactor = max(dot(normal, lightDir), 0.0f);
    vec3 diffuseShading = scaleFactor * lightColour * material.diffuse;
    return diffuseShading;
}


vec3 calcSpecular(vec3 lightColour, vec3 lightDir,vec3 normal) {
    vec3 fragToCam = normalize(cameraPos - fWorldPos);
    vec3 reflectDir = reflect(-lightDir, normal); //Calculate relflection ray from indicent ray
    float specScaleFactor = pow(max(dot(fragToCam, reflectDir), 0.0f), material.shininess);
    vec3 specularShading = lightColour * specScaleFactor * material.specular;
    return specularShading;
}


vec4 getFinalColour(vec3 ambience, vec3 diffuse, vec3 specular, sampler2D tex) {
    vec3 newColour = (ambience + diffuse + specular) * material.objectColour;
    vec4 colour = texture(tex, fTexelCoord) * vec4(newColour, 1.0f);
    return colour;
}

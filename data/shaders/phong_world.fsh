#version 330 core

//Phong lighting in world space

out vec4 FragColour;

in vec2 fTexelCoord;
in vec3 fNormal;
in vec3 fWorldPos;

uniform vec3 cameraPos;
uniform vec3 lightDiffuse;
uniform vec3 lightAmbient;
uniform vec3 lightPos;

struct Material {
    sampler2D diffuse;
    sampler2D specular;
    sampler2D emission;
    float shininess;
};

uniform Material material;


vec3 calcAmbient(vec3 lightColour);
vec3 calcDiffuse(vec3 lightColour, vec3 lightDir, vec3 normal);
vec3 calcSpecular(vec3 lightColour, vec3 lightDir, vec3 normal);


void main() {
    vec3 normalVec = normalize(fNormal);
    vec3 fragToLight = normalize(lightPos - fWorldPos); //Frag to Light

    vec3 ambientComponent = calcAmbient(lightAmbient);
    vec3 diffuseComponent = calcDiffuse(lightDiffuse, fragToLight, normalVec);
    vec3 specularComponent = calcSpecular(lightDiffuse, fragToLight, normalVec);

    FragColour = vec4(ambientComponent + diffuseComponent + specularComponent, 1.0f) + texture(material.emission, fTexelCoord);
}


vec3 calcAmbient(vec3 lightColour) {
    return lightColour * vec3(texture(material.diffuse, fTexelCoord));
}


vec3 calcDiffuse(vec3 lightColour, vec3 lightDir, vec3 normal) {
    //Calculate how close the light direction is to the normal
    float scaleFactor = max(dot(normal, lightDir), 0.0f);
    vec3 diffuseShading = scaleFactor * lightColour * vec3(texture(material.diffuse, fTexelCoord));
    return diffuseShading;
}


vec3 calcSpecular(vec3 lightColour, vec3 lightDir,vec3 normal) {
    vec3 fragToCam = normalize(cameraPos - fWorldPos);
    vec3 reflectDir = reflect(-lightDir, normal); //Calculate relflection ray from indicent ray
    float specScaleFactor = pow(max(dot(fragToCam, reflectDir), 0.0f), material.shininess);
    vec3 specularShading = lightColour * specScaleFactor * vec3(texture(material.specular, fTexelCoord));
    return specularShading;
}


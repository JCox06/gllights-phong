package uk.co.jcox.gllights

import org.joml.Vector3f

data class Material(
    val diffuseTexture: Int,
    val specularTexture: Int,
    val emissionTexture: Int,
    val shininess: Float,
)

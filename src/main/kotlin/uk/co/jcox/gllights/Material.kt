package uk.co.jcox.gllights

import org.joml.Vector3f

data class Material(
    val objectColour: Vector3f,
    val ambientEffector: Vector3f,
    val diffuseEffector: Vector3f,
    val specularEffector: Vector3f,
    val shininess: Float,
    val textureId: Int,
)

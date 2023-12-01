package uk.co.jcox.gllights

import org.lwjgl.opengl.*


class Renderer {

    fun setup() {
        GL.createCapabilities()
        GLUtil.setupDebugMessageCallback()
        GL11.glEnable(GL11.GL_DEPTH_TEST)
    }


    fun setColour(x: Float, y: Float, z: Float) {
        GL11.glClearColor(x, y, z, 1.0f)
    }

    fun clearBuffers() {
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT or GL11.GL_DEPTH_BUFFER_BIT)
    }


    fun setViewCanvas(screenX: Int, screenY: Int) {
        GL11.glViewport(0, 0, screenX, screenY)
    }

    fun draw(objRep: ObjRepresentative, program: ShaderProgram) {
        program.bind()
        GL30.glBindVertexArray(objRep.geometry)
        GL15.glActiveTexture(GL15.GL_TEXTURE0)
        GL15.glBindTexture(GL15.GL_TEXTURE_2D, objRep.material.textureId)
        program.send("main2D", 0)
        program.send("objectColour", objRep.material.diffuseColour)
        GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, 36)
    }
}
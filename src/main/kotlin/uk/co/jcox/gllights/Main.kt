package uk.co.jcox.gllights

import org.joml.Math
import org.joml.Matrix4f
import org.joml.Vector3f
import org.lwjgl.BufferUtils
import org.lwjgl.glfw.GLFW
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL30
import org.lwjgl.stb.STBImage
import java.io.IOException
import java.nio.IntBuffer
import java.nio.file.Files
import java.nio.file.Paths

private const val CAM_SPEED: Float = 5f
private const val CAM_SENSE: Float = 0.25f

private val window: Window = Window("LWJGL TEST", 1000, 1000)
private val renderer: Renderer = Renderer()
private val mainProgram: ShaderProgram = ShaderProgram("Main")
private val camera: Camera = Camera()
private var cubeObj: ObjRepresentative? = null
private var lightObj: ObjRepresentative? = null

private val lightPos: Vector3f = Vector3f(-5.0f, 2.0f, 5.0f)

private val cubePositions = listOf(
    Vector3f(0.0f, 0.0f, 0.0f),
    Vector3f(2.0f, 2.0f, 2.0f),
    Vector3f(-2.0f, -2.0f, -2.0f),
    Vector3f(1.0f, 3.0f, -2.0f),
    Vector3f(-1.0f, 0.0f, 2.0f),
    Vector3f(0.0f, 0.0f, 4.0f),
    Vector3f(0.0f, 0.0f, -4.0f),
    Vector3f(2.0f, 3.0f, 8.0f),
    Vector3f(0.0f, -3.0f, -3.0f),
    Vector3f(-3.0f, 1.0f, -4.0f),
    Vector3f(15.0f, 0.0f, 0.0f),
    Vector3f(2.0f, 6.0f, 2.0f),
    Vector3f(-2.0f, -2.0f, -2.0f),
    Vector3f(24.0f, 4.0f, -2.0f),
    Vector3f(-1.0f, 0.0f, -15.0f),
    Vector3f(0.0f, 0.0f, 13.0f),
    Vector3f(14.0f, 0.0f, -4.0f),
    Vector3f(-12.0f, 3.0f, 8.0f),
    Vector3f(6.0f, -3.0f, -3.0f),
    Vector3f(2.0f, 1.0f, -12.0f),
    Vector3f(-25.0f, -2.0f, -2.0f),
    Vector3f(24.0f, 4.0f, -25.0f),
    Vector3f(-12.0f, 0.0f, 25.0f),
    Vector3f(30.0f, 2.0f, 13.0f),
    Vector3f(-35.0f, -1.0f, -4.0f),
    Vector3f(40.0f, 3.0f, 8.0f),
    Vector3f(6.0f, -3.0f, -45.0f),
    Vector3f(2.0f, 1.0f, -50.0f),
    Vector3f(0.0f, 0.0f, -10.0f),
    Vector3f(0.0f, 0.0f, -20.0f),
    Vector3f(0.0f, 0.0f, -30.0f),
    Vector3f(0.0f, 0.0f, -40.0f),
    Vector3f(0.0f, 0.0f, -50.0f),
    Vector3f(0.0f, 0.0f, -60.0f),
    Vector3f(0.0f, 0.0f, -70.0f),
    Vector3f(0.0f, 0.0f, -80.0f),
)

fun main() {
    window.createGlContext()
    renderer.setup()

    val vertShadSrc = readFile("data/shaders/phong_world.vsh")
    val fragShadSrc = readFile("data/shaders/phong_world.fsh")

    mainProgram.createProgram(ShaderProgram.ShaderInfo(ShaderProgram.ShaderType.VERTEX, vertShadSrc), ShaderProgram.ShaderInfo(ShaderProgram.ShaderType.FRAGMENT, fragShadSrc))

    val geometry = GeometryBuilder2D.cube()
    var diffuseTexture = loadTexture("data/textures/container_diffuse.png")
    val specularTexture = loadTexture("data/textures/container_specular.png")
    var material = Material(
        diffuseTexture = diffuseTexture,
        specularTexture = specularTexture,
        emissionTexture = 0,
        shininess = 64f,
    )
    cubeObj = ObjRepresentative(geometry, material)

    diffuseTexture = loadTexture("data/textures/lightsource.png")

    material = Material(
        diffuseTexture =  0,
        specularTexture = 0,
        emissionTexture = diffuseTexture,
        shininess = 4f,
    )
    lightObj = ObjRepresentative(geometry, material)


    renderer.setColour(0.0f, 0.0f, 0.0f)

    runRender()

    window.terminate()
}


private fun runRender() {

    window.setMouseFunc {
        if (window.mousePressed(GLFW.GLFW_MOUSE_BUTTON_1)) {
            camera.rotate(window.getxOffset() * CAM_SENSE, window.getyOffset() * CAM_SENSE)
        }
    }

    var lastFrameTime = 0.0
    var deltaTime: Double

    while(! window.shouldClose()) {

        val time = window.timeElapsed
        deltaTime = time - lastFrameTime
        lastFrameTime = time

        render()
        input(deltaTime.toFloat())
        update(deltaTime.toFloat())

        window.runWindowUpdates()
    }
}

private fun render() {
    renderer.clearBuffers()

    val proj = camera.getProjection(window.width / window.height.toFloat())

    renderer.setViewCanvas(window.width, window.height)
    mainProgram.bind()
    mainProgram.send("cameraPos", camera.position)
    mainProgram.send("projMatrix", proj)
    mainProgram.send("camMatrix", camera.lookAt)

    mainProgram.send("directionalLight.lightShading.ambience", Vector3f(0.005f, 0.005f, 0.005f));
    mainProgram.send("directionalLight.lightShading.diffuse", Vector3f(0.5f, 0.5f, 0.5f));
    mainProgram.send("directionalLight.lightShading.specular", Vector3f(1.0f, 1.0f, 1.0f));
    mainProgram.send("directionalLight.direction", Vector3f(0.0f, 1.0f, 0.0f))

    mainProgram.send("pointLights[0].lightShading.ambience", Vector3f(0.1f, 0.1f, 0.1f))
    mainProgram.send("pointLights[0].lightShading.diffuse", Vector3f(1.0f, 1.0f, 1.0f))
    mainProgram.send("pointLights[0].lightShading.specular", Vector3f(1.0f, 1.0f, 1.0f))

    mainProgram.send("pointLights[0].constant", 1.0f)
    mainProgram.send("pointLights[0].linear", 1.0f)
    mainProgram.send("pointLights[0].quadratic", 2.0f)
    mainProgram.send("pointLights[0].worldPosition", lightPos)
    mainProgram.send("pointLights[0].direction", Vector3f(0.0f, 0.0f, 0.0f))
    mainProgram.send("pointLights[0].spotLight", 0)
    mainProgram.send("pointLights[0].on", 1)



    mainProgram.send("pointLights[1].lightShading.ambience", Vector3f(0.1f, 0.1f, 0.1f))
    mainProgram.send("pointLights[1].lightShading.diffuse", Vector3f(1.0f, 0.0f, 0.0f))
    mainProgram.send("pointLights[1].lightShading.specular", Vector3f(1.0f, 1.0f, 1.0f))

    mainProgram.send("pointLights[1].constant", 1.0f)
    mainProgram.send("pointLights[1].linear", 0.22f)
    mainProgram.send("pointLights[1].quadratic", 0.20f)
    mainProgram.send("pointLights[1].worldPosition", camera.position)
    mainProgram.send("pointLights[1].angleCutoff", Math.cos(Math.toRadians(15.0f)))
    mainProgram.send("pointLights[1].angleOuterCutoff", Math.cos(Math.toRadians(20.0f)))
    mainProgram.send("pointLights[0].spotLight", 1)
    mainProgram.send("pointLights[1].direction", camera.facing)
    if (window.isPressed(GLFW.GLFW_KEY_F)) {
        mainProgram.send("pointLights[1].on", 1)
    } else {
        mainProgram.send("pointLights[1].on", 0)
    }

    cubePositions.forEach {
        mainProgram.send("modelMatrix", Matrix4f().translate(it))
        renderer.draw(cubeObj!!, mainProgram)
    }

    mainProgram.send("modelMatrix", Matrix4f().translate(lightPos).scale(0.25f))
    renderer.draw(lightObj!!, mainProgram)
}


private fun input(deltaTime: Float) {

    if (window.isPressed(GLFW.GLFW_KEY_W)) {
        camera.moveForward(CAM_SPEED * deltaTime)
    }
    if (window.isPressed(GLFW.GLFW_KEY_S)) {
        camera.moveForward(-CAM_SPEED * deltaTime)
    }
    if (window.isPressed(GLFW.GLFW_KEY_D)) {
        camera.moveRight(CAM_SPEED * deltaTime)
    }
    if (window.isPressed(GLFW.GLFW_KEY_A)) {
        camera.moveRight(-CAM_SPEED * deltaTime)
    }


    if (window.isPressed(GLFW.GLFW_KEY_UP)) {
        lightPos.add(Vector3f(0.0f, 1.0f, 0.0f).mul(deltaTime))
    }

    if (window.isPressed(GLFW.GLFW_KEY_DOWN)) {
        lightPos.add(Vector3f(0.0f, -1.0f, 0.0f).mul(deltaTime))
    }

}


private fun update(deltaTime: Float) {

    val radius = 1.5f;
    val speed = 1;

    lightPos.x = radius * Math.sin(window.timeElapsed * speed).toFloat()
    lightPos.z = radius * Math.cos(window.timeElapsed * speed).toFloat()
    lightPos.y = radius * Math.sin(window.timeElapsed * speed).toFloat()
}

private fun readFile(file: String): String {
    try {
        val path = Paths.get(file)
        println(path.toAbsolutePath())
        return Files.readString(path)
    } catch (e: IOException) {
        e.printStackTrace()
    }


    return ""
}


private fun loadTexture(pngPath: String): Int {

    STBImage.stbi_set_flip_vertically_on_load(true)

    println("Loading texture: $pngPath")

    val widthBuff: IntBuffer = BufferUtils.createIntBuffer(1)
    val heightBuff: IntBuffer = BufferUtils.createIntBuffer(1)
    val nrChannels: IntBuffer = BufferUtils.createIntBuffer(1)

    val data = STBImage.stbi_load(pngPath, widthBuff, heightBuff, nrChannels, 4)

    val textureId = GL11.glGenTextures()
    GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureId)

    GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, widthBuff.get(), heightBuff.get(), 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, data)
    GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D)
    if (data != null) {
        STBImage.stbi_image_free(data)
    }

    println("Texture loading completed")

    return textureId
}


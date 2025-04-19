package org.example;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.GLFW_CONTEXT_VERSION_MAJOR;
import static org.lwjgl.glfw.GLFW.GLFW_CONTEXT_VERSION_MINOR;
import static org.lwjgl.glfw.GLFW.GLFW_CURSOR;
import static org.lwjgl.glfw.GLFW.GLFW_CURSOR_DISABLED;
import static org.lwjgl.glfw.GLFW.GLFW_FALSE;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_A;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_D;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_LEFT_SHIFT;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_S;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_SPACE;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_W;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_RIGHT;
import static org.lwjgl.glfw.GLFW.GLFW_OPENGL_CORE_PROFILE;
import static org.lwjgl.glfw.GLFW.GLFW_OPENGL_FORWARD_COMPAT;
import static org.lwjgl.glfw.GLFW.GLFW_OPENGL_PROFILE;
import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.GLFW_RESIZABLE;
import static org.lwjgl.glfw.GLFW.GLFW_TRUE;
import static org.lwjgl.glfw.GLFW.GLFW_VISIBLE;
import static org.lwjgl.glfw.GLFW.glfwCreateWindow;
import static org.lwjgl.glfw.GLFW.glfwDefaultWindowHints;
import static org.lwjgl.glfw.GLFW.glfwDestroyWindow;
import static org.lwjgl.glfw.GLFW.glfwGetKey;
import static org.lwjgl.glfw.GLFW.glfwGetMouseButton;
import static org.lwjgl.glfw.GLFW.glfwGetTime;
import static org.lwjgl.glfw.GLFW.glfwInit;
import static org.lwjgl.glfw.GLFW.glfwMakeContextCurrent;
import static org.lwjgl.glfw.GLFW.glfwPollEvents;
import static org.lwjgl.glfw.GLFW.glfwSetCursorPosCallback;
import static org.lwjgl.glfw.GLFW.glfwSetErrorCallback;
import static org.lwjgl.glfw.GLFW.glfwSetInputMode;
import static org.lwjgl.glfw.GLFW.glfwSetKeyCallback;
import static org.lwjgl.glfw.GLFW.glfwSetWindowShouldClose;
import static org.lwjgl.glfw.GLFW.glfwShowWindow;
import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;
import static org.lwjgl.glfw.GLFW.glfwSwapInterval;
import static org.lwjgl.glfw.GLFW.glfwTerminate;
import static org.lwjgl.glfw.GLFW.glfwWindowHint;
import static org.lwjgl.glfw.GLFW.glfwWindowShouldClose;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.GL_TRUE;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.system.MemoryUtil.NULL;

public class App {

    private long window;
    public Camera camera;
    private double lastTime;

    private ShaderProgram shader;
    private Texture texture;
    private Chunk chunk;
    private Mesh chunkMesh;

    // for block-break animations
    private static class BreakingBlock {
        int x, y, z;
        float start;
        BreakingBlock(int x, int y, int z, float s) {
            this.x = x; this.y = y; this.z = z; this.start = s;
        }
    }
    private List<BreakingBlock> breaking = new ArrayList<>();
    private Mesh cubeMesh;
    private float breakAnimDur = 0.5f;

    private float destroyDelay = 0.2f, buildDelay = 0.2f;
    private float destroyTimer = 0, buildTimer = 0;

    public static void main(String[] args) {
        new App().run();
    }

    public void run() {
        init();
        loop();
        // cleanup
        chunkMesh.cleanup();
        cubeMesh.cleanup();
        shader.cleanup();
        glfwFreeCallbacks(window);
        glfwDestroyWindow(window);
        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }

    private void init() {
        GLFWErrorCallback.createPrint(System.err).set();
        if (!glfwInit()) throw new IllegalStateException("Unable to initialize GLFW");

        // Request OpenGL 3.3 core
        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
        glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GL_TRUE);
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);

        window = glfwCreateWindow(800, 600, "Minecraft-like Engine", NULL, NULL);
        if (window == NULL) throw new RuntimeException("Failed to create GLFW window");

        // Input callbacks
        glfwSetKeyCallback(window, (win, key, scancode, action, mods) -> {
            if (key == GLFW_KEY_ESCAPE && action == GLFW_PRESS) {
                glfwSetWindowShouldClose(win, true);
            }
        });
        glfwSetCursorPosCallback(window, (win, xpos, ypos) -> camera.handleMouseMovement(xpos, ypos));
        glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_DISABLED);

        glfwMakeContextCurrent(window);
        glfwSwapInterval(1);
        glfwShowWindow(window);

        GL.createCapabilities();
        glEnable(GL_DEPTH_TEST);
        glClearColor(0.5f, 0.7f, 1.0f, 1.0f);

        // Setup camera and time
        camera = new Camera(8, 20, -20, -30, 0);
        lastTime = glfwGetTime();

        // Load resources
        shader    = new ShaderProgram("shaders/vertex.glsl", "shaders/fragment.glsl");
        texture   = new Texture("textures/texture_atlas.png");
        chunk     = new Chunk();
        chunkMesh = MeshGenerator.generateMesh(chunk);
        cubeMesh  = generateUnitCubeMesh();
    }

    private void loop() {
        Matrix4f projection = new Matrix4f()
                .perspective((float)Math.toRadians(70), 800f/600f, 0.1f, 100f);

        while (!glfwWindowShouldClose(window)) {
            double now = glfwGetTime();
            float  dt  = (float)(now - lastTime);
            lastTime = now;

            destroyTimer -= dt;
            buildTimer   -= dt;
            processInput(dt);

            // Handle break animations
            Iterator<BreakingBlock> iter = breaking.iterator();
            while (iter.hasNext()) {
                BreakingBlock bb = iter.next();
                if ((now - bb.start) >= breakAnimDur) {
                    chunk.destroyBlock(bb.x, bb.y, bb.z);
                    iter.remove();
                    chunkMesh.cleanup();
                    chunkMesh = MeshGenerator.generateMesh(chunk);
                }
            }

            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

            Matrix4f view  = camera.getViewMatrix();
            Matrix4f model = new Matrix4f().identity();

            shader.bind();
            shader.setUniform("projection", projection);
            shader.setUniform("view", view);
            shader.setUniform("model", model);
            shader.setUniform("lightPos",   new Vector3f(10, 10, 10));
            shader.setUniform("viewPos",    new Vector3f(camera.posX, camera.posY, camera.posZ));
            shader.setUniform("lightColor", new Vector3f(1, 1, 1));
            shader.setUniform("objectColor",new Vector3f(1, 1, 1));

            texture.bind();
            shader.setUniform("textureSampler", 0);

            // Render world
            chunkMesh.render();

            // Render break animation cubes
            for (BreakingBlock bb : breaking) {
                float p = (float)((now - bb.start) / breakAnimDur);
                float s = 1f - p;
                Matrix4f m = new Matrix4f()
                        .translate(bb.x, bb.y, bb.z)
                        .translate(0.5f, 0.5f, 0.5f)
                        .scale(s)
                        .translate(-0.5f, -0.5f, -0.5f);
                shader.setUniform("model", m);
                cubeMesh.render();
            }

            shader.unbind();
            glfwSwapBuffers(window);
            glfwPollEvents();
        }
    }

    private void processInput(float dt) {
        // WASD
        if (glfwGetKey(window, GLFW_KEY_W) == GLFW_PRESS) moveForward( dt);
        if (glfwGetKey(window, GLFW_KEY_S) == GLFW_PRESS) moveForward(-dt);
        if (glfwGetKey(window, GLFW_KEY_A) == GLFW_PRESS) strafe(-dt);
        if (glfwGetKey(window, GLFW_KEY_D) == GLFW_PRESS) strafe( dt);

        // Fly up/down
        if (glfwGetKey(window, GLFW_KEY_SPACE)      == GLFW_PRESS) camera.posY += camera.movementSpeed * dt;
        if (glfwGetKey(window, GLFW_KEY_LEFT_SHIFT) == GLFW_PRESS) camera.posY -= camera.movementSpeed * dt;

        // Block actions
        if (glfwGetMouseButton(window, GLFW_MOUSE_BUTTON_LEFT)  == GLFW_PRESS && destroyTimer <= 0) {
            raycastDestroy(); destroyTimer = destroyDelay;
        }
        if (glfwGetMouseButton(window, GLFW_MOUSE_BUTTON_RIGHT) == GLFW_PRESS && buildTimer   <= 0) {
            raycastPlace();   buildTimer   = buildDelay;
        }
    }

    private void moveForward(float amt) {
        float[] f = camera.getForwardVector();
        camera.posX += f[0] * camera.movementSpeed * amt;
        camera.posY += f[1] * camera.movementSpeed * amt;
        camera.posZ += f[2] * camera.movementSpeed * amt;
    }

    private void strafe(float amt) {
        float[] f = camera.getForwardVector();
        float lx = -f[2], lz = f[0];
        camera.posX += lx * camera.movementSpeed * amt;
        camera.posZ += lz * camera.movementSpeed * amt;
    }

    private void raycastDestroy() {
        float maxD = 10f, step = 0.1f;
        float[] f = camera.getForwardVector();
        double now = glfwGetTime();
        float ox = camera.posX, oy = camera.posY, oz = camera.posZ;

        for (float t = 0; t < maxD; t += step) {
            int bx = (int)Math.floor(ox + f[0]*t),
                by = (int)Math.floor(oy + f[1]*t),
                bz = (int)Math.floor(oz + f[2]*t);
            if (chunk.getBlock(bx, by, bz) != BlockType.AIR) {
                breaking.add(new BreakingBlock(bx, by, bz, (float)now));
                break;
            }
        }
    }

    private void raycastPlace() {
        float maxD = 10f, step = 0.1f;
        float[] f = camera.getForwardVector();
        float ox = camera.posX, oy = camera.posY, oz = camera.posZ;
        float lx = ox, ly = oy, lz = oz;
        boolean hit = false;

        for (float t = 0.5f; t < maxD; t += step) {
            float x = ox + f[0]*t, y = oy + f[1]*t, z = oz + f[2]*t;
            int bx = (int)Math.floor(x), by = (int)Math.floor(y), bz = (int)Math.floor(z);
            if (chunk.getBlock(bx, by, bz) != BlockType.AIR) {
                int px = (int)Math.floor(lx), py = (int)Math.floor(ly), pz = (int)Math.floor(lz);
                if (chunk.getBlock(px, py, pz) == BlockType.AIR) {
                    chunk.placeBlock(px, py, pz, BlockType.SPECIAL);
                }
                hit = true;
                break;
            }
            lx = x; ly = y; lz = z;
        }

        if (!hit) {
            int px = (int)Math.floor(lx), py = (int)Math.floor(ly), pz = (int)Math.floor(lz);
            chunk.placeBlock(px, py, pz, BlockType.SPECIAL);
        }
        chunkMesh.cleanup();
        chunkMesh = MeshGenerator.generateMesh(chunk);
    }

    private Mesh generateUnitCubeMesh() {
        float[] V = {
            // front face
             0,0,1, 0,0, 0,0,1,
             1,0,1, 1,0, 0,0,1,
             1,1,1, 1,1, 0,0,1,
             0,1,1, 0,1, 0,0,1,
            // back face
             1,0,0, 0,0, 0,0,-1,
             0,0,0, 1,0, 0,0,-1,
             0,1,0, 1,1, 0,0,-1,
             1,1,0, 0,1, 0,0,-1,
            // left face
             0,0,0, 0,0,-1,0,0,
             0,0,1, 1,0,-1,0,0,
             0,1,1, 1,1,-1,0,0,
             0,1,0, 0,1,-1,0,0,
            // right face
             1,0,1, 0,0, 1,0,0,
             1,0,0, 1,0, 1,0,0,
             1,1,0, 1,1, 1,0,0,
             1,1,1, 0,1, 1,0,0,
            // top face
             0,1,1, 0,0, 0,1,0,
             1,1,1, 1,0, 0,1,0,
             1,1,0, 1,1, 0,1,0,
             0,1,0, 0,1, 0,1,0,
            // bottom face
             1,0,1, 0,0, 0,-1,0,
             0,0,1, 1,0, 0,-1,0,
             0,0,0, 1,1, 0,-1,0,
             1,0,0, 0,1, 0,-1,0
        };
        int[] I = {
             0,1,2, 2,3,0,
             4,5,6, 6,7,4,
             8,9,10,10,11,8,
             12,13,14,14,15,12,
             16,17,18,18,19,16,
             20,21,22,22,23,20
        };
        return new Mesh(V, I);
    }
}

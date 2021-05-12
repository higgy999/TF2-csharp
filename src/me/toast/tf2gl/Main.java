package me.toast.tf2gl;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;

import java.nio.FloatBuffer;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL33.*;
import static org.lwjgl.system.MemoryUtil.*;

public class Main {
    // The window handle
    private long window;
    int width = 800;
    int height = 800;
    GLCapabilities capabilities;

    float[] vertices =
    { //     COORDINATES     /        COLORS      /   TexCoord  //
            -0.5f, 0.0f,  0.5f,     0.83f, 0.70f, 0.44f,	0.0f, 0.0f,
            -0.5f, 0.0f, -0.5f,     0.83f, 0.70f, 0.44f,	5.0f, 0.0f,
            0.5f, 0.0f, -0.5f,     0.83f, 0.70f, 0.44f,	0.0f, 0.0f,
            0.5f, 0.0f,  0.5f,     0.83f, 0.70f, 0.44f,	5.0f, 0.0f,
            0.0f, 0.8f,  0.0f,     0.92f, 0.86f, 0.76f,	2.5f, 5.0f
    };

    int[] indices =  {
            0, 1, 2,
            0, 2, 3,
            0, 1, 4,
            1, 2, 4,
            2, 3, 4,
            3, 0, 4
    };

    public void run() {
        init();
        loop();

        // Free the window callbacks and destroy the window
        glfwFreeCallbacks(window);
        glfwDestroyWindow(window);

        // Terminate GLFW and free the error callback
        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }

    private void init() {
        // Setup an error callback. The default implementation
        // will print the error message in System.err.
        GLFWErrorCallback.createPrint(System.err).set();

        // Initialize GLFW. Most GLFW functions will not work before doing this.
        if ( !glfwInit() )
            throw new IllegalStateException("Unable to initialize GLFW");

        // Configure GLFW
        glfwDefaultWindowHints(); // optional, the current window hints are already the default
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); // the window will stay hidden after creation
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE); // the window will be resizable

        // Create the window
        window = glfwCreateWindow(width, height, "Josh Lyman", NULL, NULL);
        if ( window == NULL )
            throw new RuntimeException("Failed to create the GLFW window");

        // Setup a key callback. It will be called every time a key is pressed, repeated or released.
        glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {
            if ( key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE )
                glfwSetWindowShouldClose(window, true); // We will detect this in the rendering loop
        });

        glfwSetWindowSizeCallback(window, (window, width, height) -> {
            if(capabilities != null) {
                this.width = width;
                this.height = height;
                glViewport(0, 0, width, height);
            }
        });

        // Make the OpenGL context current
        glfwMakeContextCurrent(window);
        // Enable v-sync
        glfwSwapInterval(1);

        // Make the window visible
        glfwShowWindow(window);
    }

    private void loop() {
        // This line is critical for LWJGL's interoperation with GLFW's
        // OpenGL context, or any context that is managed externally.
        // LWJGL detects the context that is current in the current thread,
        // creates the GLCapabilities instance and makes the OpenGL
        // bindings available for use.
        capabilities = GL.createCapabilities();

        glViewport(0, 0, width, height);

        // Set the clear color
        glClearColor(0.0f, 0.0f, 0.0f, 1.0f);

        //Make our shader and tell it the name of the vertex and fragment shaders
        Shader shader = new Shader("triangle");
        //Make our VAO for our triangle
        VAO vao = new VAO();
        //Set it as active
        vao.Bind();

        //Make our VBO and EBO under our VAO
        VBO vbo = new VBO(vertices);
        EBO ebo = new EBO(indices);

        //Tell the VAO what the data means
        vao.LinkAttrib(vbo, 0, 3, GL_FLOAT, 8 * Float.BYTES, 0);
        vao.LinkAttrib(vbo, 1, 3, GL_FLOAT, 8 * Float.BYTES, 3 * Float.BYTES);
        vao.LinkAttrib(vbo, 2, 2, GL_FLOAT, 8 * Float.BYTES, 6 * Float.BYTES);
        //Unbind everything
        vao.Unbind();
        vbo.Unbind();
        ebo.Unbind();

        int uniID = glGetUniformLocation(shader.ID, "scale");

        Texture josh = new Texture("./assets/textures/josh.png", GL_TEXTURE_2D, GL_TEXTURE0, GL_RGBA, GL_UNSIGNED_BYTE);
        josh.TextureUnit(shader, "tex0", 0);

        // Variables that help the rotation of the pyramid
        float rotation = 0.0f;
        double prevTime = glfwGetTime();

        FloatBuffer modelBuffer = BufferUtils.createFloatBuffer(16);
        FloatBuffer viewBuffer = BufferUtils.createFloatBuffer(16);
        FloatBuffer projBuffer = BufferUtils.createFloatBuffer(16);

        // Enables the Depth Buffer
        glEnable(GL_DEPTH_TEST);

        // Run the rendering loop until the user has attempted to close
        // the window or has pressed the ESCAPE key.
        while ( !glfwWindowShouldClose(window) ) {
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer

            //Tell GL we want to use this Shader
            shader.Bind();

            // Simple timer
            double crntTime = glfwGetTime();
            if (crntTime - prevTime >= 1 / 60)
            {
                rotation += 0.5f;
                prevTime = crntTime;
            }

            Matrix4f model = new Matrix4f();
            Matrix4f view = new Matrix4f();
            Matrix4f proj = new Matrix4f();

            model.rotate((float) Math.toRadians(rotation), new Vector3f(0.0f, 1.0f, 0.0f), model);
            view.translate(new Vector3f(0.0f, -0.5f, -2.0f), view);
            proj.perspective((float) Math.toRadians(45.0f), (float) width/height, 0.1f, 100.0f);

            // Outputs the matrices into the Vertex Shader
            int modelLoc = glGetUniformLocation(shader.ID, "model");
            glUniformMatrix4fv(modelLoc, false, model.get(modelBuffer));

            int viewLoc = glGetUniformLocation(shader.ID, "view");
            glUniformMatrix4fv(viewLoc, false, view.get(viewBuffer));

            int projLoc = glGetUniformLocation(shader.ID, "proj");
            glUniformMatrix4fv(projLoc, false, proj.get(projBuffer));

            glUniform1f(uniID, 0.5f);
            josh.Bind();
            //Tell GL we want to use this VAO
            vao.Bind();
            //Draw the triangle
            glDrawElements(GL_TRIANGLES, indices.length, GL_UNSIGNED_INT, 0);

            glfwSwapBuffers(window);
            glfwPollEvents();
        }

        vao.Delete();
        vbo.Delete();
        ebo.Delete();
        josh.Delete();
        shader.Delete();
    }

    public static void main(String[] args) {
        new Main().run();
    }
}

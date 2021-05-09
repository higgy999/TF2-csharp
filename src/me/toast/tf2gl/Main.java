package me.toast.tf2gl;

import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.MemoryStack;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL33.*;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.*;
import static org.lwjgl.stb.STBImage.*;

public class Main {
    // The window handle
    private long window;

    float[] vertices =
    { //     COORDINATES     /        COLORS      /   TexCoord  //
            -0.5f, -0.5f, 0.0f,     1.0f, 0.0f, 0.0f,	0.0f, 0.0f, // Lower left corner
            -0.5f,  0.5f, 0.0f,     0.0f, 1.0f, 0.0f,	0.0f, 1.0f, // Upper left corner
            0.5f,  0.5f, 0.0f,     0.0f, 0.0f, 1.0f,	1.0f, 1.0f, // Upper right corner
            0.5f, -0.5f, 0.0f,     1.0f, 1.0f, 1.0f,	1.0f, 0.0f  // Lower right corner
    };

    int[] indices =  {
            0, 2, 1, // Upper triangle
            0, 3, 2 // Lower triangle
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
        window = glfwCreateWindow(800, 800, "Team Fortress 2 OpenGL & Java", NULL, NULL);
        if ( window == NULL )
            throw new RuntimeException("Failed to create the GLFW window");

        // Setup a key callback. It will be called every time a key is pressed, repeated or released.
        glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {
            if ( key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE )
                glfwSetWindowShouldClose(window, true); // We will detect this in the rendering loop
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
        GL.createCapabilities();

        glViewport(0, 0, 800, 800);

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

        int widthImg, heightImg, numberOfColorChannels;
        IntBuffer w = BufferUtils.createIntBuffer(1);
        IntBuffer h = BufferUtils.createIntBuffer(1);
        IntBuffer comp = BufferUtils.createIntBuffer(1);
        stbi_set_flip_vertically_on_load(true);
        ByteBuffer bytes = stbi_load("./assets/textures/josh.png", w, h, comp, 0);
        widthImg = w.get(0);
        heightImg = h.get(0);
        numberOfColorChannels = comp.get(0);

        int texture = glGenTextures();
        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, texture);

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);

        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, widthImg, heightImg, 0, GL_RGBA, GL_UNSIGNED_BYTE, bytes);
        if (bytes != null) {
            if (numberOfColorChannels == 3) {
                glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, widthImg, heightImg,
                        0, GL_RGB, GL_UNSIGNED_BYTE, bytes);
            } else if (numberOfColorChannels == 4) {
                glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, widthImg, heightImg,
                        0, GL_RGBA, GL_UNSIGNED_BYTE, bytes);
            } else {
                assert false : "Error: (Texture) Unknown number of channels '" + numberOfColorChannels + "'";
            }
        } else {
            assert false : "Error: (Texture) Could not load image '" + "./assets/textures/josh.png" + "'";
        }
        glGenerateMipmap(GL_TEXTURE_2D);

        stbi_image_free(bytes);
        glBindTexture(GL_TEXTURE_2D, 0);

        int texUni = glGetUniformLocation(shader.ID, "tex0");
        shader.Bind();
        glUniform1i(texUni, 0);

        // Run the rendering loop until the user has attempted to close
        // the window or has pressed the ESCAPE key.
        while ( !glfwWindowShouldClose(window) ) {
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer

            //Tell GL we want to use this Shader
            shader.Bind();
            glUniform1f(uniID, 0.5f);
            glBindTexture(GL_TEXTURE_2D, texture);
            //Tell GL we want to use this VAO
            vao.Bind();
            //Draw the triangle
            glDrawElements(GL_TRIANGLES, 6, GL_UNSIGNED_INT, 0);

            glfwSwapBuffers(window);
            glfwPollEvents();
        }

        vao.Delete();
        vbo.Delete();
        ebo.Delete();
        glDeleteTextures(texture);
        shader.Delete();
    }

    public static void main(String[] args) {
        new Main().run();
    }

}

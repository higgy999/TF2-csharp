package me.toast.tf2gl;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;

import static org.lwjgl.opengl.GL30.*;

public class Shader {

    public int ID;

    public Shader(String name) {
        String sourceVert = loadAsString("./assets/shaders/" + name + ".vert");
        String sourceFrag = loadAsString("./assets/shaders/" + name + ".frag");

        //Declare a vertex shader and compile it
        int vertexShader = glCreateShader(GL_VERTEX_SHADER);
        glShaderSource(vertexShader, sourceVert);
        glCompileShader(vertexShader);

        //Declare a fragment shader and compile it
        int fragmentShader = glCreateShader(GL_FRAGMENT_SHADER);
        glShaderSource(fragmentShader, sourceFrag);
        glCompileShader(fragmentShader);

        //Combine the vertex and fragment shaders into one program
        ID = glCreateProgram();
        glAttachShader(ID, vertexShader);
        glAttachShader(ID, fragmentShader);
        //Link the program to GL
        glLinkProgram(ID);

        //Delete the vertex and fragment shaders after they have been attached and linked
        glDeleteShader(vertexShader);
        glDeleteShader(fragmentShader);
    }

    public void Bind() {
        glUseProgram(ID);
    }

    public void Unbind() {
        glUseProgram(0);
    }

    public void Delete() {
        glDeleteProgram(ID);
    }

    public static String loadAsString(String location) {
        StringBuilder result = new StringBuilder();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(location));
            String buffer = "";
            while ((buffer = reader.readLine()) != null) {
                result.append(buffer);
                result.append("\n");
            }
            reader.close();
        } catch (IOException e) { System.err.println(e); }
        return result.toString();
    }
}

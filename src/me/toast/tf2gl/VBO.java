package me.toast.tf2gl;

import static org.lwjgl.opengl.GL30.*;

public class VBO {

    public int ID;

    public VBO(float[] vertices) {
        ID = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, ID);
        //Upload our vertices to the GPU in our VBO
        glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW);
    }

    public void Bind() {
        glBindBuffer(GL_ARRAY_BUFFER, ID);
    }

    public void Unbind() {
        glBindBuffer(GL_ARRAY_BUFFER, 0);
    }

    public void Delete() {
        glDeleteBuffers(ID);
    }
}

package me.toast.tf2gl;

import static org.lwjgl.opengl.GL30.*;

public class EBO {

    public int ID;

    public EBO(int[] indices) {
        ID = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ID);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW);
    }

    void Bind()
    {
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ID);
    }

    void Unbind()
    {
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
    }

    public void Delete() {
        glDeleteBuffers(ID);
    }
}

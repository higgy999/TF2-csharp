package me.toast.tf2gl;

import static org.lwjgl.opengl.GL30.*;

public class VAO {

    public int ID;

    public VAO() {
        ID = glGenVertexArrays();
    }

    public void LinkAttrib(VBO VBO, int layout, int numberOfComponents, int type, int stride, int offset) {
        VBO.Bind();
        //Tell the VAO what the data is
        glVertexAttribPointer(layout, numberOfComponents, type, false, stride, offset);
        glEnableVertexAttribArray(layout); //?????
        VBO.Unbind();
    }

    public void Bind() {
        glBindVertexArray(ID);
    }

    public void Unbind() {
        glBindVertexArray(0);
    }

    public void Delete() {
        glDeleteVertexArrays(ID);
    }
}

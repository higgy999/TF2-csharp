package me.toast.tf2gl;

import org.lwjgl.BufferUtils;

import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.stb.STBImage.*;

public class Texture {

    public int ID;
    public final int width, height, numChannels;
    public final int type;

    public Texture(String file, int textureType, int slot, int format, int pixelType) {
        //Make some temp buffers to get the width, height, and numOfChannels of the texture
        IntBuffer w = BufferUtils.createIntBuffer(1);
        IntBuffer h = BufferUtils.createIntBuffer(1);
        IntBuffer comp = BufferUtils.createIntBuffer(1);

        //Tell stbi to load the image the way OpenGL will read it
        stbi_set_flip_vertically_on_load(true);
        //Actually load it into a buffer
        ByteBuffer bytes = stbi_load(file, w, h, comp, 0);

        //Set the variables of the Texture from temp buffers
        width = w.get(0);
        height = h.get(0);
        numChannels = comp.get(0);

        type = textureType;

        //Make ID for texture
        ID = glGenTextures();
        //Set the active slot we are working in and bind the texture for use
        glActiveTexture(slot);
        Bind();

        //Set Parameters
        glTexParameteri(type, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(type, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glTexParameteri(type, GL_TEXTURE_WRAP_S, GL_REPEAT);
        glTexParameteri(type, GL_TEXTURE_WRAP_T, GL_REPEAT);

        //Upload the buffer to the GPU
        glTexImage2D(type, 0, GL_RGBA, width, height, 0, format, pixelType, bytes);
        //Make some mipmaps for future use //TODO: Set Mipmap Parameters
        glGenerateMipmap(type);

        //Delete the buffer and unbind the texture
        stbi_image_free(bytes);
        Unbind();
    }

    public void TextureUnit(Shader shader, String uniform, int unit) {
        // Gets the location of the uniform
        int texUni = glGetUniformLocation(shader.ID, uniform);
        // Shader needs to be activated before changing the value of a uniform
        shader.Bind();
        // Sets the value of the uniform
        glUniform1i(texUni, unit);
    }

    //Make our texture the current one we are working on
    public void Bind() {
        glBindTexture(type, ID);
    }

    //Set the current texture to 0 so we don't accidentally write to a texture
    public void Unbind() {
        glBindTexture(type, 0);
    }

    //Delete the texture at cleanup
    public void Delete() {
        glDeleteTextures(ID);
    }
}

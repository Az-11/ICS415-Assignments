package org.example;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.stb.STBImage.*;
import static org.lwjgl.system.MemoryStack.stackPush;

public class Texture {
    private final int id;
    private final int width;
    private final int height;

    public Texture(String filePath) {
        ByteBuffer imageBuffer;
        try {
            imageBuffer = Utils.ioResourceToByteBuffer(filePath, 8 * 1024);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load texture file " + filePath, e);
        }
        try (MemoryStack stack = stackPush()) {
            IntBuffer w = stack.mallocInt(1);
            IntBuffer h = stack.mallocInt(1);
            IntBuffer channels = stack.mallocInt(1);

            ByteBuffer image = stbi_load_from_memory(imageBuffer, w, h, channels, 4);
            if (image == null) {
                throw new RuntimeException("Failed to load texture file " + filePath + "\n" + stbi_failure_reason());
            }
            width = w.get(0);
            height = h.get(0);

            id = glGenTextures();
            glBindTexture(GL_TEXTURE_2D, id);
            glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, image);

            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

            stbi_image_free(image);
        }
    }

    public void bind() {
        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, id);
    }

    public int getId() {
        return id;
    }

    public int getWidth() {
        return width;
    }
    
    public int getHeight() {
        return height;
    }
}

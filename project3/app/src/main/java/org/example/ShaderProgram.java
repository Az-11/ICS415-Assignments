package org.example;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import static org.lwjgl.opengl.GL20.*;

public class ShaderProgram {
    private final int programId;

    public ShaderProgram(String vertexPath, String fragmentPath) {
        int vertexShaderId = loadShader(vertexPath, GL_VERTEX_SHADER);
        int fragmentShaderId = loadShader(fragmentPath, GL_FRAGMENT_SHADER);
        programId = glCreateProgram();
        if (programId == 0) {
            throw new RuntimeException("Could not create Shader");
        }
        glAttachShader(programId, vertexShaderId);
        glAttachShader(programId, fragmentShaderId);
        glLinkProgram(programId);
        if (glGetProgrami(programId, GL_LINK_STATUS) == 0) {
            throw new RuntimeException("Error linking Shader code: " + glGetProgramInfoLog(programId, 1024));
        }
        glValidateProgram(programId);
        // Detach and delete shaders after linking.
        glDetachShader(programId, vertexShaderId);
        glDetachShader(programId, fragmentShaderId);
        glDeleteShader(vertexShaderId);
        glDeleteShader(fragmentShaderId);
    }

    private int loadShader(String filePath, int type) {
        String shaderCode;
        try {
            shaderCode = new String(Files.readAllBytes(Paths.get(getClass().getClassLoader().getResource(filePath).toURI())));
        } catch (Exception e) {
            throw new RuntimeException("Could not read shader file: " + filePath, e);
        }
        int shaderId = glCreateShader(type);
        glShaderSource(shaderId, shaderCode);
        glCompileShader(shaderId);
        if (glGetShaderi(shaderId, GL_COMPILE_STATUS) == 0) {
            throw new RuntimeException("Error compiling Shader code: " + glGetShaderInfoLog(shaderId, 1024));
        }
        return shaderId;
    }
    
    public void bind() {
        glUseProgram(programId);
    }
    
    public void unbind() {
        glUseProgram(0);
    }
    
    public void cleanup() {
        unbind();
        if (programId != 0) {
            glDeleteProgram(programId);
        }
    }
    
    // Helper methods to set uniforms.
    public void setUniform(String name, Matrix4f value) {
        int location = glGetUniformLocation(programId, name);
        try (var stack = org.lwjgl.system.MemoryStack.stackPush()) {
            float[] mat = new float[16];
            value.get(mat);
            glUniformMatrix4fv(location, false, mat);
        }
    }
    
    public void setUniform(String name, Vector3f value) {
        int location = glGetUniformLocation(programId, name);
        glUniform3f(location, value.x, value.y, value.z);
    }
    
    public void setUniform(String name, int value) {
        int location = glGetUniformLocation(programId, name);
        glUniform1i(location, value);
    }
}

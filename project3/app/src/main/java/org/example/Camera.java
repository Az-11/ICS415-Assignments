package org.example;

import org.joml.Matrix4f;
import org.joml.Vector3f;

public class Camera {
    public float posX, posY, posZ;
    public float pitch, yaw;
    public float movementSpeed = 3.0f;
    public float mouseSensitivity = 0.1f;
    
    private boolean firstMouse = true;
    private double lastMouseX, lastMouseY;

    public Camera(float posX, float posY, float posZ, float pitch, float yaw) {
        this.posX = posX;
        this.posY = posY;
        this.posZ = posZ;
        this.pitch = pitch;
        this.yaw = yaw;
    }

    // Returns the forward vector based on current pitch and yaw.
    public float[] getForwardVector() {
        float cosPitch = (float) Math.cos(Math.toRadians(pitch));
        float sinPitch = (float) Math.sin(Math.toRadians(pitch));
        float cosYaw = (float) Math.cos(Math.toRadians(yaw));
        float sinYaw = (float) Math.sin(Math.toRadians(yaw));
        return new float[] {
            cosPitch * sinYaw,
            sinPitch,
            cosPitch * cosYaw
        };
    }

    // Computes and returns the view matrix using JOML.
    public Matrix4f getViewMatrix() {
        Vector3f eye = new Vector3f(posX, posY, posZ);
        float[] forward = getForwardVector();
        Vector3f center = new Vector3f(
            posX + forward[0],
            posY + forward[1],
            posZ + forward[2]
        );
        Vector3f up = new Vector3f(0, 1, 0);
        return new Matrix4f().lookAt(eye, center, up);
    }
    
    public void handleMouseMovement(double xpos, double ypos) {
        if (firstMouse) {
            lastMouseX = xpos;
            lastMouseY = ypos;
            firstMouse = false;
        }
        double offsetX = xpos - lastMouseX;
        double offsetY = lastMouseY - ypos; // reversed Y axis
        lastMouseX = xpos;
        lastMouseY = ypos;
        offsetX *= mouseSensitivity;
        offsetY *= mouseSensitivity;
        yaw += offsetX;
        pitch += offsetY;
        if (pitch > 89.0f) {
            pitch = 89.0f;
        }
        if (pitch < -89.0f) {
            pitch = -89.0f;
        }
    }
}

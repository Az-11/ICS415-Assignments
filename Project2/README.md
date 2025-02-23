# Minimal Ray Tracer in GLSL

This project implements a minimal ray tracer using GLSL. The shader renders a scene with three large, brightly colored spheres arranged side by side along with many smaller spheres scattered across a flat gray plane. It features basic lighting (ambient, diffuse, and specular) and supports a single reflection bounce for reflective surfaces.

## Overview

- **Scene Composition:**  
  - **3 Big Spheres:** Placed side by side (left, center, right) with bright, clear colors.  
  - **Many Small Spheres:** Randomly scattered on the ground plane.
  - **Ground Plane:** A simple gray plane at `y = 0`.

- **Lighting:**  
  A directional light provides ambient, diffuse, and specular shading.

- **Reflections:**  
  Each object supports one reflection bounce to simulate mirror-like surfaces without heavy computation.

- **Camera:**  
  The camera is positioned and aimed to capture the scene similarly to your reference image. It is fully adjustable.

- **Platform:**  
  The shader is written in GLSL and is designed to run on platforms like [Shadertoy](https://www.shadertoy.com/) or any WebGL environment.

## File Structure

- `shader.frag`: The main fragment shader source file containing all the ray tracing code.
- `README.md`: This file, which documents the project and explains how to run and customize the shader.

## How It Works

1. **Scene Setup:**  
   The `buildScene()` function creates the scene by:
   - Adding **3 large spheres** (the “big balls”) with user-specified positions, radii, colors, and reflectivity.
   - Scattering many small spheres across the plane with random positions, colors, and reflectivity values.

2. **Ray Intersection:**  
   - **Ray-Plane Intersection:** The function `intersectPlane()` computes the intersection with the ground plane.
   - **Ray-Sphere Intersection:** The function `intersectSphere()` computes the intersection with each sphere.

3. **Lighting:**  
   The `computeLighting()` function applies a simple lighting model:
   - **Ambient Lighting:** Provides a base color.
   - **Diffuse Lighting:** Simulates light scattered by the surface.
   - **Specular Highlights:** Adds shine based on the reflection of the light direction.

4. **Reflection:**  
   A single reflection bounce is computed by reflecting the primary ray at the hit point. The resulting color is mixed with the local shading based on the sphere’s reflectivity.

5. **Camera Setup:**  
   The camera is defined with a position (`camPos`), a target (`camTarget`), and an adjustable field of view (FOV). This can be modified to match your desired view.

6. **Main Shader Routine:**  
   The `mainImage()` function calculates normalized screen coordinates, sets up the camera, and computes the final pixel color by tracing rays and applying gamma correction.

## Getting Started

### Running on Shadertoy

1. Visit [Shadertoy](https://www.shadertoy.com/).
2. Click on **New Shader**.
3. Copy the contents of `shader.frag` (provided below) into the editor.
4. Click **Run** to see the rendered scene.

### Running Locally in VSCode

1. Install [Visual Studio Code](https://code.visualstudio.com/).
2. Create a new file named `shader.frag` and paste the GLSL code.
3. Install a GLSL extension (e.g., "GLSL Linter") for syntax highlighting and error checking.
4. Use a WebGL viewer like [glslViewer](https://github.com/patriciogonzalezvivo/glslViewer) or integrate with your own WebGL setup to preview the shader.

## Customization

- **Big Sphere Colors:**  
  Adjust the color values in the `addSphere()` calls within `buildScene()` to make the big spheres brighter and more vivid. For example:  
  ```glsl
  addSphere(vec3(-2.0, 1.0, 5.0), 1.0, vec3(1.0, 1.0, 0.2), 0.3);  // Left big sphere: bright yellow
  addSphere(vec3( 0.0, 1.0, 5.0), 1.0, vec3(1.0),          0.6);  // Middle big sphere: bright silver
  addSphere(vec3( 2.0, 1.0, 5.0), 1.0, vec3(0.8, 0.6, 1.0), 0.4);  // Right big sphere: bright purple

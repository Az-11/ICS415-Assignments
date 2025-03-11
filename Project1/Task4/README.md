# RayTracer4

RayTracer4 is a simple ray tracing application written in Java. It implements basic ray tracing functionality, including object rendering, lighting, and shadows.

## Features
- Renders 3D objects using ray tracing.
- Supports Phong lighting model.
- Can load `.obj` files (e.g., Stanford Bunny).
- Outputs an image file with the rendered scene.

## Requirements
To run this project, you need:
- Java Development Kit (JDK 17 or higher).
- Apache Maven (for building the project).
- JavaFX (for rendering the scene).
- A compatible `.obj` file (such as the Stanford Bunny).

## Installation
Follow these steps to set up and run the project:

### 1. Clone the Repository
```sh
git clone https://github.com/Az-11/ICS415-Assignments.git
cd RayTracer4
```
 
### 2. Build the Project
```sh
mvn clean install
```

### 3. Run the Application
```sh
mvn exec:java -Dexec.mainClass="com.yourpackage.Main"
```

### 4. Load the Stanford Bunny Model
1. Download the Stanford Bunny `.obj` file from [this link](http://graphics.stanford.edu/~mdfisher/Data/Meshes/bunny.obj).
2. Place the file inside the `models` directory in the project root.
3. Run the program and it will automatically load the model.

## Expected Output
Upon running, the program will generate an image file containing the rendered scene with objects, lighting, and shadows.

## Troubleshooting
- If Maven fails, ensure you have installed all required dependencies.
- If the program does not render correctly, make sure JavaFX is properly set up.
- If an `bunny.obj` file does not load, check the file path and ensure it is correctly referenced in the code.

## License
This project is licensed under the MIT License. Feel free to use and modify it.

---




# FinalRayTracing

FinalRayTracing is final task (the project) ray tracing application written in **Java**. It renders 3D objects using ray tracing techniques and outputs an image of the rendered scene.

## Features
- Renders a 3D scene using ray tracing.
- Supports lighting, shadows, and reflections.
- Generates high-quality images with anti-aliasing.
- Uses a camera with adjustable field of view and depth of field.

## Requirements
To run this project, you need:
- **Java Development Kit (JDK) 11+**
- **Maven** (for dependency management and building the project)

## Installation and Setup
Follow these steps to set up and run the project:

### 1. Clone the Repository
```sh
git clone https://github.com/Az-11/ICS415-Assignments.git
cd FinalRayTracing
```

### 2. Build the Project using Maven
```sh
mvn clean compile
```

### 3. Run the Application
```sh
mvn exec:java -Dexec.mainClass="FinalRayTracing"
```

### 4. View the Output
- The rendered image will be saved as `FinalRayTracing.png` in the project directory.
- Open the image using any image viewer to see the result.

## Expected Output
Upon running, the program will generate an image file `FinalRayTracing.png` containing a ray-traced 3D scene.

## Troubleshooting
- **Compilation Errors**: Ensure you have Java 11+ installed and that Maven is properly set up.
- **Maven Not Found**: Install Maven from [Apache Maven](https://maven.apache.org/install.html).
- **No Output Image**: Check for any exceptions in the console and verify file permissions.

## License
This project is licensed under the MIT License. Feel free to use and modify it.




# Ray Tracing with RayTracer2 in Java

This project implements an advanced ray tracing algorithm in Java to render a scene with spheres and lighting effects. The generated image is displayed in a window and saved as `RayTracer2.png`.

## Prerequisites

Ensure you have the following installed:

- Java Development Kit (JDK) 8 or later
- Any Java IDE (optional) or command-line tools

## How to Run the Code

### Clone the Repository

```sh
git clone https://github.com/Az-11/ICS415-Assignments.git
cd raytracer2-java
```

### Compile the Java Code

```sh
javac -d . RayTracer2.java
```

### Run the Program

```sh
java Task2.RayTracer2
```

### View the Output

- A window will open displaying the rendered scene.
- The output image will also be saved as `RayTracer2.png` in the project directory.

## Code Explanation

- The program defines a `RayTracer2` class that implements ray tracing with lighting effects.
- It supports ambient, point, and directional lighting.
- The output is displayed in a GUI window using `JFrame` and `JLabel`.
- The final image is generated using `BufferedImage`.

## Troubleshooting

- If the window does not appear, ensure your system allows Java GUI applications.
- If Java is not recognized, check your installation and ensure it's added to the system path.

## License

This project is open-source and available under the MIT License.




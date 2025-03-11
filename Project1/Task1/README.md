# Ray Tracing in Java

This task implements a simple ray tracing algorithm in Java to render a scene containing spheres. The generated image is saved as `RayTracing.png`.

## Prerequisites

Before running the program, ensure you have the following installed:

- Java Development Kit (JDK) 8 or later
- Any Java IDE (optional) or command-line tools

## How to Run the Code

### Clone the Repository

```sh
git clone https://github.com/Az-11/ICS415-Assignments.git
cd raytracing-java
```

### Compile the Java Code

```sh
javac -d . RayTracing.java
```

### Run the Program

```sh
java Task1.RayTracing
```

### View the Output

After execution, an image file named `RayTracing.png` will be generated in the same directory. Open this image to see the rendered spheres.

## Code Explanation

- The program defines a `RayTracing` class that renders a scene containing spheres.
- It computes ray-sphere intersections and assigns colors accordingly.
- The output is saved as a PNG image using `BufferedImage` and `ImageIO`.

## Troubleshooting

- If you encounter errors, ensure that Java is installed correctly and added to the system path.
- Check for file permission issues when writing the output image.

## License

This project is open-source and available under the MIT License.

---


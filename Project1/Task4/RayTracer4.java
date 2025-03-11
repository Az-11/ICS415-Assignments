package Task4;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;

public class RayTracer4 {

    // 3D vector class with basic operations and a cross-product.
    static class Vector {
        double x, y, z;

        Vector(double x, double y, double z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }

        Vector add(Vector v) {
            return new Vector(x + v.x, y + v.y, z + v.z);
        }

        Vector subtract(Vector v) {
            return new Vector(x - v.x, y - v.y, z - v.z);
        }

        Vector multiply(double scalar) {
            return new Vector(x * scalar, y * scalar, z * scalar);
        }

        double dot(Vector v) {
            return x * v.x + y * v.y + z * v.z;
        }

        // Cross product useful for triangle intersections.
        Vector cross(Vector v) {
            return new Vector(
                    y * v.z - z * v.y,
                    z * v.x - x * v.z,
                    x * v.y - y * v.x);
        }

        double length() {
            return Math.sqrt(x * x + y * y + z * z);
        }

        Vector normalize() {
            double len = length();
            if (len == 0) return new Vector(0, 0, 0);
            return new Vector(x / len, y / len, z / len);
        }
    }

    // A sphere in the scene.
    static class Sphere {
        Vector center;
        double radius;
        Color color;
        double specular;
        double reflective;

        Sphere(Vector center, double radius, Color color, double specular, double reflective) {
            this.center = center;
            this.radius = radius;
            this.color = color;
            this.specular = specular;
            this.reflective = reflective;
        }
    }

    // A triangle defined by three vertices.
    static class Triangle {
        Vector v0, v1, v2;
        Color color;
        double specular;
        double reflective;

        Triangle(Vector v0, Vector v1, Vector v2, Color color, double specular, double reflective) {
            this.v0 = v0;
            this.v1 = v1;
            this.v2 = v2;
            this.color = color;
            this.specular = specular;
            this.reflective = reflective;
        }

        // Möller–Trumbore ray-triangle intersection.
        // Returns t (distance along the ray) or POSITIVE_INFINITY if no valid intersection.
        double intersect(Vector O, Vector D) {
            final double EPSILON = 1e-6;
            Vector edge1 = v1.subtract(v0);
            Vector edge2 = v2.subtract(v0);
            Vector h = D.cross(edge2);
            double a = edge1.dot(h);
            if (a > -EPSILON && a < EPSILON) { // Ray is parallel to triangle.
                return Double.POSITIVE_INFINITY;
            }
            double f = 1.0 / a;
            Vector s = O.subtract(v0);
            double u = f * s.dot(h);
            if (u < 0.0 || u > 1.0) {
                return Double.POSITIVE_INFINITY;
            }
            Vector q = s.cross(edge1);
            double v = f * D.dot(q);
            if (v < 0.0 || u + v > 1.0) {
                return Double.POSITIVE_INFINITY;
            }
            double t = f * edge2.dot(q);
            if (t > EPSILON) { // Ray intersection.
                return t;
            } else { // Intersection behind the ray origin.
                return Double.POSITIVE_INFINITY;
            }
        }
    }

    // Light source.
    static class Light {
        String type; // "ambient", "point", or "directional"
        double intensity;
        Vector position;  // For point lights.
        Vector direction; // For directional lights.

        Light(String type, double intensity, Vector position, Vector direction) {
            this.type = type;
            this.intensity = intensity;
            this.position = position;
            this.direction = direction;
        }
    }

    // The scene now contains spheres, triangles (e.g. for the bunny), and lights.
    static class Scene {
        List<Sphere> spheres = new ArrayList<>();
        List<Triangle> triangles = new ArrayList<>();
        List<Light> lights = new ArrayList<>();
    }

    // Compute lighting at point P with normal N and view direction V.
    static double computeLighting(Vector P, Vector N, Vector V, double s, Scene scene) {
        double intensity = 0.0;
        for (Light light : scene.lights) {
            Vector L;
            if (light.type.equals("ambient")) {
                intensity += light.intensity;
                continue;
            } else if (light.type.equals("point")) {
                L = light.position.subtract(P);
            } else { // directional
                L = light.direction;
            }
            // Diffuse
            double nDotL = N.dot(L);
            if (nDotL > 0) {
                intensity += light.intensity * nDotL / (N.length() * L.length());
            }
            // Specular
            if (s != -1) {
                Vector R = N.multiply(2 * N.dot(L)).subtract(L);
                double rDotV = R.dot(V);
                if (rDotV > 0) {
                    intensity += light.intensity * Math.pow(rDotV / (R.length() * V.length()), s);
                }
            }
        }
        return intensity;
    }

    // Ray-sphere intersection.
    // Returns an array containing two t values (distances along the ray) or null if no intersection.
    static double[] intersectRaySphere(Vector O, Vector D, Sphere sphere) {
        Vector CO = O.subtract(sphere.center);
        double a = D.dot(D);
        double b = 2 * CO.dot(D);
        double c = CO.dot(CO) - sphere.radius * sphere.radius;
        double discriminant = b * b - 4 * a * c;
        if (discriminant < 0) return null;
        double sqrtDisc = Math.sqrt(discriminant);
        double t1 = (-b + sqrtDisc) / (2 * a);
        double t2 = (-b - sqrtDisc) / (2 * a);
        return new double[]{t1, t2};
    }

    // The ray tracing function which now considers both spheres and triangles.
    static Color traceRay(Vector O, Vector D, double tMin, double tMax, Scene scene, int depth) {
        if (depth <= 0) return Color.BLACK;

        double closestT = Double.POSITIVE_INFINITY;
        Sphere closestSphere = null;
        Triangle closestTriangle = null;

        // Check intersections with spheres.
        for (Sphere sphere : scene.spheres) {
            double[] tValues = intersectRaySphere(O, D, sphere);
            if (tValues == null) continue;
            if (tValues[0] >= tMin && tValues[0] <= tMax && tValues[0] < closestT) {
                closestT = tValues[0];
                closestSphere = sphere;
                closestTriangle = null; // Clear triangle hit if sphere is closer.
            }
            if (tValues[1] >= tMin && tValues[1] <= tMax && tValues[1] < closestT) {
                closestT = tValues[1];
                closestSphere = sphere;
                closestTriangle = null;
            }
        }

        // Check intersections with triangles.
        for (Triangle triangle : scene.triangles) {
            double t = triangle.intersect(O, D);
            if (t >= tMin && t <= tMax && t < closestT) {
                closestT = t;
                closestTriangle = triangle;
                closestSphere = null;
            }
        }

        // If nothing was hit, return background color.
        if (closestSphere == null && closestTriangle == null) {
            return Color.BLACK;
        }

        // Compute the intersection point.
        Vector P = O.add(D.multiply(closestT));
        Vector N;          // Normal at the intersection.
        Color objectColor; // Base color of the object.
        double specular;
        double reflective;

        if (closestSphere != null) {
            N = P.subtract(closestSphere.center).normalize();
            objectColor = closestSphere.color;
            specular = closestSphere.specular;
            reflective = closestSphere.reflective;
        } else {
            // For the triangle, compute the face normal.
            N = (closestTriangle.v1.subtract(closestTriangle.v0))
                    .cross(closestTriangle.v2.subtract(closestTriangle.v0)).normalize();
            objectColor = closestTriangle.color;
            specular = closestTriangle.specular;
            reflective = closestTriangle.reflective;
        }

        Vector V = D.multiply(-1); // View direction.
        double lighting = computeLighting(P, N, V, specular, scene);
        int r = (int) (objectColor.getRed() * lighting);
        int g = (int) (objectColor.getGreen() * lighting);
        int b = (int) (objectColor.getBlue() * lighting);
        Color localColor = new Color(Math.min(r, 255), Math.min(g, 255), Math.min(b, 255));

        // If the object is not reflective, return the local color.
        if (reflective <= 0) {
            return localColor;
        }

        // Compute the reflection direction: R = D - 2*(D dot N)*N.
        Vector R = N.multiply(2 * N.dot(V)).subtract(V);
        Color reflectedColor = traceRay(P, R, 0.001, Double.POSITIVE_INFINITY, scene, depth - 1);

        int finalR = (int) (localColor.getRed() * (1 - reflective) + reflectedColor.getRed() * reflective);
        int finalG = (int) (localColor.getGreen() * (1 - reflective) + reflectedColor.getGreen() * reflective);
        int finalB = (int) (localColor.getBlue() * (1 - reflective) + reflectedColor.getBlue() * reflective);

        return new Color(Math.min(finalR, 255), Math.min(finalG, 255), Math.min(finalB, 255));
    }

    // Load an OBJ file and return a list of Triangle objects.
    // This simple loader expects lines starting with "v" for vertices and "f" for faces (triangles).
    static List<Triangle> loadOBJ(String filename, Color color, double specular, double reflective) {
        List<Vector> vertices = new ArrayList<>();
        List<Triangle> triangles = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                // Process vertex lines.
                if (line.startsWith("v ")) {
                    String[] tokens = line.split("\\s+");
                    double x = Double.parseDouble(tokens[1]);
                    double y = Double.parseDouble(tokens[2]);
                    double z = Double.parseDouble(tokens[3]);
                    vertices.add(new Vector(x, y, z));
                }
                // Process face lines. Assumes triangular faces.
                else if (line.startsWith("f ")) {
                    String[] tokens = line.split("\\s+");
                    if (tokens.length < 4) continue; // Not a valid face.
                    int[] indices = new int[3];
                    for (int i = 0; i < 3; i++) {
                        // Faces can be in the format "f v/vt/vn". We only need the first number.
                        String[] parts = tokens[i + 1].split("/");
                        indices[i] = Integer.parseInt(parts[0]) - 1; // OBJ indices start at 1.
                    }
                    Triangle tri = new Triangle(vertices.get(indices[0]),
                            vertices.get(indices[1]),
                            vertices.get(indices[2]),
                            color, specular, reflective);
                    triangles.add(tri);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return triangles;
    }

    // A helper method to transform (scale and translate) a list of triangles.
    static void transformTriangles(List<Triangle> triangles, double scale, Vector translate) {
        for (Triangle tri : triangles) {
            tri.v0 = tri.v0.multiply(scale).add(translate);
            tri.v1 = tri.v1.multiply(scale).add(translate);
            tri.v2 = tri.v2.multiply(scale).add(translate);
        }
    }

    public static void main(String[] args) {
        int width = 800, height = 800;
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        Scene scene = new Scene();
        
        // Add some spheres to the scene.
        scene.spheres.add(new Sphere(new Vector(0, -1, 3), 1, Color.RED, 500, 0.2));
        scene.spheres.add(new Sphere(new Vector(2, 0, 4), 1, Color.BLUE, 500, 0.3));
        scene.spheres.add(new Sphere(new Vector(-2, 0, 4), 1, Color.GREEN, 10, 0.4));
        // A large floor sphere.
        scene.spheres.add(new Sphere(new Vector(0, -5001, 0), 5000, Color.YELLOW, 1000, 0.5));

        // Add lights.
        scene.lights.add(new Light("ambient", 0.2, null, null));
        scene.lights.add(new Light("point", 0.6, new Vector(2, 1, 0), null));
        scene.lights.add(new Light("directional", 0.2, null, new Vector(1, 4, 4)));

        // Load the Stanford Bunny from an OBJ file.
        // The bunny will be given a light gray color, moderate specular highlight, and some reflectivity.
        List<Triangle> bunnyTriangles = loadOBJ("Project1/Task4/models/bunny.obj", Color.LIGHT_GRAY, 10, 0.2);
        // Transform the bunny so that it is visible in the scene.
        // Here we scale it by 4 and translate it upward and into the scene.
        transformTriangles(bunnyTriangles, 4, new Vector(0, 1, 5));
        // Add the bunny triangles to the scene.
        scene.triangles.addAll(bunnyTriangles);

        Vector camera = new Vector(0, 0, 0);

        // For each pixel, shoot a ray into the scene.
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                // Map pixel coordinate to viewport (with simple normalization).
                double nx = (x - width / 2.0) / width;
                double ny = -(y - height / 2.0) / height;
                Vector direction = new Vector(nx, ny, 1).normalize();
                Color color = traceRay(camera, direction, 1, Double.POSITIVE_INFINITY, scene, 3);
                image.setRGB(x, y, color.getRGB());
            }
        }

        // Display the rendered image.
        JFrame frame = new JFrame("Ray Tracer");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(width, height);
        frame.add(new JLabel(new ImageIcon(image)));
        frame.setVisible(true);
    }
}

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;

public class RayTracer2 {
    static class Vector {
        double x, y, z;

        Vector(double x, double y, double z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }

        Vector subtract(Vector v) {
            return new Vector(x - v.x, y - v.y, z - v.z);
        }

        Vector add(Vector v) {
            return new Vector(x + v.x, y + v.y, z + v.z);
        }

        Vector multiply(double scalar) {
            return new Vector(x * scalar, y * scalar, z * scalar);
        }

        double dot(Vector v) {
            return x * v.x + y * v.y + z * v.z;
        }

        double length() {
            return Math.sqrt(x * x + y * y + z * z);
        }

        Vector normalize() {
            double length = length();
            return new Vector(x / length, y / length, z / length);
        }
    }

    static class Sphere {
        Vector center;
        double radius;
        Color color;
        double specular;

        Sphere(Vector center, double radius, Color color, double specular) {
            this.center = center;
            this.radius = radius;
            this.color = color;
            this.specular = specular;
        }
    }

    static class Light {
        String type;
        double intensity;
        Vector position;
        Vector direction;

        Light(String type, double intensity, Vector position, Vector direction) {
            this.type = type;
            this.intensity = intensity;
            this.position = position;
            this.direction = direction;
        }
    }

    static class Scene {
        List<Sphere> spheres = new ArrayList<>();
        List<Light> lights = new ArrayList<>();
    }

    static double computeLighting(Vector P, Vector N, Vector V, double s, Scene scene) {
        double intensity = 0.0;

        for (Light light : scene.lights) {
            Vector L;
            if (light.type.equals("ambient")) {
                intensity += light.intensity;
                continue;
            } else if (light.type.equals("point")) {
                L = light.position.subtract(P);
            } else {
                L = light.direction;
            }

            double nDotL = N.dot(L);
            if (nDotL > 0) {
                intensity += light.intensity * nDotL / (N.length() * L.length());
            }

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

    static double[] intersectRaySphere(Vector O, Vector D, Sphere sphere) {
        Vector CO = O.subtract(sphere.center);
        double a = D.dot(D);
        double b = 2 * CO.dot(D);
        double c = CO.dot(CO) - sphere.radius * sphere.radius;

        double discriminant = b * b - 4 * a * c;
        if (discriminant < 0) return null;

        double t1 = (-b + Math.sqrt(discriminant)) / (2 * a);
        double t2 = (-b - Math.sqrt(discriminant)) / (2 * a);
        return new double[]{t1, t2};
    }

    static Color traceRay(Vector O, Vector D, double tMin, double tMax, Scene scene) {
        double closestT = Double.POSITIVE_INFINITY;
        Sphere closestSphere = null;

        for (Sphere sphere : scene.spheres) {
            double[] tValues = intersectRaySphere(O, D, sphere);
            if (tValues == null) continue;

            if (tValues[0] >= tMin && tValues[0] <= tMax && tValues[0] < closestT) {
                closestT = tValues[0];
                closestSphere = sphere;
            }
            if (tValues[1] >= tMin && tValues[1] <= tMax && tValues[1] < closestT) {
                closestT = tValues[1];
                closestSphere = sphere;
            }
        }

        if (closestSphere == null) {
            return Color.WHITE; // Background color
        }

        Vector P = O.add(D.multiply(closestT));
        Vector N = P.subtract(closestSphere.center).normalize();
        Vector V = D.multiply(-1);

        double lighting = computeLighting(P, N, V, closestSphere.specular, scene);
        int r = (int) (closestSphere.color.getRed() * lighting);
        int g = (int) (closestSphere.color.getGreen() * lighting);
        int b = (int) (closestSphere.color.getBlue() * lighting);

        return new Color(Math.min(r, 255), Math.min(g, 255), Math.min(b, 255));
    }

    public static void main(String[] args) {
        int width = 800, height = 800;
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        Scene scene = new Scene();
        scene.spheres.add(new Sphere(new Vector(0, -1, 3), 1, Color.RED, 500));
        scene.spheres.add(new Sphere(new Vector(2, 0, 4), 1, Color.BLUE, 500));
        scene.spheres.add(new Sphere(new Vector(-2, 0, 4), 1, Color.GREEN, 10));
        scene.spheres.add(new Sphere(new Vector(0, -5001, 0), 5000, Color.YELLOW, 1000));

        scene.lights.add(new Light("ambient", 0.2, null, null));
        scene.lights.add(new Light("point", 0.6, new Vector(2, 1, 0), null));
        scene.lights.add(new Light("directional", 0.2, null, new Vector(1, 4, 4)));

        Vector camera = new Vector(0, 0, 0);

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                double nx = (x - width / 2.0) / width;
                double ny = -(y - height / 2.0) / height;
                Vector direction = new Vector(nx, ny, 1).normalize();

                Color color = traceRay(camera, direction, 1, Double.POSITIVE_INFINITY, scene);
                image.setRGB(x, y, color.getRGB());
            }
        }

        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(width, height);
        frame.add(new JLabel(new ImageIcon(image)));
        frame.setVisible(true);
    }
}

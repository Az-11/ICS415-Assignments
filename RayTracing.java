import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class RayTracing {

    static final int WIDTH = 800;
    static final int HEIGHT = 800;
    static final double VIEWPORT_SIZE = 1;
    static final double PROJECTION_PLANE_D = 1;
    static final int[] BACKGROUND_COLOR = {255, 255, 255};

    static class Sphere {
        double[] center;
        double radius;
        int[] color;

        Sphere(double[] center, double radius, int[] color) {
            this.center = center;
            this.radius = radius;
            this.color = color;
        }
    }

    static Sphere[] scene = {
        new Sphere(new double[]{0, -1, 3}, 1, new int[]{255, 0, 0}), // Red
        new Sphere(new double[]{2, 0, 4}, 1, new int[]{0, 0, 255}), // Blue
        new Sphere(new double[]{-2, 0, 4}, 1, new int[]{0, 255, 0})  // Green
    };

    public static void main(String[] args) {
        BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);

        for (int x = -WIDTH / 2; x < WIDTH / 2; x++) {
            for (int y = -HEIGHT / 2; y < HEIGHT / 2; y++) {
                double[] direction = canvasToViewport(x, y);
                int[] color = traceRay(new double[]{0, 0, 0}, direction, 1, Double.POSITIVE_INFINITY);
                image.setRGB(x + WIDTH / 2, HEIGHT / 2 - y - 1, (color[0] << 16) | (color[1] << 8) | color[2]);
            }
        }

        try {
            File output = new File("ray_tracing_output.png");
            ImageIO.write(image, "png", output);
            System.out.println("Image saved as ray_tracing_output.png");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static double[] canvasToViewport(int x, int y) {
        return new double[]{
            x * VIEWPORT_SIZE / WIDTH,
            y * VIEWPORT_SIZE / HEIGHT,
            PROJECTION_PLANE_D
        };
    }

    static int[] traceRay(double[] origin, double[] direction, double tMin, double tMax) {
        double closestT = Double.POSITIVE_INFINITY;
        Sphere closestSphere = null;

        for (Sphere sphere : scene) {
            double[] tValues = intersectRaySphere(origin, direction, sphere);

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
            return BACKGROUND_COLOR;
        }
        return closestSphere.color;
    }

    static double[] intersectRaySphere(double[] origin, double[] direction, Sphere sphere) {
        double[] CO = subtract(origin, sphere.center);
        double a = dot(direction, direction);
        double b = 2 * dot(CO, direction);
        double c = dot(CO, CO) - sphere.radius * sphere.radius;
        double discriminant = b * b - 4 * a * c;

        if (discriminant < 0) {
            return new double[]{Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY};
        }

        double t1 = (-b + Math.sqrt(discriminant)) / (2 * a);
        double t2 = (-b - Math.sqrt(discriminant)) / (2 * a);
        return new double[]{t1, t2};
    }

    static double dot(double[] a, double[] b) {
        return a[0] * b[0] + a[1] * b[1] + a[2] * b[2];
    }

    static double[] subtract(double[] a, double[] b) {
        return new double[]{a[0] - b[0], a[1] - b[1], a[2] - b[2]};
    }
}

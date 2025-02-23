/*
    Single-Bounce Ray Tracer: 3 Big Spheres + Many Small
    ----------------------------------------------------
    - Plane at y=0 (gray).
    - 3 large spheres side by side (x=-2, x=0, x=2).
    - ~80 random small spheres scattered around the plane.
    - Simple directional lighting + 1 reflection bounce.
    - Camera from above/behind to view them like your reference.

    Should run at reasonable speed in Shadertoy.
*/

#ifdef GL_ES
precision mediump float;
#endif



// ------------------- Scene Limits -------------------
#define MAX_SPHERES  128

// ------------------- Data Structures ----------------
struct Sphere {
    vec3 center;
    float radius;
    vec3 color;
    float reflectivity;
};

Sphere g_spheres[MAX_SPHERES];
int   g_sphereCount = 0;

void addSphere(vec3 c, float r, vec3 col, float refl)
{
    if(g_sphereCount >= MAX_SPHERES) return;
    g_spheres[g_sphereCount].center       = c;
    g_spheres[g_sphereCount].radius       = r;
    g_spheres[g_sphereCount].color        = col;
    g_spheres[g_sphereCount].reflectivity = refl;
    g_sphereCount++;
}

// ------------------- Random Utility -----------------
// For scattering small spheres
float hash12(vec2 p) {
    float n = dot(p, vec2(127.1, 311.7));
    return fract(sin(n)*43758.5453123);
}

// ------------------- Scene Setup --------------------
void buildScene()
{
    g_sphereCount = 0;

    // 1) 3 BIG SPHERES side by side
    //    x = -2, 0, +2; all at z=5, y=1, radius=1
    //    Colors & reflectivity can be adjusted as you like
    //    Example: left=yellowish, middle=silver, right=purple-ish
    addSphere(vec3(-2.0, 1.0, 5.0), 1.0, vec3(1.0, 1.0, 0.0), 0.1);
    addSphere(vec3( 0.0, 1.0, 5.0), 1.0, vec3(0.95),          0.2);  // Middle big sphere (silver)
    addSphere(vec3( 2.0, 1.0, 5.0), 1.0, vec3(0.7, 0.6, 1.0), 0.2);  // Right big sphere

    // 2) Many small spheres scattered on plane
    //    We'll do ~80 for "a lot of balls."
    for(int i=0; i<80; i++)
    {
        // Random x in [-8..8], z in [0..12]
        float rx = mix(-8.0, 8.0, hash12(vec2(float(i), 12.3)));
        float rz = mix( 0.0,12.0, hash12(vec2(float(i), 98.7)));
        // random color
        float rc = hash12(vec2(float(i), 11.1));
        float gc = hash12(vec2(float(i), 22.2));
        float bc = hash12(vec2(float(i), 33.3));
        // random reflect
        float refl = 0.1 + 0.4*hash12(vec2(float(i), 44.4));

        
        addSphere(vec3(rx, 0.3, rz), 0.3, vec3(rc, gc, bc), refl);
    }
}


float intersectPlane(vec3 ro, vec3 rd)
{
    // plane at y=0
    if(abs(rd.y) < 1e-6) return -1.0;
    float t = -ro.y / rd.y;
    return (t > 0.001) ? t : -1.0;
}


float intersectSphere(vec3 ro, vec3 rd, Sphere s)
{
    vec3 oc = ro - s.center;
    float b = dot(oc, rd);
    float c = dot(oc, oc) - s.radius*s.radius;
    float h = b*b - c;
    if(h < 0.0) return -1.0;
    h = sqrt(h);
    float t1 = -b - h;
    float t2 = -b + h;
    if(t1>0.001 && t2>0.001) return min(t1,t2);
    if(t2>0.001) return t2;
    return -1.0;
}

// ----------------- Scene Intersection ---------------
bool  g_hitPlane;
float g_tHit;
vec3  g_hitColor;
vec3  g_hitNormal;
float g_hitReflect;

float traceRay(vec3 ro, vec3 rd)
{
    float closestT = 1e9;
    int   hitID    = -2;  // -1=plane, >=0 sphere index, -2=none
    g_hitPlane     = false;

    // 1) Plane
    float tPlane = intersectPlane(ro, rd);
    if(tPlane>0.0 && tPlane<closestT) {
        closestT = tPlane;
        hitID    = -1;
        g_hitPlane = true;
    }

    // 2) Spheres
    for(int i=0; i<MAX_SPHERES; i++)
    {
        if(i >= g_sphereCount) break;
        float tSph = intersectSphere(ro, rd, g_spheres[i]);
        if(tSph>0.0 && tSph<closestT) {
            closestT = tSph;
            hitID    = i;
            g_hitPlane = false;
        }
    }

    if(hitID == -2) return -1.0; // no hit

    // Evaluate material
    g_tHit = closestT;
    vec3 pos = ro + rd*closestT;
    if(hitID == -1) {
        // plane
        g_hitNormal  = vec3(0.0, 1.0, 0.0);
        g_hitColor   = vec3(0.5); // gray
        g_hitReflect = 0.2;       // mild reflection
    } else {
        // sphere
        Sphere s = g_spheres[hitID];
        g_hitNormal  = normalize(pos - s.center);
        g_hitColor   = s.color;
        g_hitReflect = s.reflectivity;
    }

    return closestT;
}

// ------------------- Simple Lighting -----------------
vec3 computeLighting(vec3 pos, vec3 normal, vec3 rd)
{
    // Basic directional light
    vec3 lightDir = normalize(vec3(0.3, 0.7, 0.5));
    // Ambient
    vec3 col = 0.2 * g_hitColor;
    // Diffuse
    float diff = max(dot(normal, lightDir), 0.0);
    col += diff * 0.8 * g_hitColor;
    // Specular
    vec3 r = reflect(-lightDir, normal);
    float spec = pow(max(dot(r, -rd), 0.0), 32.0);
    col += vec3(1.0)*spec*0.4;

    return col;
}

// ------------------- Ray Color (1 bounce) ------------
vec3 getRayColor(vec3 ro, vec3 rd)
{
    float t = traceRay(ro, rd);
    if(t<0.0) {
        // background color (light lavender)
        return vec3(0.9, 0.9, 1.0);
    }
    // local shading
    vec3 hitPos = ro + rd*t;
    vec3 baseCol = computeLighting(hitPos, g_hitNormal, rd);

    // reflection bounce
    float refl = g_hitReflect;
    if(refl>0.001) {
        vec3 rdir = reflect(rd, g_hitNormal);
        vec3 newPos = hitPos + g_hitNormal*0.001; // offset to avoid self-intersect
        float t2 = traceRay(newPos, rdir);
        if(t2>0.0) {
            vec3 hitPos2 = newPos + rdir*t2;
            vec3 col2 = computeLighting(hitPos2, g_hitNormal, rdir);
            baseCol = mix(baseCol, col2, refl);
        } else {
            // reflection sees background
            vec3 bg = vec3(0.9, 0.9, 1.0);
            baseCol = mix(baseCol, bg, refl);
        }
    }

    return baseCol;
}

// ------------------- Main Shader ---------------------
void mainImage(out vec4 fragColor, in vec2 fragCoord)
{
    // Build the scene once per pixel
    buildScene();

    // Normalized coords
    vec2 uv = (fragCoord / iResolution.xy)*2.0 - 1.0;
    uv.x *= iResolution.x / iResolution.y;

    // Camera
    // We place it behind & above, looking at z=5 where big spheres are.
    vec3 camPos = vec3(-4.0, 1.0, 3.0);
    vec3 camTarget = vec3(0.0, 1.0,  5.0);
    vec3 camDir    = normalize(camTarget - camPos);
    vec3 right     = normalize(cross(camDir, vec3(0.0, 1.0, 0.0)));
    vec3 up        = cross(right, camDir);

    float fov      = 1.0;
    vec3 rd        = normalize(uv.x*right + uv.y*up + fov*camDir);

    // Ray color
    vec3 col = getRayColor(camPos, rd);

    // Gamma correction
    col = pow(col, vec3(0.4545)); // ~ gamma 2.2

    fragColor = vec4(col, 1.0);
}

package org.example;

import java.util.ArrayList;
import java.util.List;

public class MeshGenerator {
    public static Mesh generateMesh(Chunk chunk) {
        List<Float> vertices = new ArrayList<>();
        List<Integer> indices = new ArrayList<>();
        int indexOffset = 0;

        for (int x = 0; x < Chunk.WIDTH;  x++) {
            for (int y = 0; y < Chunk.HEIGHT; y++) {
                for (int z = 0; z < Chunk.DEPTH;  z++) {
                    BlockType block = chunk.getBlock(x,y,z);
                    if (block == BlockType.AIR) continue;

                    float uOff = block.getUOffset();

                    // for each face: if neighbor is AIR, add a face
                    // front
                    if (chunk.getBlock(x, y, z+1) == BlockType.AIR) {
                        addFace(vertices, indices,
                            new float[]{x,   y,   z+1},
                            new float[]{x+1, y,   z+1},
                            new float[]{x+1, y+1, z+1},
                            new float[]{x,   y+1, z+1},
                            0,0,1, indexOffset, uOff);
                        indexOffset += 4;
                    }
                    // back
                    if (chunk.getBlock(x, y, z-1) == BlockType.AIR) {
                        addFace(vertices, indices,
                            new float[]{x+1,y,   z},
                            new float[]{x,  y,   z},
                            new float[]{x,  y+1, z},
                            new float[]{x+1,y+1, z},
                            0,0,-1, indexOffset, uOff);
                        indexOffset += 4;
                    }
                    // left
                    if (chunk.getBlock(x-1, y, z) == BlockType.AIR) {
                        addFace(vertices, indices,
                            new float[]{x, y,   z},
                            new float[]{x, y,   z+1},
                            new float[]{x, y+1, z+1},
                            new float[]{x, y+1, z},
                            -1,0,0, indexOffset, uOff);
                        indexOffset += 4;
                    }
                    // right
                    if (chunk.getBlock(x+1, y, z) == BlockType.AIR) {
                        addFace(vertices, indices,
                            new float[]{x+1,y,   z+1},
                            new float[]{x+1,y,   z},
                            new float[]{x+1,y+1, z},
                            new float[]{x+1,y+1, z+1},
                            1,0,0, indexOffset, uOff);
                        indexOffset += 4;
                    }
                    // top
                    if (chunk.getBlock(x, y+1, z) == BlockType.AIR) {
                        addFace(vertices, indices,
                            new float[]{x,   y+1, z},
                            new float[]{x+1, y+1, z},
                            new float[]{x+1, y+1, z+1},
                            new float[]{x,   y+1, z+1},
                            0,1,0, indexOffset, uOff);
                        indexOffset += 4;
                    }
                    // bottom
                    if (chunk.getBlock(x, y-1, z) == BlockType.AIR) {
                        addFace(vertices, indices,
                            new float[]{x+1,y,   z},
                            new float[]{x,  y,   z},
                            new float[]{x,  y,   z+1},
                            new float[]{x+1,y,   z+1},
                            0,-1,0, indexOffset, uOff);
                        indexOffset += 4;
                    }
                }
            }
        }

        float[] vArray = new float[vertices.size()];
        int[] iArray   = new int[indices.size()];
        for (int i=0; i<vertices.size(); i++) vArray[i] = vertices.get(i);
        for (int i=0; i<indices.size();  i++) iArray[i] = indices.get(i);

        return new Mesh(vArray, iArray);
    }

    private static void addFace(
        List<Float> vs, List<Integer> is,
        float[] v0, float[] v1, float[] v2, float[] v3,
        float nx, float ny, float nz,
        int off, float uOff
    ) {
        addVertex(vs, v0, uOff+0f,  0f,  nx, ny, nz);
        addVertex(vs, v1, uOff+0.25f,0f,  nx, ny, nz);
        addVertex(vs, v2, uOff+0.25f,1f,  nx, ny, nz);
        addVertex(vs, v3, uOff+0f,  1f,  nx, ny, nz);

        is.add(off);   is.add(off+1); is.add(off+2);
        is.add(off+2); is.add(off+3); is.add(off);
    }

    private static void addVertex(
        List<Float> vs, float[] pos,
        float u, float v,
        float nx, float ny, float nz
    ) {
        vs.add(pos[0]); vs.add(pos[1]); vs.add(pos[2]);
        vs.add(u);      vs.add(v);
        vs.add(nx);     vs.add(ny);     vs.add(nz);
    }
}

package org.example;

public class Chunk {
    public static final int WIDTH  = 100;
    public static final int HEIGHT = 100;
    public static final int DEPTH  = 100;
    
    private BlockType[][][] blocks = new BlockType[WIDTH][HEIGHT][DEPTH];

    /**
     * Flat world: one layer of GRASS at y==0 across the whole chunk,
     * all other cells are AIR.
     */
    public Chunk() {
        for (int x = 0; x < WIDTH;  x++) {
            for (int z = 0; z < DEPTH; z++) {
                for (int y = 0; y < HEIGHT; y++) {
                    if (y == 0) {
                        blocks[x][y][z] = BlockType.GRASS;
                    } else {
                        blocks[x][y][z] = BlockType.AIR;
                    }
                }
            }
        }
    }

    public BlockType getBlock(int x, int y, int z) {
        if (x < 0 || x >= WIDTH ||
            y < 0 || y >= HEIGHT ||
            z < 0 || z >= DEPTH)
            return BlockType.AIR;
        return blocks[x][y][z];
    }

    public void destroyBlock(int x, int y, int z) {
        if (x>=0 && x<WIDTH && y>=0 && y<HEIGHT && z>=0 && z<DEPTH) {
            blocks[x][y][z] = BlockType.AIR;
        }
    }

    public void placeBlock(int x, int y, int z, BlockType type) {
        if (x>=0 && x<WIDTH && y>=0 && y<HEIGHT && z>=0 && z<DEPTH) {
            blocks[x][y][z] = type;
        }
    }
}

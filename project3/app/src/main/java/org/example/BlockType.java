package org.example;

public enum BlockType {
    AIR,
    STONE,
    DIRT,
    GRASS,
    WOOD,
    LEAVES,
    WATER,
    SPECIAL;

    public float getUOffset() {
        return switch (this) {
            case STONE   -> 0.0f;
            case DIRT    -> 0.125f;
            case GRASS   -> 0.25f;
            case WOOD    -> 0.375f;
            case LEAVES  -> 0.5f;
            case WATER   -> 0.625f;
            case SPECIAL -> 0.75f;
            default      -> 0.0f;
        };
    }
}

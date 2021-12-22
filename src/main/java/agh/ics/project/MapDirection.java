package agh.ics.project;

import java.util.Random;

public enum MapDirection {
    NORTH,
    SOUTH,
    WEST,
    EAST,
    NORTHWEST,
    NORTHEAST,
    SOUTHWEST,
    SOUTHEAST;

    public String toString() {
        return switch (this) {
            case NORTH -> "północ";
            case SOUTH -> "południe";
            case EAST -> "wschód";
            case WEST -> "zachód";
            case NORTHWEST -> "północny-zachód";
            case NORTHEAST -> "północny-wschód";
            case SOUTHWEST -> "południowy-zachód";
            case SOUTHEAST -> "południowy-wschód";
        };
    }

    public MapDirection next(){
        return switch (this) {
            case NORTH -> NORTHEAST;
            case SOUTH -> SOUTHWEST;
            case EAST -> SOUTHEAST;
            case WEST -> NORTHWEST;
            case NORTHWEST -> NORTH;
            case NORTHEAST -> EAST;
            case SOUTHWEST -> WEST;
            case SOUTHEAST -> SOUTH;
        };
    }

    public MapDirection previous(){
        return switch (this) {
            case NORTH -> NORTHWEST;
            case SOUTH -> SOUTHEAST;
            case EAST -> NORTHEAST;
            case WEST -> SOUTHWEST;
            case NORTHWEST -> WEST;
            case NORTHEAST -> NORTH;
            case SOUTHWEST -> SOUTH;
            case SOUTHEAST -> EAST;
        };
    }

    public MapDirection doublePrevious(){
        return switch (this) {
            case NORTH -> WEST;
            case SOUTH -> EAST;
            case EAST -> NORTH;
            case WEST -> SOUTH;
            case NORTHWEST -> SOUTHWEST;
            case NORTHEAST -> NORTHWEST;
            case SOUTHWEST -> SOUTHEAST;
            case SOUTHEAST -> NORTHEAST;
        };
    }

    public MapDirection doubleNext(){
        return switch (this) {
            case NORTH -> EAST;
            case SOUTH -> WEST;
            case EAST -> SOUTH;
            case WEST -> NORTH;
            case NORTHWEST -> NORTHEAST;
            case NORTHEAST -> SOUTHEAST;
            case SOUTHWEST -> NORTHWEST;
            case SOUTHEAST -> SOUTHWEST;
        };
    }

    public Vector2d toUnitVector(){
        return switch (this) {
            case NORTH -> new Vector2d(0,1);
            case SOUTH -> new Vector2d(0,-1);
            case EAST -> new Vector2d(1,0);
            case WEST -> new Vector2d(-1,0);
            case NORTHWEST -> new Vector2d(-1,1);
            case NORTHEAST -> new Vector2d(1,1);
            case SOUTHWEST -> new Vector2d(-1,-1);
            case SOUTHEAST -> new Vector2d(1,-1);
        };
    }

    public MapDirection getRandom(){
        Random generator = new Random();
        return switch (generator.nextInt(8)){
            case 0 -> MapDirection.NORTH;
            case 1 -> MapDirection.NORTHEAST;
            case 2 -> MapDirection.EAST;
            case 3 -> MapDirection.SOUTHEAST;
            case 4 -> MapDirection.SOUTH;
            case 5 -> MapDirection.SOUTHWEST;
            case 6 -> MapDirection.WEST;
            case 7 -> MapDirection.NORTHWEST;
            default -> throw new IllegalStateException("Unexpected value: " + generator.nextInt(8)); //why is it required, I have no idea
        };
    }

}


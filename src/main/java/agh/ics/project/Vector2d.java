package agh.ics.project;

import java.util.Objects;

import static java.lang.Math.max;
import static java.lang.Math.min;

public class Vector2d {
    public final int x;
    public final int y;

    public Vector2d(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public String toString() {
        return "(" + this.x + "," + this.y + ")";
    }

    public boolean precedes(Vector2d other) {
        return this.x <= other.x && this.y <= other.y;
    }

    public boolean follows(Vector2d other) {
        return this.x >= other.x && this.y >= other.y;
    }

    public Vector2d upperRight(Vector2d other) {
        int xn = max(this.x, other.x);
        int yn = max(this.y, other.y);
        return new Vector2d(xn, yn);
    }

    public Vector2d lowerLeft(Vector2d other) {
        int xn = min(this.x, other.x);
        int yn = min(this.y, other.y);
        return new Vector2d(xn, yn);
    }

    public Vector2d add(Vector2d other) {
        return new Vector2d((this.x + other.x), (this.y + other.y));
    }

    public Vector2d subtract(Vector2d other) {
        return new Vector2d((this.x - other.x), (this.y - other.y));
    }

    public boolean equals(Object other) {
        if (this == other)
            return true;
        if (other == null)
            return false;
        if (getClass() != other.getClass())
            return false;
        Vector2d temp = (Vector2d) other;
        return this.x == temp.x && this.y == temp.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    public Vector2d opposite(){
        return new Vector2d(-this.x, -this.y);
    }
}


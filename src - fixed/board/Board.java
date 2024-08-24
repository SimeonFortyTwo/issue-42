package board;

import java.util.HashSet;
import java.util.Set;

public class Board {
    private final Color[][] squares;
    private final Set<Long> stars;

    public Board(int width, int height) {
        this.squares = new Color[width][height];
        this.stars = new HashSet<>();
    }

    public void set(Color color, int x, int y) {
        this.squares[x][y] = color;
    }

    public void fill(Color color, int fromX, int fromY, int toX, int toY) {
        if (fromX > toX) {
            int from = fromX;
            fromX = toX;
            toX = from;
        }
        if (fromY > toY) {
            int from = fromY;
            fromY = toY;
            toY = from;
        }
        for (int x = fromX; x <= toX; x++) {
            for (int y = fromY; y <= toY; y++) {
                this.set(color, x, y);
            }
        }
    }

    public Color get(int x, int y) {
        return this.squares[x][y];
    }

    public void addStar(int x, int y) {
        this.stars.add(Pos.from(x, y));
    }

    public Set<Long> copyStars() {
        return new HashSet<>(this.stars);
    }
}

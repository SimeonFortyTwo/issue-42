package board;

public class Pos {
    public static long from(int x, int y) {
        return x | (((long) y) << 32);
    }

    public static int x(long pos) {
        return (int) pos;
    }

    public static int y(long pos) {
        return (int) (pos >> 32);
    }
}
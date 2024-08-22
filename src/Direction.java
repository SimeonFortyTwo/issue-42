public enum Direction {
    UP,
    RIGHT,
    DOWN,
    LEFT;

    public static Direction rotate(Direction direction, boolean left) {
        if (left) {
            return switch (direction) {
                case UP -> LEFT;
                case RIGHT -> UP;
                case DOWN -> RIGHT;
                case LEFT -> DOWN;
            };
        } else {
            return switch (direction) {
                case UP -> RIGHT;
                case RIGHT -> DOWN;
                case DOWN -> LEFT;
                case LEFT -> UP;
            };
        }
    }
}

import board.Board;
import board.Color;
import board.Direction;
import board.Game;
import program.Command;
import program.InstructionByte;
import program.ProgramArray;
import program.Result;

import java.util.List;
import java.util.Locale;

public class Main {
    private static final long START_TIME = System.currentTimeMillis();

    public static void main(String[] args) {
        int startX = 1;
        int startY = 1;
        Direction startDirection = Direction.UP;

        Command[] availableCommands = new Command[]{
                Command.MOVE,
                Command.TURN,
                //Command.PAINT,
                Command.FUNCTION
        };
        int[] functionSizes = new int[]{
                6
        };
        boolean conditions = true;

        int depth = 1000;
        int stack = 1000;

        int printInterval = 250_000;

        int boardWidth = 8;
        int boardHeight = 15;
        Board board = new Board(boardWidth, boardHeight);

        // configure the board colors and stars here
        configureBoard(board);

        run(new Game(board, startX, startY, startDirection), availableCommands, conditions, functionSizes, depth, stack, printInterval);
    }

    private static void configureBoard(Board board) {
        board.fill(Color.RED, 1, 1, 1, 6);
        board.set(Color.BLUE, 1, 7);
        board.set(Color.RED, 1, 8);
        board.set(Color.RED, 2, 8);
        board.set(Color.RED, 2, 9);
        board.set(Color.RED, 3, 9);
        board.set(Color.RED, 3, 10);
        board.set(Color.RED, 4, 10);
        board.set(Color.RED, 4, 11);
        board.set(Color.RED, 5, 11);
        board.set(Color.RED, 5, 12);
        board.set(Color.RED, 6, 12);
        board.set(Color.RED, 6, 13);

        board.addStar(6, 13);
    }

    private static void configurePossibleBoard(Board board) {
        board.fill(Color.RED, 1, 1, 1, 7);
        board.set(Color.GREEN, 1, 8);
        board.set(Color.BLUE, 2, 8);
        board.set(Color.GREEN, 2, 9);
        board.set(Color.BLUE, 3, 9);
        board.set(Color.GREEN, 3, 10);
        board.set(Color.BLUE, 4, 10);
        board.set(Color.GREEN, 4, 11);
        board.set(Color.BLUE, 5, 11);
        board.set(Color.GREEN, 5, 12);
        board.set(Color.BLUE, 6, 12);
        board.set(Color.GREEN, 6, 13);

        board.addStar(6, 13);
    }

    private static void run(Game game, Command[] commands, boolean conditions, int[] functions, int depth, int stack, int printInterval) {
        print("Creating all possible programs...");

        List<byte[][]> programs = ProgramArray.createAllPossiblePrograms(game, commands, conditions, functions);
        print("Created %s possible programs", programs.size());

        for (int i = 0; i < programs.size(); i++) {
            if (i % printInterval == 0) {
                print("Tried %s/%s programs", i, programs.size());
            }
            byte[][] program = programs.get(i);
            if (ProgramArray.execute(program, game, depth, stack) == Result.WON) {
                print("Won after %s tries with the following program: %s", i, program);
                for (byte instruction : program[0]) {
                    print("%s (%s) - %s", InstructionByte.getCommand(instruction), InstructionByte.getCondition(instruction), InstructionByte.getFunction(instruction));
                }
                return;
            }
        }
        print("No winning instructions possible!");
    }

    private static void print(String msg, Object... args) {
        System.out.println(
                "[" + getTime() + "] " + String.format(Locale.ROOT, msg, args)
        );
    }

    private static String getTime() {
        int total = (int) ((System.currentTimeMillis() - START_TIME) / 1000);
        int s = total % 60;
        String seconds = (s < 10 ? "0" : "") + s;
        int m = (total / 60) % 60;
        String minutes = (m < 10 ? "0" : "") + m;
        int hours = total / 3600;
        return hours > 0 ?
                String.format("%s:%s:%s", hours, minutes, seconds) :
                String.format("%s:%s", minutes, seconds);
    }
}
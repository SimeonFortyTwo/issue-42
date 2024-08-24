package program;

import board.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ProgramArray {
    public static Result execute(byte[][] program, Game game, int depth, int stackSize) {
        Board board = game.board();
        int x = game.x();
        int y = game.y();
        Direction direction = game.direction();

        Color field = board.get(x, y);
        Set<Long> stars = board.copyStars();

        if (stars.isEmpty()) {
            throw new IllegalArgumentException("Board has no stars!");
        }

        Stack stack = new Stack();
        int functionIndex = 0;
        byte[] function = program[functionIndex];
        int line = 0;

        for (int cycles = 0; cycles < depth; cycles++) {
            if (line >= function.length) {
                if (stack.isEmpty()) {
                    // reached the end of the program
                    return Result.PROGRAM_END;
                }
                // return to last function from the stack
                Stack.Entry entry = stack.retrieve();
                function = program[entry.function()];
                line = entry.line() + 1;
                continue;
            }

            byte instruction = function[line];

            Color condition = InstructionByte.getCondition(instruction);
            if (condition != null && condition != field) {
                line++;
                continue;
            }
            Command command = InstructionByte.getCommand(instruction);
            switch (command) {
                case Command.MOVE -> {
                    switch (direction) {
                        case Direction.UP -> y++;
                        case Direction.DOWN -> y--;
                        case Direction.RIGHT -> x++;
                        case Direction.LEFT -> x--;
                    }

                    field = board.get(x, y);

                    if (field == null) {
                        // left viable squares
                        return Result.LOST;
                    }
                    // collect any stars on the new square
                    if (stars.remove(Pos.from(x, y)) && stars.isEmpty()) {
                        // collected the final star
                        return Result.WON;
                    }
                }
                case Command.TURN -> {
                    direction = Direction.rotate(direction, InstructionByte.isLeft(instruction));
                }
                case Command.PAINT -> {
                    board.set(InstructionByte.getColor(instruction), x, y);
                }
                case Command.FUNCTION -> {
                    int newFunction = InstructionByte.getFunction(instruction);

                    if (newFunction >= program.length) {
                        return Result.ERROR;
                    }

                    if (functionIndex == newFunction && condition == null) {
                        // clear the stack if function is looping infinitely
                        stack.clear();
                    }

                    stack.add(functionIndex, line);
                    if (stackSize < stack.size()) {
                        // exceeded stack size
                        return Result.STACKOVERFLOW;
                    }

                    function = program[newFunction];
                    line = 0;
                    continue;
                }
            }
            line++;
        }

        // reached the end of instructions
        return Result.DEPTH_LIMIT;
    }

    public static List<byte[][]> createAllPossiblePrograms(Game game, Command[] commands, boolean conditions, int[] functions) {
        List<byte[][]> programs = new ArrayList<>();

        byte[] allInstructions = InstructionByte.createAllPossibleInstructions(commands, conditions, functions.length);

        List<List<byte[]>> programStarts = new ArrayList<>();
        programStarts.add(new ArrayList<>());

        for (int i = 0; i < functions.length; i++) {
            int size = functions[i];
            List<byte[]> allFunctions = FunctionArray.createAllPossibleFunctions(game, i, size, allInstructions);

            // save current function starts and create a new list for next cycle
            List<List<byte[]>> currentProgramStarts = programStarts;
            programStarts = new ArrayList<>();

            for (List<byte[]> functionStart : currentProgramStarts) {
                for (byte[] function : allFunctions) {
                    programStarts.add(appendFunction(functionStart, function));
                }
            }
        }

        for (List<byte[]> program : programStarts) {
            byte[][] programArray = new byte[program.size()][];
            for (int i = 0; i < programArray.length; i++) {
                programArray[i] = program.get(i);
            }
            if (canWin(game, programArray)) {
                programs.add(programArray);
            }
        }

        return programs;
    }

    private static boolean canWin(Game game, byte[][] program) {
        List<Integer> reachableFunctions = new ArrayList<>();
        reachableFunctions.add(0);

        boolean canMove = false;
        boolean canTurn = false;
        for (int i = 0; i < reachableFunctions.size(); i++) {
            for (byte instruction : program[reachableFunctions.get(i)]) {
                Command command = InstructionByte.getCommand(instruction);
                if (command == Command.MOVE) {
                    canMove = true;
                }
                if (command == Command.TURN) {
                    canTurn = true;
                }
                if (command == Command.FUNCTION) {
                    int function = InstructionByte.getFunction(instruction);
                    if (!reachableFunctions.contains(function)) {
                        reachableFunctions.add(function);
                    }
                }
            }
        }

        if (!canTurn) {
            for (long star : game.board().copyStars()) {
                int x = Pos.x(star);
                int y = Pos.y(star);
                switch (game.direction()) {
                    case UP -> {
                        if (x != game.x() || y < game.y()) {
                            return false;
                        }
                    }
                    case RIGHT -> {
                        if (x < game.x() || y != game.y()) {
                            return false;
                        }
                    }
                    case DOWN -> {
                        if (x != game.x() || y > game.y()) {
                            return false;
                        }
                    }
                    case LEFT -> {
                        if (x > game.x() || y != game.y()) {
                            return false;
                        }
                    }
                }
            }
        }

        return canMove;
    }

    private static List<byte[]> appendFunction(List<byte[]> functions, byte[] function) {
        functions = new ArrayList<>(functions);
        functions.add(function);
        return functions;
    }
}

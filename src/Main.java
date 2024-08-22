import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        Color[][] board = createBoard();

        List<Instruction> allInstructions = Instruction.createAllPossibleInstruction();
        System.out.println("Possible Instructions: " + allInstructions.size());

        List<List<Instruction>> allInstructionLists = new ArrayList<>();

        for (Instruction i0 : allInstructions) {
            for (Instruction i1 : allInstructions) {
                for (Instruction i2 : allInstructions) {
                    for (Instruction i3 : allInstructions) {
                        for (Instruction i4 : allInstructions) {
                            for (Instruction i5 : allInstructions) {
                                List<Instruction> instructions = new ArrayList<>();
                                instructions.add(i0);
                                instructions.add(i1);
                                instructions.add(i2);
                                instructions.add(i3);
                                instructions.add(i4);
                                instructions.add(i5);
                                allInstructionLists.add(instructions);
                            }
                        }
                    }
                }
            }
        }

        System.out.println("All Instruction Lists: " + allInstructionLists.size());

        allInstructionLists.removeIf(Main::cannotWin);

        System.out.println("Filtered Instruction Lists: " + allInstructionLists.size());

        for (int i = 0; i < allInstructionLists.size(); i++) {
            List<Instruction> instructions = allInstructionLists.get(i);
            if (execute(board, instructions, 5000)) {
                System.out.println("Won with the following instructions: " + instructions);
                return;
            }
            if (i % 100_000 == 0) {
                System.out.println("Tried " + i + "/" + allInstructionLists.size() + " instructions");
            }
        }
        System.out.println("No winning instructions possible!");
    }

    private static Color[][] createBoard() {
        Color[][] board = new Color[7][14];
        board[1][1] = Color.RED;
        board[1][2] = Color.RED;
        board[1][3] = Color.RED;
        board[1][4] = Color.RED;
        board[1][5] = Color.RED;
        board[1][6] = Color.RED;
        // blue square
        board[1][7] = Color.BLUE;
        board[1][8] = Color.RED;
        board[2][8] = Color.RED;
        board[2][9] = Color.RED;
        board[3][9] = Color.RED;
        board[3][10] = Color.RED;
        board[4][10] = Color.RED;
        board[4][11] = Color.RED;
        board[5][11] = Color.RED;
        board[5][12] = Color.RED;
        board[6][12] = Color.RED;
        // winning square
        board[6][13] = Color.RED;
        return board;
    }

    private static Color[][] createPossibleBoard() {
        Color[][] board = new Color[7][14];
        board[1][1] = Color.RED;
        board[1][2] = Color.RED;
        board[1][3] = Color.RED;
        board[1][4] = Color.RED;
        board[1][5] = Color.RED;
        board[1][6] = Color.RED;
        // formerly blue square
        board[1][7] = Color.RED;
        board[1][8] = Color.GREEN;
        board[2][8] = Color.BLUE;
        board[2][9] = Color.GREEN;
        board[3][9] = Color.BLUE;
        board[3][10] = Color.GREEN;
        board[4][10] = Color.BLUE;
        board[4][11] = Color.GREEN;
        board[5][11] = Color.BLUE;
        board[5][12] = Color.GREEN;
        board[6][12] = Color.BLUE;
        // winning square
        board[6][13] = Color.RED;
        return board;
    }

    private static boolean cannotWin(List<Instruction> instructions) {
        for (Instruction instruction : instructions) {
            if (instruction.command() == Command.FORWARD) {
                return false;
            }
        }
        return true;
    }

    private static boolean execute(Color[][] board, List<Instruction> instructions, int limitCycles) {
        int x = 1;
        int y = 1;
        Direction direction = Direction.UP;

        int i = 0;
        int cycles = 0;
        while (i < instructions.size()) {
            cycles++;
            if (cycles > limitCycles) {
                // exceeded cycles limit
                return false;
            }

            Instruction instruction = instructions.get(i);
            Color condition = instruction.condition();
            if (condition != null && condition != board[x][y]) {
                i++;
                continue;
            }
            Command command = instruction.command();
            switch (command) {
                case FORWARD -> {
                    switch (direction) {
                        case UP -> y++;
                        case DOWN -> y--;
                        case RIGHT -> x++;
                        case LEFT -> x--;
                    }
                    if (board[x][y] == null) {
                        // left viable squares
                        return false;
                    }
                    if (x == 6 && y == 13) {
                        // reached winning square
                        return true;
                    }
                }
                case LEFT -> {
                    direction = Direction.rotate(direction, true);
                }
                case RIGHT -> {
                    direction = Direction.rotate(direction, false);
                }
                case F0 -> {
                    i = 0;
                    continue;
                }
            }
            i++;
        }
        // reached the end of instructions
        return false;
    }
}
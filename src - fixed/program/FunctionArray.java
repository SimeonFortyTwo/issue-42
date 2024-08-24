package program;

import board.Color;
import board.Game;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FunctionArray {
    public static List<byte[]> createAllPossibleFunctions(Game game, int index, int size, byte[] allInstructions) {
        List<byte[]> functions = new ArrayList<>();

        List<byte[]> functionStarts = new ArrayList<>();
        functionStarts.add(new byte[0]);

        byte loopInstruction = InstructionByte.encodeFunction(null, index);

        for (int i = 0; i < size; i++) {
            // save current function starts and create a new list for next cycle
            List<byte[]> currentFunctionStarts = functionStarts;
            functionStarts = new ArrayList<>();

            for (byte[] functionStart : currentFunctionStarts) {
                for (byte instruction : allInstructions) {
                    if (isInstructionInefficient(functionStart, instruction)) {
                        continue;
                    }
                    byte[] function = appendInstruction(functionStart, instruction);
                    if (index == 0 && isMainFunctionLosing(game, function)) {
                        continue;
                    }
                    if (instruction == loopInstruction) {
                        // loops on itself unconditionally
                        // instructions after that are unreachable anyway
                        functions.add(function);
                        continue;
                    }

                    functionStarts.add(function);
                }
            }
        }

        functions.addAll(functionStarts);

        return functions;
    }

    private static byte[] appendInstruction(byte[] functionStart, byte instruction) {
        byte[] function = Arrays.copyOf(functionStart, functionStart.length + 1);
        function[function.length - 1] = instruction;
        return function;
    }

    private static boolean isInstructionInefficient(byte[] function, byte instruction) {
        Command command = InstructionByte.getCommand(instruction);
        Color condition = InstructionByte.getCondition(instruction);

        if (function.length == 0 && command == Command.FUNCTION && condition == null) {
            // unconditionally calling another function instantly is inefficient
            return true;
        }

        for (int j = function.length - 1; j >= 0; j--) {
            byte earlierInstruction = function[j];
            Command earlierCommand = InstructionByte.getCommand(earlierInstruction);
            Color earlierCondition = InstructionByte.getCondition(earlierInstruction);
            if (earlierCommand == Command.FUNCTION || earlierCommand == Command.MOVE) {
                // functions and moving interrupt the flow of analysis
                // since the state can change unpredictably
                return false;
            }
            if (earlierCommand == Command.PAINT) {
                if (earlierCondition == null && condition != null) {
                    // if the earlier instruction is guaranteed to have painted,
                    // conditions are now deterministic and as such only one working one needs to pass
                    return true;
                }
                if (earlierCondition == condition && condition != null) {
                    // if the earlier instruction has the same condition and then repainted,
                    // this instructions outcome is now deterministic
                    // it is either a NO_OP or guaranteed, so only one working one needs to pass
                    return true;
                }
                // color changes interrupt the flow of analysis
                return false;
            }
            if (earlierCondition == condition && earlierCommand == Command.TURN && command == Command.TURN) {
                if (InstructionByte.isLeft(earlierInstruction) != InstructionByte.isLeft(instruction)) {
                    // skip functions that have redundant LEFT-RIGHT or RIGHT-LEFT instructions
                    return true;
                }
                if (InstructionByte.isLeft(earlierInstruction) && InstructionByte.isLeft(instruction)) {
                    // skip functions that have LEFT-LEFT instructions,
                    // RIGHT-RIGHT instructions will be passed and do the same thing
                    return true;
                }
            }
        }
        return false;
    }

    private static boolean isMainFunctionLosing(Game game, byte[] function) {
        return ProgramArray.execute(new byte[][]{
                function
        }, game, 25, 10) == Result.LOST;
    }
}

package program;

import board.Color;

import java.util.ArrayList;
import java.util.List;

public class InstructionByte {
    public static byte encode(Command command, Color condition) {
        return (byte) (command.ordinal() | encodeColor(condition) << 2);
    }

    public static byte encodeTurn(Color condition, boolean left) {
        return (byte) (encode(Command.TURN, condition) | (left ? 1 : 0) << 4);
    }

    public static byte encodePaint(Color condition, Color color) {
        return (byte) (encode(Command.PAINT, condition) | encodeColor(color) << 4);
    }

    public static byte encodeFunction(Color condition, int function) {
        return (byte) (encode(Command.FUNCTION, condition) | function << 4);
    }

    public static Command getCommand(byte instruction) {
        return Command.values()[instruction & 3];
    }

    public static Color getCondition(byte instruction) {
        return decodeColor((byte) ((instruction >> 2) & 3));
    }

    public static boolean isLeft(byte instruction) {
        return ((instruction >> 4) & 1) == 1;
    }

    public static Color getColor(byte instruction) {
        return decodeColor((byte) ((instruction >> 4) & 3));
    }

    public static int getFunction(byte instruction) {
        return (instruction >> 4) & 7;
    }

    private static byte encodeColor(Color color) {
        return color != null ?
                (byte) (color.ordinal() + 1) :
                (byte) 0;
    }

    private static Color decodeColor(byte i) {
        return i != 0 ?
                Color.values()[i - 1] :
                null;
    }

    public static byte[] createAllPossibleInstructions(Command[] commands, boolean conditions, int functions) {
        List<Byte> instructions = new ArrayList<>();
        for (Command command : commands) {
            switch (command) {
                case PAINT -> {
                    for (Color color : Color.values()) {
                        instructions.add(encodePaint(null, color));
                        if (!conditions) {
                            continue;
                        }
                        for (Color condition : Color.values()) {
                            if (color == condition) {
                                // if color and condition are equal,
                                // the instruction is a NO_OP
                                continue;
                            }
                            instructions.add(encodePaint(condition, color));
                        }
                    }
                }
                case FUNCTION -> {
                    for (int i = 0; i < functions; i++) {
                        instructions.add(encodeFunction(null, i));
                        if (!conditions) {
                            continue;
                        }
                        for (Color condition : Color.values()) {
                            instructions.add(encodeFunction(condition, i));
                        }
                    }
                }
                case TURN -> {
                    for (int i = 0; i < functions; i++) {
                        instructions.add(encodeTurn(null, true));
                        instructions.add(encodeTurn(null, false));
                        if (!conditions) {
                            continue;
                        }
                        for (Color condition : Color.values()) {
                            instructions.add(encodeTurn(condition, true));
                            instructions.add(encodeTurn(condition, false));
                        }
                    }
                }
                default -> {
                    instructions.add(encode(command, null));
                    if (!conditions) {
                        continue;
                    }
                    for (Color condition : Color.values()) {
                        instructions.add(encode(command, condition));
                    }
                }
            }
        }
        byte[] instructionArray = new byte[instructions.size()];
        for (int i = 0; i < instructionArray.length; i++) {
            instructionArray[i] = instructions.get(i);
        }
        return instructionArray;
    }
}

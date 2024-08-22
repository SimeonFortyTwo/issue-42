import java.util.ArrayList;
import java.util.List;

public record Instruction(Command command, Color condition) {

    public static List<Instruction> createAllPossibleInstruction() {
        List<Instruction> instructions = new ArrayList<>();
        for (Command command : Command.values()) {
            instructions.add(new Instruction(command, null));
            for (Color condition : Color.values()) {
                instructions.add(new Instruction(command, condition));
            }
        }
        return instructions;
    }

    @Override
    public String toString() {
        return "Instruction{" +
                "command=" + command +
                ", condition=" + condition +
                '}';
    }
}
